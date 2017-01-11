package com.weibo.sns.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.tencent.connect.UserInfo;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.weibo.sns.BaseComponent;
import com.weibo.sns.Constants;
import com.weibo.sns.DiskCacheUtil;
import com.weibo.sns.LoginCallback;
import com.weibo.sns.R;
import com.weibo.sns.ServiceFactory;
import com.weibo.sns.SharePlatformConfig;
import com.weibo.sns.UserInfoResponse;
import com.weibo.sns.Util;
import com.weibo.sns.qq.network.QQApiService;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN;
import static com.tencent.connect.common.Constants.PARAM_EXPIRES_IN;
import static com.tencent.connect.common.Constants.PARAM_OPEN_ID;
import static com.tencent.connect.common.Constants.REQUEST_API;
import static com.tencent.connect.common.Constants.REQUEST_LOGIN;
import static com.tencent.connect.common.Constants.REQUEST_QQ_SHARE;

/**
 * QQ分享和登录组件,使用的是QQ SDK 3.1.0
 * Created by leiweibo on 12/16/16.
 */

public class QQComponent extends BaseComponent {
  private static final String SCOPE = "all";
  private static final String PARAM_ICON_URL = "figureurl_qq_2";
  private static final String PARAM_NICK_NAME = "nickname";

  private Context context;
  private Tencent tencent;
  private IUiListener qqRequestListener;

  public QQComponent(Context context) {
    super();
    this.context = context;
    this.tencent = Tencent.createInstance(SharePlatformConfig.getQQAppId(), context);
  }

  /**
   * QQ登录，登录完成之后，返回 screen_name， profile_image_url， openid字段
   */
  public void login(LoginCallback callback) {
    qqRequestListener = buildQQListener(new QQListenerCallback() {
      @Override public void doComplete(JSONObject object) {
        initOpenidAndToken(object);
        getUserInfo();
      }
    }, false);

    if (!tencent.isSessionValid()) {
      Util.showProgressDialog(context, "登录中");
      tencent.login((Activity) context, SCOPE, qqRequestListener);
    } else {
      tencent.logout(context);
      tencent.login((Activity) context, SCOPE, qqRequestListener);
    }

    this.loginCallback = callback;
  }

  /**
   * 构建UIListener，用于登录或者API请求的时候
   *
   * @return 回调监听
   */
  private IUiListener buildQQListener(final QQListenerCallback callback, final boolean showToast) {
    IUiListener loginListener = new IUiListener() {
      @Override public void onComplete(Object response) {
        if (null == response) {
          Util.showResultDialog(context, "返回为空", "授权失败");
          return;
        }
        JSONObject jsonResponse = (JSONObject) response;
        if (jsonResponse.length() == 0) {
          Util.showResultDialog(context, "返回为空", "授权失败");
          return;
        }
        if (showToast) {
          Util.toastMessage(context, R.string.authorize_success_full);
        }
        if (callback != null) {
          callback.doComplete((JSONObject) response);
        }
        Util.dismissProgressDialog();
      }

      @Override public void onError(UiError uiError) {
        Util.toastMessage(context, R.string.authorize_failed_full);
        Log.e("QQComponent", uiError.errorDetail + "");
        Util.dismissProgressDialog();
      }

      @Override public void onCancel() {
        Util.toastMessage(context, R.string.authorize_cancel_full);
        Util.dismissProgressDialog();
      }
    };
    return loginListener;
  }

