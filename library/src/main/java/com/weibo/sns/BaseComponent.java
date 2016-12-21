package com.weibo.sns;

import android.content.Intent;

/**
 * Created by leiweibo on 12/20/16.
 */

public abstract class BaseComponent {

  protected LoginCallback loginCallback;


  public abstract void login(LoginCallback callback);

  /**
   * 某些平台上要对onActivityResult进行处理，细节看每个对应的component里面的onActivityResult的注释
   *
   * @param requestCode 请求code
   * @param resultCode 返回的结构的code
   * @param data data
   */
  public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

  /**
   * 每个平台对应的source名称
   *
   * @return Constants.BIND_SOURCE_xxx
   */
  protected abstract String getSource();

  public LoginCallback getLoginCallback() {
    return loginCallback;
  }

  /**
   * 纯文字分享
   */
  public abstract void shareText();

  /**
   * 纯图片分享
   */
  public abstract void shareImage();

  /**
   * 图文分享
   */
  public abstract void shareTextWithImage();

  /**
   * 网页分享
   */
  public abstract void shareUrl();
}
