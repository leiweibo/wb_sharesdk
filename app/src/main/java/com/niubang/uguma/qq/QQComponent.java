package com.niubang.uguma.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.niubang.uguma.BaseComponent;
import com.niubang.uguma.Constants;
import com.niubang.uguma.LoginCallback;
import com.niubang.uguma.UserInfoResponse;
import com.niubang.uguma.Util;
import com.tencent.connect.UserInfo;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import static com.tencent.connect.common.Constants.ACTIVITY_OK;
import static com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN;
import static com.tencent.connect.common.Constants.PARAM_EXPIRES_IN;
import static com.tencent.connect.common.Constants.PARAM_OPEN_ID;
import static com.tencent.connect.common.Constants.REQUEST_API;
import static com.tencent.connect.common.Constants.REQUEST_LOGIN;

/**
 * QQ分享和登录组件,使用的是QQ SDK 3.1.0
 * Created by leiweibo on 12/16/16.
 */

public class QQComponent extends BaseComponent {
  private final String SCOPE = "all";
  private final String PARAM_ICON_URL = "figureurl_qq_2";
  private final String PARAM_NICK_NAME = "nickname";

  private Context context;
  private Tencent tencent;
  private IUiListener qqRequestListener;

  public QQComponent(Context context) {
    super();
    this.context = context;
    this.tencent = Tencent.createInstance(Constants.QQ_APP_ID, context);
  }

  /**
   * QQ登录，登录完成之后，返回 screen_name， profile_image_url， openid字段
   */
  public void login(LoginCallback callback) {
    qqRequestListener = buildQQListenr(new QQListenerCallback() {
      @Override public void doComplete(JSONObject object) {
        initOpenidAndToken(object);
        getUserInfo();
      }
    });

    if (!tencent.isSessionValid()) {
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
  private IUiListener buildQQListenr(final QQListenerCallback callback) {
    IUiListener loginListener = new IUiListener() {
      @Override public void onComplete(Object response) {
        if (null == response) {
          Util.showResultDialog(context, "返回为空", "登录失败");
          return;
        }
        JSONObject jsonResponse = (JSONObject) response;
        if (null != jsonResponse && jsonResponse.length() == 0) {
          Util.showResultDialog(context, "返回为空", "登录失败");
          return;
        }

        if (callback != null) {
          callback.doComplete((JSONObject) response);
        }
      }

      @Override public void onError(UiError uiError) {
        Util.toastMessage(context, uiError.errorDetail);
      }

      @Override public void onCancel() {
        Util.toastMessage(context, "取消");
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
      qqRequestListener = buildQQListenr(new QQListenerCallback() {
        @Override public void doComplete(JSONObject object) {
          doGetUserInfoComplete(object);
        }
      });
      userInfo.getUserInfo(qqRequestListener);
    }
  }

  private void doGetUserInfoComplete(Object response) {

    JSONObject jsonObject = (JSONObject) response;
    try {
      if (jsonObject != null) {
        UserInfoResponse userInfoResponse =
            new UserInfoResponse(getSource(), tencent.getOpenId(), jsonObject.getString(PARAM_NICK_NAME),
                jsonObject.getString(PARAM_ICON_URL));

        if(loginCallback != null) {
          loginCallback.onComplete(userInfoResponse);
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
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
    if ((requestCode == REQUEST_LOGIN || requestCode == REQUEST_API) && resultCode == ACTIVITY_OK) {
      tencent.onActivityResultData(requestCode, resultCode, data, qqRequestListener);
    }
  }

  @Override protected String getSource() {
    return Constants.BIND_SOURCE_QQ;
  }

  /**
   * 发送给QQ用户（单向）
   */
  public void shareToQQ() {
    final Bundle params = new Bundle();
    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
    params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
    params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "要分享的摘要");
    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com/news/1.html");
    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
        "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "测试应用222222");
    //params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
    tencent.shareToQQ((Activity) context, params, new IUiListener() {
      @Override public void onComplete(Object o) {

      }

      @Override public void onError(UiError uiError) {

      }

      @Override public void onCancel() {

      }
    });
  }

  /**
   * 分享到QQ空间，目前只支持图文分享
   */
  public void shareToQzone() {
    Bundle params = new Bundle();
    params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
    params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "标题");//必填
    params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "摘要");//选填
    params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://www.uguma.com");//必填
    ArrayList<String> urls = new ArrayList<>();
    urls.add("http://photo.iyaxin.com/attachement/jpg/site2/20111122/001966a9235310358ab51f.jpg");
    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
    tencent.shareToQzone((Activity) context, params, new IUiListener() {
      @Override public void onComplete(Object o) {
        Log.e("weibooo", "onComplete");
      }

      @Override public void onError(UiError uiError) {
        Log.e("weibooo", "onError");
      }

      @Override public void onCancel() {
        Log.e("weibooo", "onCancel");
      }
    });
  }

}
