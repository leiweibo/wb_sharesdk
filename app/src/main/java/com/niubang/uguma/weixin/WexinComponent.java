package com.niubang.uguma.weixin;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.niubang.uguma.BaseComponent;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import static com.tencent.mm.sdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

/**
 * 微信登录和分享的组件
 * Created by leiweibo on 12/15/16.
 */

public class WexinComponent extends BaseComponent {

  private Context context;
  IWXAPI wxapi;
  public WexinComponent(Context context) {
    this.context = context;
    this.wxapi = WXAPIFactory.createWXAPI(context, Constants.APP_ID, true);
  }

  /**
   * 微信的登录功能，要求用户手机必须安装微信客户端
   */
  public void login() {

    wxapi.registerApp(Constants.APP_ID);

    if (wxapi != null && wxapi.isWXAppInstalled()) {
      SendAuth.Req req = new SendAuth.Req();
      req.scope = "snsapi_userinfo";
      req.state = "wechat_sdk_demo_test_neng";
      wxapi.sendReq(req);
    } else {
      Toast.makeText(context, "用户未安装微信", Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * 发送到微信功能，分享内容包括，要求用户手机必须安装微信客户端
   */
  public void share() {
    WXTextObject textObject = new WXTextObject();
    textObject.text = "这个是来自于微信分享的SDK";

    WXMediaMessage msg = new WXMediaMessage();
    msg.mediaObject = textObject;
    msg.description = "这个是来自于微信分享的SDK";

    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = String.valueOf(System.currentTimeMillis());
    req.message = msg;

    wxapi.sendReq(req);
  }

  /**
   * 分享到朋友圈
   */
  public void shareToWechat() {
    WXTextObject textObject = new WXTextObject();
    textObject.text = "这个是来自于微信分享的SDK";

    WXMediaMessage msg = new WXMediaMessage();
    msg.mediaObject = textObject;
    msg.description = "这个是来自于微信分享的SDK";

    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = String.valueOf(System.currentTimeMillis());
    req.scene = WXSceneTimeline;
    req.message = msg;

    wxapi.sendReq(req);

  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {

  }

  @Override protected String getSource() {
    return com.niubang.uguma.Constants.BIND_SOURCE_WEIXIN;
  }
}
