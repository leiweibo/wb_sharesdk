package com.niubang.uguma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.weibo.sns.BaseComponent;
import com.weibo.sns.LoginCallback;
import com.weibo.sns.UserInfoResponse;
import com.weibo.sns.qq.QQComponent;
import com.weibo.sns.sina.WeiboComponent;
import com.weibo.sns.weixin.WeixinComponent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private WeiboComponent weiboComponent;
  private WeixinComponent wechatComponent;
  private QQComponent qqComponent;

  private BaseComponent component;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.weibo).setOnClickListener(this);
    findViewById(R.id.weixin).setOnClickListener(this);
    findViewById(R.id.weibo_login).setOnClickListener(this);
    findViewById(R.id.weixin_login).setOnClickListener(this);
    findViewById(R.id.qq).setOnClickListener(this);
    findViewById(R.id.qzone).setOnClickListener(this);
    findViewById(R.id.qq_login).setOnClickListener(this);


    weiboComponent = new WeiboComponent(this, savedInstanceState);
    wechatComponent = WeixinComponent.getInstance(this);
    qqComponent = new QQComponent(this);
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.weibo:
        component = weiboComponent;
        weiboComponent.share();
        break;

      case R.id.weibo_login:
        component = weiboComponent;
        weiboComponent.login(new LoginCallback() {
          @Override public void onComplete(UserInfoResponse userInfo) {
            Log.e("Weibooooo", userInfo.toString());
          }
        });
        break;

      case R.id.qq_login:
        component = qqComponent;
        qqComponent.login(new LoginCallback() {
          @Override public void onComplete(UserInfoResponse userInfo) {
            Log.e("Weibooooo", userInfo.toString());
          }
        });
        break;

      case R.id.qq:
        component = qqComponent;
        qqComponent.shareToQQ();
        break;

      case R.id.qzone:
        component = qqComponent;
        qqComponent.shareToQzone();
        break;

      case R.id.weixin_login:
        component = wechatComponent;
        wechatComponent.login(new LoginCallback() {
          @Override public void onComplete(UserInfoResponse userInfo) {
            Log.e("Weibooooo", userInfo.toString());
          }
        });
        break;

      case R.id.weixin:
        component = wechatComponent;
        wechatComponent.shareToWechat();
        break;
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    component.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
  }
}
