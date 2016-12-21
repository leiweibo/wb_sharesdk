package com.weibo.sns;

import android.content.Intent;
import android.graphics.Bitmap;

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
   * 纯图片分享
   * @param imageUrl 图片的链接
   */
  public abstract void shareImage(String imageUrl);

  /**
   * 纯图片分享
   * @param bitmap 本地图片被转化bitmap
   */
  public abstract void shareImage(Bitmap bitmap);

  /**
   * 网页分享
   * @param title
   * @param summary
   * @param targetUrl
   * @param imageUrl
   */
  public abstract void shareContent(String title, String summary, String targetUrl, String imageUrl);

  /**
   * 网页分享，含本地图片
   * @param title
   * @param summary
   * @param targetUrl
   * @param image
   */
  public abstract void shareContent(String title, String summary, String targetUrl, Bitmap image);
}
