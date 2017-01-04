package com.weibo.sns;

import android.content.Context;
import android.os.Bundle;
import com.weibo.sns.qq.QQComponent;
import com.weibo.sns.sina.WeiboComponent;
import com.weibo.sns.weixin.WeixinComponent;

/**
 * Created by leiweibo on 12/21/16.
 */

public class LoginApi {

  private static LoginApi instance;
  private BaseComponent component;
  private Context context;
  private Bundle savedInstance;
  private LoginCallback callback;

  private LoginApi() {
  }

  public static LoginApi getInstance() {
    if (instance == null) {
      instance = new LoginApi();
    }
    return instance;
  }

  public LoginApi with(Context context) {
    this.context = context;
    return this;
  }

  /**
   * 新浪微博登录的时候需要用到
   * @param savedInstance
   * @return
   */
  public LoginApi savedInstance(Bundle savedInstance) {
    this.savedInstance = savedInstance;
    return this;
  }

  public LoginApi callback(LoginCallback callback) {
    this.callback = callback;
    return this;
  }

  public LoginApi platform(String platform) {
    if (context == null) {
      throw new IllegalStateException("Please call .with(context)");
    }
    if (platform.equals(Constants.BIND_SOURCE_QQ)) {
      component = new QQComponent(context);
    } else if (platform.equals(Constants.BIND_SOURCE_WEIXIN)) {
      component = WeixinComponent.getInstance(context);
    } else if (platform.equals(Constants.BIND_SOURCE_SINA)) {
      component = WeiboComponent.getInstance(context, savedInstance);
    }
    return this;
  }

  public BaseComponent login() {
    if (component != null) {
      component.login(callback);
    }
    return component;
  }
}
