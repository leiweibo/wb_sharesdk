package com.weibo.sns.sina;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.weibo.sns.BaseComponent;
import com.weibo.sns.Constants;
import com.weibo.sns.DiskCacheUtil;
import com.weibo.sns.LoginCallback;
import com.weibo.sns.R;
import com.weibo.sns.ServiceFactory;
import com.weibo.sns.SharePlatformConfig;
import com.weibo.sns.Util;
import com.weibo.sns.sina.models.WeiboRawUserInfoResponse;
import com.weibo.sns.weixin.WeixinComponent;
import java.lang.ref.WeakReference;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 微博分享登录组件
 * Created by leiweibo on 12/14/16.
 */
public class WeiboComponent extends BaseComponent implements IWeiboHandler.Response {
  // 微博微博分享接口实例
  private IWeiboShareAPI weiboShareAPI;

  // 启动微博分享、登录的上下文
  private static Context context;

  // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
  // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
  // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
  // 失败返回 false，不调用上述回调
  private Bundle savedInstance; //savedInstance

  //授权认证所需要的信息
  private AuthInfo authInfo;

  //sso授权认证实例
  private SsoHandler ssoHandler;

  //图片分享的对象
  private Bitmap bitmap = null;

  private static WeakReference<WeiboComponent> weakReference;

  /**
   * WebComponent组件构造函数
   *
   * @param context activity上下文
   * @param savedInsance Activity实例里面的savedInstance
   */
  private WeiboComponent(Context context, Bundle savedInsance) {
    this.context = context;
    this.savedInstance = savedInsance;
    this.authInfo =
        new AuthInfo(context, SharePlatformConfig.getSinaAppKey(), Constants.WEIBO_REDIRECT_URL,
            Constants.WEIBO_SCOPE);
  }

  /**
   * 特殊的单例模式，之所以做成单例，是因为分享回调的时候，需要去调用WBShareCallbackActivity，
   * 在那边获取到这个对应的component，对weibo返回来的intent内容进行处理，为了不让这个单例一直持有
   * 某一个activity的引用，所以判断getInstance里面传过来的context跟已存在的context不相等的时候，
   * 重新new一个实例
   * 这里采用Weakreference防止内存泄漏
   */
  public static WeiboComponent getInstance(Context context1, Bundle savedInstance) {
    if (weakReference == null) {
      WeiboComponent component = new WeiboComponent(context1, savedInstance);
      weakReference = new WeakReference<WeiboComponent>(component);
      return component;
    } else if ((context != null && context1 != context)) {
      context = context1;
    }
    return weakReference.get();
  }

  /**
   * 登录功能，如果本地已经安装微博客户端，那么直接唤起微博客户端进行登录认证，如果没有登录，则通过WebView进行登录
   */
  public void login(final LoginCallback callback) {
    Util.showProgressDialog(context, "登录中");
    //实例化登录认证sso实例
    ssoHandler = new SsoHandler((Activity) context, authInfo);
    //如果实例化登录认证sso成功，则不为空，开始进行认证登录操作
    if (ssoHandler != null) {
      ssoHandler.authorize(buildWeiboListener(new WeiboListenerCallback() {
        @Override public void doComplete() {
          getUserInfo();
        }
      }));
    }

    this.loginCallback = callback;
  }

  /**
   * 构建微博回调监听
   *
   * @param callback 回调
   */
  private WeiboAuthListener buildWeiboListener(final WeiboListenerCallback callback) {
    WeiboAuthListener weiboAuthListener = new WeiboAuthListener() {
      @Override public void onComplete(Bundle bundle) {
        Util.toastMessage(context, R.string.authorize_success_full);
        Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
        if (accessToken != null && accessToken.isSessionValid()) {
          AccessTokenKeeper.writeAccessToken(context.getApplicationContext(), accessToken);
          if (callback != null) {
            callback.doComplete();
          }
          Util.dismissProgressDialog();
        }
      }

      @Override public void onWeiboException(WeiboException e) {
        Util.toastMessage(context, R.string.authorize_failed_full);
        Log.e(getClass().getName(), e.getMessage() + "");
        Util.dismissProgressDialog();
      }

      @Override public void onCancel() {
        Util.toastMessage(context, R.string.authorize_cancel_full);
        Util.dismissProgressDialog();
      }
    };
    return weiboAuthListener;
  }

  /**
   * 网络请求获取用户信息
   */
  private void getUserInfo() {
    Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(context);

    Observable<WeiboRawUserInfoResponse> observable =
        ServiceFactory.getWeiboApiService().getUserInfo(token.getToken(), token.getUid());
    observable.subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(new Observer<WeiboRawUserInfoResponse>() {
          @Override public void onCompleted() {
            Util.dismissProgressDialog();
          }

          @Override public void onError(Throwable e) {
            Util.dismissProgressDialog();
            Toast.makeText(context, "获取用户信息失败，请重试", Toast.LENGTH_LONG).show();
          }

          @Override public void onNext(WeiboRawUserInfoResponse weiboRawUserInfoResponse) {
            if (weiboRawUserInfoResponse != null) {
              Log.e(getClass().getName(), weiboRawUserInfoResponse.converToUserInfo().toString());
              if (loginCallback != null) {
                loginCallback.onComplete(weiboRawUserInfoResponse.converToUserInfo());
              }
            } else {
              Toast.makeText(context, "获取用户信息失败，请重试", Toast.LENGTH_LONG).show();
            }
          }
        });
  }

