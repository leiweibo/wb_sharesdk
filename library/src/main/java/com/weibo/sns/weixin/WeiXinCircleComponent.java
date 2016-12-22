package com.weibo.sns.weixin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weibo.sns.Constants;
import com.weibo.sns.LoginCallback;
import com.weibo.sns.SharePlatformConfig;

import static com.tencent.mm.sdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

/**
 * Created by leiweibo on 12/22/16.
 */

public class WeiXinCircleComponent extends WeixinBaseComponent {
  private static WeiXinCircleComponent instance;

  private WeiXinCircleComponent(Context context) {
    this.context = context;
    this.wxapi = WXAPIFactory.createWXAPI(context, SharePlatformConfig.getWeixinAppKey(), true);
  }

  /**
   * 单例模式，之所以做成单例，是因为微信回调的时候，需要跟component进行通信，
   * 但是因为微信的特殊性，它发送了一个请求之后的事情都交给WXEntryActivity去处理，
   * 为了能保持代码一致性，在登录的时候，把接口注入，所以这里采用单例。
   */
  public static WeiXinCircleComponent getInstance(Context context) {
    if (instance == null) {
      instance = new WeiXinCircleComponent(context);
    }
    return instance;
  }

  /**
   * 不需要实现
   * @param callback
   */
  @Deprecated @Override public void login(LoginCallback callback) {

  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {

  }

  @Override protected String getSource() {
    return Constants.BIND_SOURCE_WEIXIN_CIRCLE;
  }

  @Override public void shareImage(String imageUrl) {
    shareImage(imageUrl, WXSceneTimeline);
  }

  @Override public void shareImage(Bitmap bitmap) {
    shareImage(bitmap, WXSceneTimeline);
  }

  @Override
  public void shareContent(String title, String summary, String targetUrl, String imageUrl) {
    shareContent(title, summary, targetUrl, imageUrl, WXSceneTimeline);
  }

  @Override public void shareContent(String title, String summary, String targetUrl, Bitmap image) {
    shareContent(title, summary, targetUrl, image, WXSceneTimeline);
  }
}
