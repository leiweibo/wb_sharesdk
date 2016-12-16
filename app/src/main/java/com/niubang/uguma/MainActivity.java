package com.niubang.uguma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.niubang.uguma.qq.QQComponent;
import com.niubang.uguma.wechat.WechatComponent;
import com.niubang.uguma.weibo.WeiboComponent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private WeiboComponent weiboComponent;
  private WechatComponent wechatComponent;
  private QQComponent qqComponent;

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
    wechatComponent = new WechatComponent(this);
    qqComponent = new QQComponent(this);
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.weibo:
        weiboComponent.share();
        break;

      case R.id.weibo_login:
        weiboComponent.login();
        break;

      case R.id.qq_login:
        qqComponent.login();
        break;

      case R.id.qq:
        qqComponent.shareToQQ();
        break;

      case R.id.qzone:
        qqComponent.shareToQzone();
        break;

      case R.id.weixin_login:
        wechatComponent.login();
        break;

      case R.id.weixin:
        wechatComponent.shareToWechat();
        break;
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    weiboComponent.onActivityResult(requestCode, resultCode, data);
  }
}
