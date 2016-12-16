package com.niubang.uguma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.niubang.uguma.weibo.WeiboComponent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private WeiboComponent weiboComponent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.weibo).setOnClickListener(this);
    findViewById(R.id.weibo_login).setOnClickListener(this);


    weiboComponent = new WeiboComponent(this, savedInstanceState);
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.weibo:
        weiboComponent.share();
        break;

      case R.id.weibo_login:
        weiboComponent.login();
        break;

      case R.id.qq:

        break;

      case R.id.qzone:

        break;

      case R.id.weixin:

        break;
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    weiboComponent.onActivityResult(requestCode, resultCode, data);
  }
}