  /**
   * 将登录成功之后的token和openid设置到tencent里面去
   */
  private void initOpenidAndToken(JSONObject jsonObject) {
    try {
      String token = jsonObject.getString(PARAM_ACCESS_TOKEN);
      String expires = jsonObject.getString(PARAM_EXPIRES_IN);
      String openId = jsonObject.getString(PARAM_OPEN_ID);
      if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
        tencent.setAccessToken(token, expires);
        tencent.setOpenId(openId);
      }
    } catch (Exception e) {
    }
  }

  /**
   * 拉去用户信息
   */
  private void getUserInfo() {
    if (tencent != null && tencent.isSessionValid()) {
      UserInfo userInfo = new UserInfo(context, tencent.getQQToken());
      qqRequestListener = buildQQListener(new QQListenerCallback() {
        @Override public void doComplete(JSONObject object) {
          doGetUserInfoComplete(object);
        }
      }, true);
      userInfo.getUserInfo(qqRequestListener);
    }
  }

  private void doGetUserInfoComplete(Object response) {

    JSONObject jsonObject = (JSONObject) response;
    try {
      if (jsonObject != null) {
        final UserInfoResponse userInfoResponse =
            new UserInfoResponse(getSource(), tencent.getOpenId(),
                jsonObject.getString(PARAM_NICK_NAME), jsonObject.getString(PARAM_ICON_URL));

        QQApiService qqApiService = ServiceFactory.getQQApiService();
        Map<String, String> map = new HashMap<>();
        map.put("access_token", tencent.getAccessToken());
        map.put("unionid", "1");
        qqApiService.getUnionId(map)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(new Observer<ResponseBody>() {
              @Override public void onCompleted() {
                Util.dismissProgressDialog();
              }

              @Override public void onError(Throwable e) {
                Util.dismissProgressDialog();
                Toast.makeText(context, "获取用户信息失败，请重试", Toast.LENGTH_LONG).show();
              }

              @Override public void onNext(ResponseBody response) {
                try {
                  JSONObject jsonObject = com.tencent.open.utils.Util.parseJson(response.string());
                  String unionId = jsonObject.getString("unionid");
                  if (loginCallback != null) {
                    userInfoResponse.setUnionId(unionId);
                    loginCallback.onComplete(userInfoResponse);
                  }
                } catch (IOException e) {
                  e.printStackTrace();
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            });
      }
    } catch (JSONException e) {
      e.printStackTrace();
    } finally {
      Util.dismissProgressDialog();
    }
  }

  /**
   * 在某些低端机上调用登录后，由于内存紧张导致APP被系统回收，登录成功后无法成功回传数据。解决办法如下
   * 在调用login的Activity或者Fragment重写onActivityResult方法
   *
   * @param requestCode 请求code
   * @param resultCode 返回的结构的code
   * @param data data
   */
  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_LOGIN || requestCode == REQUEST_API) {
      tencent.onActivityResultData(requestCode, resultCode, data, qqRequestListener);
    } else if (requestCode == REQUEST_QQ_SHARE) {
      Tencent.onActivityResultData(requestCode, resultCode, data, buildShareListener());
    }
  }

  @Override protected String getSource() {
    return Constants.BIND_SOURCE_QQ;
  }

  /**
   * 纯图片发送，目前支持本地图片分享
   *
   * @param imageUrl 图片的链接
   */
  @Override public void shareImage(String imageUrl) {
    Toast.makeText(context, "纯图片发送，目前支持本地图片分享", Toast.LENGTH_SHORT).show();
  }

  /**
   * 分享本地图片到QQ
   */
  @Override public void shareImage(Bitmap image) {
    if (image != null) {
      Observable.just(image)
          .observeOn(Schedulers.io())
          .subscribeOn(AndroidSchedulers.mainThread())
          .map(new Func1<Bitmap, String>() {
            @Override public String call(Bitmap bitmap) {
              try {
                DiskCacheUtil diskCacheUtil = new DiskCacheUtil(context);
                File file =
                    diskCacheUtil.writeLocalBitMapToCache(bitmap, System.currentTimeMillis() + "");
                return file.getAbsolutePath();
              } catch (IOException e) {
                e.printStackTrace();
              }
              return null;
            }
          })
          .subscribe(new Action1<String>() {
            @Override public void call(String path) {
              if (path != null) {
                final Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path);

                tencent.shareToQQ((Activity) context, params, buildShareListener());
              }
            }
          });
    }
  }

  /**
   * 分享远程图片到QQ
   *
   * @param title 标题
   * @param summary 概要
   * @param targetUrl 网址
   * @param image 图片url
   */
  @Override public void shareContent(String title, String summary, String targetUrl, String image) {
    if (TextUtils.isEmpty(image)) {
      Bitmap bitmap = Util.getDefaultBitmap(context);
      shareContent(title, summary, targetUrl, bitmap);
    } else {
      final Bundle params = new Bundle();
      params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
      params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
      params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
      params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
      params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, image);

      tencent.shareToQQ((Activity) context, params, buildShareListener());
    }
  }

  /**
   * 本地图片分享到QQ
   *
   * @param title 标题
   * @param summary 概要
   * @param targetUrl 网址
   * @param image 本地图片bitmap对象
   */
  @Override public void shareContent(final String title, final String summary,
      final String targetUrl, final Bitmap image) {

    if (image != null) {

      Observable.just(image)
          .observeOn(Schedulers.io())
          .subscribeOn(AndroidSchedulers.mainThread())
          .map(new Func1<Bitmap, String>() {
            @Override public String call(Bitmap bitmap) {
              try {
                DiskCacheUtil diskCacheUtil = new DiskCacheUtil(context);
                File file =
                    diskCacheUtil.writeLocalBitMapToCache(bitmap, System.currentTimeMillis() + "");
                return file.getAbsolutePath();
              } catch (IOException e) {
                e.printStackTrace();
              }
              return null;
            }
          })
          .subscribe(new Action1<String>() {
            @Override public void call(String path) {
              if (path != null) {
                final Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path);

                tencent.shareToQQ((Activity) context, params, buildShareListener());
              }
            }
          });
    } else {
      final Bundle params = new Bundle();
      params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
      params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
      params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
      params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);

      tencent.shareToQQ((Activity) context, params, buildShareListener());
    }
  }

  private IUiListener buildShareListener() {
    IUiListener uiListener = new IUiListener() {
      @Override public void onComplete(Object o) {
        Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
      }

      @Override public void onError(UiError uiError) {
        ((Activity) context).runOnUiThread(new Runnable() {
          @Override public void run() {
            Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
          }
        });
      }

      @Override public void onCancel() {
        Toast.makeText(context, "分享取消", Toast.LENGTH_SHORT).show();
      }
    };
    return uiListener;
  }
}
