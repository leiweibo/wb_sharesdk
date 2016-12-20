package com.niubang.uguma.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.niubang.uguma.Constants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by leiweibo on 12/16/16.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

  private IWXAPI api;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    api = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APP_ID, true);
    api.handleIntent(this.getIntent(), this);
  }


  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    api.handleIntent(intent, this);
  }

  /**
   *当微信发送请求到你的应用，将通过IWXAPIEventHandler接口的onReq方法进行回调
   * @param baseReq
   */
  @Override public void onReq(BaseReq baseReq) {
    Log.e("WX-SNS", "onReq");
  }

  /**
   * 应用请求微信的响应结果将通过onResp回调。
   * @param baseResp
   */
  @Override public void onResp(BaseResp baseResp) {
    Log.e("WX-SNS", "onResp");

    finish();
  }

}
