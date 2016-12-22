package com.niubang.uguma;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.weibo.sns.BaseComponent;
import com.weibo.sns.Constants;
import com.weibo.sns.LoginApi;
import com.weibo.sns.LoginCallback;
import com.weibo.sns.ShareApi;
import com.weibo.sns.SharePlatformConfig;
import com.weibo.sns.UserInfoResponse;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private final String WEIXIN_APP_ID = "wxd07936d8648b4da5";
  private final String WEIXIN_APP_SCRECT = "a7309ef6d60a2127318423cbad300aae";
  private final String WEIBO_APP_KEY = "3616164551";
  private final String QQ_APP_ID = "1105406253";

  private String title; //标题
  private String summary; //概要
  private String content; //内容
  private Bitmap bitmap; //图片 bitmap
  private String imageUrl; //图片url
  private String targetUrl; //分享的url
  private int shareType; //分享类型

  private BaseComponent component;
  private Bundle savedInstance;
  private String platform;
  private boolean login = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.qq_url_1).setOnClickListener(this);
    findViewById(R.id.qq_url_2).setOnClickListener(this);
    findViewById(R.id.qq_img1).setOnClickListener(this);
    findViewById(R.id.qq_img2).setOnClickListener(this);

    findViewById(R.id.qzone_image1).setOnClickListener(this);
    findViewById(R.id.qzone_image2).setOnClickListener(this);
    findViewById(R.id.qzone_url1).setOnClickListener(this);
    findViewById(R.id.qzone_url2).setOnClickListener(this);

    findViewById(R.id.weixin_url1).setOnClickListener(this);
    findViewById(R.id.weixin_url2).setOnClickListener(this);
    findViewById(R.id.weixin_img1).setOnClickListener(this);
    findViewById(R.id.weixin_img2).setOnClickListener(this);

    findViewById(R.id.weibo_share_url1).setOnClickListener(this);
    findViewById(R.id.weibo_share_url2).setOnClickListener(this);
    findViewById(R.id.weibo_share_img1).setOnClickListener(this);
    findViewById(R.id.weibo_share_img2).setOnClickListener(this);

    findViewById(R.id.weixin_circle_url1).setOnClickListener(this);
    findViewById(R.id.weixin_circle_url2).setOnClickListener(this);
    findViewById(R.id.weixin_circle_img1).setOnClickListener(this);
    findViewById(R.id.weixin_circle_img2).setOnClickListener(this);

    findViewById(R.id.weibo_login).setOnClickListener(this);
    findViewById(R.id.weixin_login).setOnClickListener(this);
    findViewById(R.id.qq_login).setOnClickListener(this);

    this.savedInstance = savedInstanceState;

    SharePlatformConfig.setQQ(QQ_APP_ID);
    SharePlatformConfig.setSina(WEIBO_APP_KEY);
    SharePlatformConfig.setWeixin(WEIXIN_APP_ID, WEIXIN_APP_SCRECT);
  }

  @Override public void onClick(View view) {
    login = false;
    switch (view.getId()) {

      case R.id.qzone_url1: //图片为远程
        platform = Constants.BIND_SOURCE_QZONE;
        title = "QQZone分享标题";
        summary = "QQZone分享概要";
        content = "QQZone分享内容";
        imageUrl = "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif";
        targetUrl = "http://www.qq.com/news/1.html";
        shareType = Constants.SHARE_URL_IMG_URL;
        break;

      case R.id.qzone_url2: //图片为本地, 需要把文件存入到本地，然后去做分享
        platform = Constants.BIND_SOURCE_QZONE;
        title = "QQZone分享标题-本地";
        summary = "QQZone分享概要";
        content = "QQZone分享内容";
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        targetUrl = "http://www.qq.com/news/1.html";
        shareType = Constants.SHARE_URL_IMG_LOCAL;
        break;

      case R.id.qzone_image1: //纯图片发送，本地图片
        platform = Constants.BIND_SOURCE_QZONE;
        shareType = Constants.SHARE_IMG_LOCAL;
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        break;

      case R.id.qzone_image2: //纯图片发送，SDK支持
        platform = Constants.BIND_SOURCE_QZONE;
        shareType = Constants.SHARE_IMG_URL;
        imageUrl = "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif";
        break;

      ///////////////////////////////////

      case R.id.qq_url_1: //图片为远程
        platform = Constants.BIND_SOURCE_QQ;
        title = "QQ分享标题";
        summary = "QQ分享概要";
        content = "QQ分享内容";
        imageUrl = "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif";
        targetUrl = "http://www.qq.com/news/1.html";
        shareType = Constants.SHARE_URL_IMG_URL;
        break;

      case R.id.qq_url_2: //图片为本地, 需要把文件存入到本地，然后去做分享
        platform = Constants.BIND_SOURCE_QQ;
        title = "QQ分享标题-本地";
        summary = "QQ分享概要";
        content = "QQ分享内容";
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        targetUrl = "http://www.qq.com/news/1.html";
        shareType = Constants.SHARE_URL_IMG_LOCAL;
        break;

      case R.id.qq_img1: //纯图片发送，本地图片
        platform = Constants.BIND_SOURCE_QQ;
        shareType = Constants.SHARE_IMG_LOCAL;
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        break;

      case R.id.qq_img2: //纯图片发送，SDK不支持
        platform = Constants.BIND_SOURCE_QQ;
        shareType = Constants.SHARE_IMG_URL;
        imageUrl = "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif";
        break;

      case R.id.weixin_img1:
        platform = Constants.BIND_SOURCE_WEIXIN;
        imageUrl = "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif";
        shareType = Constants.SHARE_IMG_URL;
        break;

      case R.id.weixin_img2:
        platform = Constants.BIND_SOURCE_WEIXIN;
        shareType = Constants.SHARE_IMG_LOCAL;
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        break;

      case R.id.weixin_url1:
        platform = Constants.BIND_SOURCE_WEIXIN_CIRCLE;
        title = "WX分享标题";
        summary = "WX分享概要";
        content = "WX分享内容";
        imageUrl = "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif";
        targetUrl = "http://www.qq.com/news/1.html";
        shareType = Constants.SHARE_URL_IMG_URL;
        break;

      case R.id.weixin_url2:
        platform = Constants.BIND_SOURCE_WEIXIN_CIRCLE;
        title = "WX分享标题-本地";
        summary = "WX分享概要";
        content = "WX分享内容";
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        targetUrl = "http://www.qq.com/news/1.html";
        shareType = Constants.SHARE_URL_IMG_LOCAL;
        break;


      case R.id.weixin_circle_img1:
        platform = Constants.BIND_SOURCE_WEIXIN_CIRCLE;
        imageUrl = "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif";
        shareType = Constants.SHARE_IMG_URL;
        break;

      case R.id.weixin_circle_img2:
        platform = Constants.BIND_SOURCE_WEIXIN_CIRCLE;
        shareType = Constants.SHARE_IMG_LOCAL;
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        break;

      case R.id.weixin_circle_url1:
        platform = Constants.BIND_SOURCE_WEIXIN_CIRCLE;
        title = "WX分享标题";
        summary = "WX分享概要";
        content = "WX分享内容";
        imageUrl = "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif";
        targetUrl = "http://www.qq.com/news/1.html";
        shareType = Constants.SHARE_URL_IMG_URL;
        break;

      case R.id.weixin_circle_url2:
        platform = Constants.BIND_SOURCE_WEIXIN;
        title = "WX分享标题-本地";
        summary = "WX分享概要";
        content = "WX分享内容";
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        targetUrl = "http://www.qq.com/news/1.html";
        shareType = Constants.SHARE_URL_IMG_LOCAL;
        break;

      case R.id.weibo_share_img1:
        platform = Constants.BIND_SOURCE_SINA;
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        shareType = Constants.SHARE_IMG_LOCAL;
        break;


      case R.id.weibo_share_img2:
        platform = Constants.BIND_SOURCE_SINA;
        imageUrl = "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif";
        shareType = Constants.SHARE_IMG_URL;
        break;

      case R.id.weibo_share_url1:
        platform = Constants.BIND_SOURCE_SINA;
        title = "Weibo分享标题-图片本地";
        summary = "Weibo分享概要";
        content = "Weibo分享内容";
        targetUrl = "http://www.qq.com/news/1.html";
        shareType = Constants.SHARE_URL_IMG_LOCAL;
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        break;

      case R.id.weibo_share_url2:
        platform = Constants.BIND_SOURCE_SINA;
        title = "Weibo分享标题";
        summary = "Weibo分享概要";
        content = "Weibo分享内容";
        imageUrl = "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif";
        targetUrl = "http://www.qq.com/news/1.html";
        shareType = Constants.SHARE_URL_IMG_URL;
        break;



      /////////////////////////以下是登录

      case R.id.weibo_login:
        login = true;
        this.platform = Constants.BIND_SOURCE_SINA;
        break;

      case R.id.qq_login:
        login = true;
        this.platform = Constants.BIND_SOURCE_QQ;
        break;

      case R.id.weixin_login:
        login = true;
        this.platform = Constants.BIND_SOURCE_WEIXIN;
        break;
    }

    /*
     * 如果为登录操作
     */
    if (login) {
      component = LoginApi.getInstance()
          .with(this)
          .platform(platform)
          .savedInstance(savedInstance)
          .callback(new LoginCallback() {
            @Override public void onComplete(UserInfoResponse userInfo) {
              Log.e(platform, userInfo.toString());
            }
          })
          .login();
    } else {
      runOnUiThread(new Runnable() {
        @Override public void run() {
          component = ShareApi.getInstance()
              .platform(MainActivity.this, platform, savedInstance)
              .withTitle(title)
              .withShareType(shareType)
              .withSummary(summary)
              .withContent(content)
              .withImage(bitmap)
              .withImageUrl(imageUrl)
              .withTargetUrl(targetUrl)
              .share();
        }
      });

    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (component != null) {
      component.onActivityResult(requestCode, resultCode, data);
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}
