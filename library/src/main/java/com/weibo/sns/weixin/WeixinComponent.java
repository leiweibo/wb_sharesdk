package com.weibo.sns.weixin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weibo.sns.LoginCallback;
import com.weibo.sns.SharePlatformConfig;
import com.weibo.sns.Util;

import static com.tencent.mm.sdk.modelmsg.SendMessageToWX.Req.WXSceneSession;

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

public class WeixinComponent extends WeixinBaseComponent {

  private static WeixinComponent instance;

  private WeixinComponent(Context context) {
    this.context = context;
    this.wxapi = WXAPIFactory.createWXAPI(context, SharePlatformConfig.getWeixinAppKey(), true);
  }

  /**
   * 特殊的单例模式，之所以做成单例，是因为微信回调的时候，需要跟component进行通信，
   * 但是因为微信的特殊性，它发送了一个请求之后的事情都交给WXEntryActivity去处理，
   * 为了能保持代码一致性，在登录的时候，把接口注入，所以这里采用单例。为了不让这个单例一直持有
   * 某一个activity的引用，所以判断getInstance里面传过来的context跟已存在的context不相等的时候，
   * 重新new一个实例
   */
  public static WeixinComponent getInstance(Context context1) {
    if (instance == null) {
      instance = new WeixinComponent(context1);
    } else if ((context != null) && context !=  context1) {
      context = context1;
    }
    return instance;
  }

  /**
   * 微信的登录功能，要求用户手机必须安装微信客户端
   */
  public void login(LoginCallback callback) {
    if (wxapi != null && wxapi.isWXAppInstalled()) {
      Util.showProgressDialog(context, "登录中");
      wxapi.registerApp(SharePlatformConfig.getWeixinAppKey());
      SendAuth.Req req = new SendAuth.Req();
      req.scope = "snsapi_userinfo";
      req.state = "wechat_sdk_demo_test_neng";
      wxapi.sendReq(req);
    } else {
      Toast.makeText(context, "用户未安装微信", Toast.LENGTH_SHORT).show();
    }
    this.loginCallback = callback;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {

  }

  @Override protected String getSource() {
    return com.weibo.sns.Constants.BIND_SOURCE_WEIXIN;
  }

  /**
   * 网络图片地址分享图片到微信
   *
   * @param imageUrl 图片的链接
   */
  @Override public void shareImage(final String imageUrl) {
    shareImage(imageUrl, WXSceneSession);
  }

  /**
   * 本地图片分享图片到微信
   *
   * @param bitmap 本地图片被转化bitmap
   */
  @Override public void shareImage(Bitmap bitmap) {
    shareImage(bitmap, WXSceneSession);
  }

  /**
   * 分享图文内容到微信，图片为远程图片
   *
   * @param title 标题
   * @param summary 概要
   * @param targetUrl url地址
   * @param imageUrl 图片的url
   */
  @Override public void shareContent(final String title, final String summary,
      final String targetUrl, final String imageUrl) {
    shareContent(title, summary, targetUrl, imageUrl, WXSceneSession);
  }

  /**
   * 分享图文内容到微信，图片为本地图片
   *
   * @param title 标题
   * @param summary 概要
   * @param targetUrl url地址
   * @param bitmap 图片的bitmap
   */
  @Override public void shareContent(String title, String summary, String targetUrl,
      Bitmap bitmap) {
    shareContent(title, summary, targetUrl, bitmap, WXSceneSession);
  }
}
