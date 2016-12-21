package com.weibo.sns.weixin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;
import com.weibo.sns.BaseComponent;
import com.weibo.sns.LoginCallback;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weibo.sns.SharePlatformConfig;

import static com.tencent.mm.sdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

/**
 * 微信登录和分享的组件
 * * 微信的认证过程比较麻烦，而且也没有提供线程的API来获取用户信息，所以这里自己封装网络请求类去做网络
 * 其过程分为三步：
 * 1. 请求code
 * 2. 通过code换取access_token
 * 3. 通过accessToken访问API
 * 参考：https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN
 *
 * Created by leiweibo on 12/15/16.
 */

public class WeixinComponent extends BaseComponent {

  private Context context;
  private IWXAPI wxapi;

  private static WeixinComponent instance;

  /**
   * 单例模式，之所以做成单例，是因为微信回调的时候，需要跟component进行通信，
   * 但是因为微信的特殊性，它发送了一个请求之后的事情都交给WXEntryActivity去处理，
   * 为了能保持代码一致性，在登录的时候，把接口注入，所以这里采用单例。
   * @param context
   * @return
   */
  public static WeixinComponent getInstance(Context context) {
    if (instance == null) {
      instance = new WeixinComponent(context);
    }
    return instance;
  }

  private WeixinComponent(Context context) {
    this.context = context;
    this.wxapi = WXAPIFactory.createWXAPI(context, SharePlatformConfig.getWeixinAppKey(), true);
  }

  /**
   * 微信的登录功能，要求用户手机必须安装微信客户端
   */
  public void login(LoginCallback callback) {

    wxapi.registerApp(SharePlatformConfig.getWeixinAppKey());

    if (wxapi != null && wxapi.isWXAppInstalled()) {
      SendAuth.Req req = new SendAuth.Req();
      req.scope = "snsapi_userinfo";
      req.state = "wechat_sdk_demo_test_neng";
      wxapi.sendReq(req);
    } else {
      Toast.makeText(context, "用户未安装微信", Toast.LENGTH_SHORT).show();
    }
    this.loginCallback = callback;
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
    return com.weibo.sns.Constants.BIND_SOURCE_WEIXIN;
  }

  @Override public void shareImage(String imageUrl) {

  }

  @Override public void shareImage(Bitmap bitmap) {

  }


  @Override public void shareContent(String title, String summary, String targetUrl, String image) {

  }

  @Override public void shareContent(String title, String summary, String targetUrl, Bitmap image) {

  }
}
