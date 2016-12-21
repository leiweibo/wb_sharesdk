package com.niubang.uguma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.weibo.sns.BaseComponent;
import com.weibo.sns.Constants;
import com.weibo.sns.LoginApi;
import com.weibo.sns.LoginCallback;
import com.weibo.sns.SharePlatformConfig;
import com.weibo.sns.UserInfoResponse;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  public static final String WEIXIN_APP_ID = "wxd07936d8648b4da5";
  public static final String WEIXIN_APP_SCRECT = "a7309ef6d60a2127318423cbad300aae";

  public static final String WEIBO_APP_KEY = "3616164551";
  public static final String QQ_APP_ID = "1105406253";

  private BaseComponent component;
  private Bundle saveInstance;
  private String platform;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.weibo_share_txt).setOnClickListener(this);
    findViewById(R.id.weixin).setOnClickListener(this);
    findViewById(R.id.weibo_login).setOnClickListener(this);
    findViewById(R.id.weixin_login).setOnClickListener(this);
    findViewById(R.id.qq).setOnClickListener(this);
    findViewById(R.id.qzone).setOnClickListener(this);
    findViewById(R.id.qq_login).setOnClickListener(this);

    this.saveInstance = savedInstanceState;

    SharePlatformConfig.setQQ(QQ_APP_ID);
    SharePlatformConfig.setSina(WEIBO_APP_KEY);
    SharePlatformConfig.setWeixin(WEIXIN_APP_ID, WEIXIN_APP_SCRECT);
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.weibo_share_txt:
        break;

      case R.id.qq:
        break;

      case R.id.qzone:
        break;

      case R.id.weixin:
        break;

      /////////////////////////以下是登录

      case R.id.weibo_login:
        this.platform = Constants.BIND_SOURCE_SINA;
        break;

      case R.id.qq_login:
        this.platform = Constants.BIND_SOURCE_QQ;
        break;

      case R.id.weixin_login:
        this.platform = Constants.BIND_SOURCE_WEIXIN;
        break;
    }

    component = LoginApi.getInstance()
        .with(this)
        .platform(platform)
        .savedInstance(saveInstance)
        .callback(new LoginCallback() {
          @Override public void onComplete(UserInfoResponse userInfo) {
            Log.e(platform, userInfo.toString());
          }
        })
        .login();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    component.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
  }
}
