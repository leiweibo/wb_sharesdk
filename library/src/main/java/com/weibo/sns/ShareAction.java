package com.weibo.sns;

import android.graphics.Bitmap;

/**
 * 分享类，提供给外部类去掉用
 * Created by leiweibo on 12/21/16.
 */

public abstract class ShareAction {

  protected BaseComponent component;
  protected String title; //标题
  protected String summary; //概要
  protected String content; //内容
  protected Bitmap bitmap; //图片 bitmap
  protected String imageUrl; //图片url
  protected String targetUrl; //分享的url
  protected int shareType = Constants.SHARE_URL_IMG_URL; //参考Constants里面的定义, 默认分享纯文字

  /**
   * 分享文字
   *
   * @return 本身对象
   */
  public ShareAction withContent(String content) {
    this.content = content;
    return this;
  }

  /**
   * 分享内容的标题
   */
  public ShareAction withTitle(String title) {
    this.title = title;
    return this;
  }

  /**
   * 分享内容概要
   */
  public ShareAction withSummary(String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * 外部图片链接
   *
   * @param imageUrl 图片的url
   * @return 本身对象
   */
  public ShareAction withImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
    return this;
  }

  /**
   * 外部链接地址分享
   *
   * @param url 网址
   * @return 本身对象
   */
  public ShareAction withTargetUrl(String url) {
    this.targetUrl = url;
    return this;
  }

  public ShareAction shareType(int shareType) {
    this.shareType = shareType;
    return this;
  }

  /**
   * 本地图片对象分享
   *
   * @param bitmap 本地图片被转换为的bitmap对象
   * @return 本身对象
   */
  public ShareAction withImage(Bitmap bitmap) {
    this.bitmap = bitmap;
    return this;
  }

  public abstract void share();
}