  /**
   * 登录完成使用的activityResult里面需要添加
   */
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // SSO 授权回调
    // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
    if (ssoHandler != null) {
      ssoHandler.authorizeCallBack(requestCode, resultCode, data);
    }
  }

  @Override protected String getSource() {
    return Constants.BIND_SOURCE_SINA;
  }

  private void checkWeiboShareAPI() {
    if (weiboShareAPI == null) {
      weiboShareAPI = WeiboShareSDK.createWeiboAPI(context, SharePlatformConfig.getSinaAppKey());
      /*
       * 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
       * NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
       */
      weiboShareAPI.registerApp();

      /*
       * 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
       * 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
       * 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
       * 失败返回 false，不调用上述回调
       */
      if (savedInstance != null) {
        weiboShareAPI.handleWeiboResponse(((Activity) context).getIntent(), this);
      }
    }
  }

  @Override public void shareImage(String imageUrl) {
    Observable.just(imageUrl).observeOn(Schedulers.io()).map(new Func1<String, Bitmap>() {
      @Override public Bitmap call(String s) {
        DiskCacheUtil diskCacheUtil = new DiskCacheUtil(context);
        bitmap = diskCacheUtil.getBitmapFromURL(s);
        return bitmap;
      }
    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
      @Override public void call(Bitmap bitmap) {
        shareImage(bitmap);
      }
    });
  }

  @Override public void shareImage(final Bitmap bitmap) {
    checkWeiboShareAPI();
    Oauth2AccessToken accessToken =
        AccessTokenKeeper.readAccessToken(context.getApplicationContext());

    String token = "";
    if (accessToken != null) {
      token = accessToken.getToken();
    }
    WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
    /*
     * 图片内容消息
     */
    ImageObject imageObject = new ImageObject();
    //设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
    imageObject.setImageObject(bitmap);
    weiboMultiMessage.imageObject = imageObject;
    imageObject.description = "这个是description 字段";

    SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
    request.transaction = String.valueOf(System.currentTimeMillis());
    request.multiMessage = weiboMultiMessage;

    //发送分享内容，这里会调用微博提供的分享页面
    weiboShareAPI.sendRequest((Activity) context, request, authInfo, token,
        buildWeiboShareListener());
  }

  @Override
  public void shareContent(final String title, final String summary, final String targetUrl,
      final String image) {
    Observable.just(image).observeOn(Schedulers.io()).map(new Func1<String, Bitmap>() {
      @Override public Bitmap call(String s) {
        DiskCacheUtil diskCacheUtil = new DiskCacheUtil(context);
        Bitmap bitmap = diskCacheUtil.getBitmapFromURL(s);
        return bitmap;
      }
    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
      @Override public void call(Bitmap bitmap) {
        shareContent(title, summary, targetUrl, bitmap);
      }
    });
  }

  @Override public void shareContent(String title, String summary, String targetUrl, Bitmap image) {
    checkWeiboShareAPI();
    Oauth2AccessToken accessToken =
        AccessTokenKeeper.readAccessToken(context.getApplicationContext());

    String token = "";
    if (accessToken != null) {
      token = accessToken.getToken();
    }

    TextObject textObject = new TextObject();
    textObject.text = title + targetUrl;
    textObject.description = summary;

    WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
    weiboMultiMessage.textObject = textObject;

    SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
    request.transaction = String.valueOf(System.currentTimeMillis());
    request.multiMessage = weiboMultiMessage;

    //发送分享内容，这里会调用微博提供的分享页面
    weiboShareAPI.sendRequest((Activity) context, request, authInfo, token,
        buildWeiboShareListener());
  }

  /**
   * 用于微博分享
   * 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
   * 来接收微博客户端返回的数据；执行成功，返回 true，并调用
   * {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
   */
  public void onNewIntent(Intent intent, IWeiboHandler.Response response) {
    weiboShareAPI.handleWeiboResponse(intent, response);
  }

  @Override public void onResponse(BaseResponse baseResp) {
    if (baseResp != null) {
      switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
          Toast.makeText(context, "分享成功", Toast.LENGTH_LONG).show();
          break;
        case WBConstants.ErrorCode.ERR_CANCEL:
          Toast.makeText(context, "分享取消", Toast.LENGTH_LONG).show();
          break;
        case WBConstants.ErrorCode.ERR_FAIL:
          Toast.makeText(context, "分享失败" + "Error Message: " + baseResp.errMsg, Toast.LENGTH_LONG)
              .show();
          break;
      }
    }
  }

  private WeiboAuthListener buildWeiboShareListener() {
    WeiboAuthListener listener = new WeiboAuthListener() {

      @Override public void onWeiboException(WeiboException arg0) {
        if (!bitmap.isRecycled()) {
          bitmap.recycle();
        }
      }

      @Override public void onComplete(Bundle bundle) {
        Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
        AccessTokenKeeper.writeAccessToken(context.getApplicationContext(), newToken);
      }

      @Override public void onCancel() {
        Toast.makeText(context, "分享取消", Toast.LENGTH_LONG).show();
      }
    };

    return listener;
  }
}
