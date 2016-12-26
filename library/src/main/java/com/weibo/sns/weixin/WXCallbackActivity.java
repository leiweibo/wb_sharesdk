package com.weibo.sns.weixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.weibo.sns.Constants;
import com.weibo.sns.LoginCallback;
import com.weibo.sns.R;
import com.weibo.sns.SharePlatformConfig;
import com.weibo.sns.UserInfoResponse;
import com.weibo.sns.weixin.models.AccessTokenResponse;
import com.weibo.sns.ServiceFactory;
import com.weibo.sns.weixin.models.WeiXinRawUserInfoResponse;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by leiweibo on 12/16/16.
 */

public class WXCallbackActivity extends Activity implements IWXAPIEventHandler {

  private IWXAPI api;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    api = WXAPIFactory.createWXAPI(this, SharePlatformConfig.getWeixinAppKey(), true);
    api.handleIntent(this.getIntent(), this);
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    api.handleIntent(intent, this);
  }

  /**
   * 当微信发送请求到你的应用，将通过IWXAPIEventHandler接口的onReq方法进行回调
   */
  @Override public void onReq(BaseReq baseReq) {
    Log.e("WX-SNS", "onReq");
  }

  /**
   * 应用请求微信的响应结果将通过onResp回调。
   */
  @Override public void onResp(BaseResp resp) {

    if (resp instanceof SendAuth.Resp) {
      startRxCall(((SendAuth.Resp) resp).code);
    } else {
      String result = "";

      switch (resp.errCode) {
        case BaseResp.ErrCode.ERR_OK:
          result ="分享成功";
          break;
        case BaseResp.ErrCode.ERR_USER_CANCEL:
          result = "分享取消";
          break;
        case BaseResp.ErrCode.ERR_AUTH_DENIED:
          result = "分享拒绝";
          break;
        default:
          result = "分享错误";
          break;
      }

      Toast.makeText(this, result, Toast.LENGTH_LONG).show();

      finish();
    }
  }

  /**
   * 通过RxJava去调用获取token，然后去获取用户信息
   * @param code 登录认证获取的code，用来换取token
   */
  private void startRxCall(String code) {
    Map<String, String> params = new HashMap();
    params.put("appid", SharePlatformConfig.getWeixinAppKey());
    params.put("secret", SharePlatformConfig.getWeixinSceret());
    params.put("code", code);
    params.put("grant_type", "authorization_code");
    ServiceFactory.getWeixinApiService()
        .getToken(params)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .flatMap(new Func1<AccessTokenResponse, Observable<WeiXinRawUserInfoResponse>>() {
          @Override public Observable<WeiXinRawUserInfoResponse> call(
              AccessTokenResponse accessTokenResponse) {
            return ServiceFactory.getWeixinApiService()
                .getUserInfo(accessTokenResponse.accessToken, accessTokenResponse.openId);
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<WeiXinRawUserInfoResponse>() {
          @Override public void onCompleted() {
            Log.d("WXCallbackActivity", "onCompleted");
            finish();
          }

          @Override public void onError(Throwable e) {
            Log.e("WXCallbackActivity", e.getMessage());
            finish();
          }

          @Override public void onNext(WeiXinRawUserInfoResponse userInfoResponse) {
            UserInfoResponse userInfo = userInfoResponse.converToUserInfo();
            LoginCallback callback = WeixinComponent.getInstance(WXCallbackActivity.this).getLoginCallback();
            if (callback != null) {
              callback.onComplete(userInfo);
            }
          }
        });
  }
}
