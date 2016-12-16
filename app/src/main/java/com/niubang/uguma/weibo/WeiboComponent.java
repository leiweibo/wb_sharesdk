package com.niubang.uguma.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;
import com.niubang.uguma.R;
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
import java.text.SimpleDateFormat;

/**
 * 微博分享登录组件
 * Created by leiweibo on 12/14/16.
 */
public class WeiboComponent implements IWeiboHandler.Response {
  // 微博微博分享接口实例
  private IWeiboShareAPI weiboShareAPI;

  // 启动微博分享、登录的上下文
  private Context context;

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

  /**
   * WebComponent组件构造函数
   *
   * @param context activity上下文
   * @param savedInsance Activity实例里面的savedInstance
   */
  public WeiboComponent(Context context, Bundle savedInsance) {
    this.context = context;
    this.savedInstance = savedInsance;
    this.authInfo =
        new AuthInfo(context, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
  }

  /**
   * 分享接口，在用户尚未登录认证的情况，会让用户先进行登录认证，在登录认证的过程中，如果用户终端有安装
   * 微博客户端，那么直接唤起微博客户端；如果没有安装，那么直接打开webview页面进行登录认证
   */
  public void share() {
    if (weiboShareAPI == null) {
      weiboShareAPI = WeiboShareSDK.createWeiboAPI(context, Constants.APP_KEY);

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

    Oauth2AccessToken accessToken =
        AccessTokenKeeper.readAccessToken(context.getApplicationContext());

    String token = "";
    if (accessToken != null) {
      token = accessToken.getToken();
    }


    /*
     * 设置文本内容消息
     */
    WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
    TextObject textObject = new TextObject();
    textObject.text = "This is the content from native weibo share sdk";
    weiboMultiMessage.textObject = textObject;

    /*
     * 图片内容消息
     */
    ImageObject imageObject = new ImageObject();
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inSampleSize = 4;
    options.inPreferredConfig = Bitmap.Config.RGB_565;
    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.beauty, options);
    //设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
    imageObject.setImageObject(bitmap);
    weiboMultiMessage.imageObject = imageObject;

    /*
     * 设置链接内容消息
     */
    WebpageObject mediaObject = new WebpageObject();
    mediaObject.identify = Utility.generateGUID();
    mediaObject.title = "有股吗";
    mediaObject.description = "有股吗炒股";
    // 设置 Bitmap 类型的图片到视频对象里
    // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
    //mediaObject.setThumbImage(bitmap);
    mediaObject.actionUrl = "http://www.uguma.com";
    mediaObject.defaultText = "Webpage 默认文案";
    weiboMultiMessage.mediaObject = mediaObject;

    SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
    request.transaction = String.valueOf(System.currentTimeMillis());
    request.multiMessage = weiboMultiMessage;

    //发送分享内容，这里会调用微博提供的分享页面
    weiboShareAPI.sendRequest((Activity) context, request, authInfo, token,
        new WeiboAuthListener() {

          @Override public void onWeiboException(WeiboException arg0) {
            if (!bitmap.isRecycled()) {
              bitmap.recycle();
            }
          }

          @Override public void onComplete(Bundle bundle) {
            Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
            AccessTokenKeeper.writeAccessToken(context.getApplicationContext(), newToken);
            // Toast.makeText(getApplicationContext(), "onAuthorizeComplete token = " + newToken.getToken(), 0).show();
          }

          @Override public void onCancel() {
          }
        });
  }

  /**
   * 登录功能，如果本地已经安装微博客户端，那么直接唤起微博客户端进行登录认证，如果没有登录，则通过WebView进行登录
   */
  public void login() {
    //实例化登录认证sso实例
    ssoHandler = new SsoHandler((Activity) context, authInfo);
    //如果实例化登录认证sso成功，则不为空，开始进行认证登录操作
    if (ssoHandler != null) {
      ssoHandler.authorize(new WeiboAuthListener() {
        @Override public void onComplete(Bundle bundle) {
          Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
          if (accessToken != null && accessToken.isSessionValid()) {
            String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(accessToken.getExpiresTime()));
            //String format = context.getString(R.string.weibosdk_demo_token_to_string_format_1);
            //mTokenView.setText(String.format(format, accessToken.getToken(), date));

            AccessTokenKeeper.writeAccessToken(context.getApplicationContext(), accessToken);
          }
        }

        @Override public void onWeiboException(WeiboException e) {
          Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override public void onCancel() {
          Toast.makeText(context, "登录取消", Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

  /**
   * 登录完成使用的activityResult里面需要添加
   */
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (ssoHandler != null) {
      ssoHandler.authorizeCallBack(requestCode, resultCode, data);
    }
  }

  /**
   * 用于微博分享
   *从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
   *来接收微博客户端返回的数据；执行成功，返回 true，并调用
   *{@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
   */
  public void onNewIntent(Intent intent) {
    weiboShareAPI.handleWeiboResponse(intent, this);
  }

  @Override public void onResponse(BaseResponse baseResp) {
    if (baseResp != null) {
      switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
          //                    Toast.makeText(this, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
          break;
        case WBConstants.ErrorCode.ERR_CANCEL:
          //                    Toast.makeText(this, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
          break;
        case WBConstants.ErrorCode.ERR_FAIL:
          //                    Toast.makeText(this,
          //                            getString(R.string.weibosdk_demo_toast_share_failed) + "Error Message: " + baseResp.errMsg,
          //                            Toast.LENGTH_LONG).show();
          break;
      }
    }
  }
}
