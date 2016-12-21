package com.weibo.sns;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.weibo.sns.qq.QQShareAction;
import com.weibo.sns.qq.QzoneShareAction;
import com.weibo.sns.sina.WeiboShareAction;
import com.weibo.sns.weixin.WeixinShareAction;

/**
 * 分享API接口，给外部调用
 * Created by leiweibo on 12/21/16.
 */

public class ShareApi {
  private static ShareApi instance;
  private ShareAction shareAction;

  private ShareApi() {

  }

  public static ShareApi getInstance() {
    if (instance == null) {
      instance = new ShareApi();
    }
    return instance;
  }

  /**
   *
   * @param context 上下文
   * @param platform 对应的平台
   * @param savedInstance Activity的savedInstance 只有新浪需要用到
   * @return
   */
  public ShareApi platform(Context context, String platform, Bundle savedInstance) {
    if (platform.equals(Constants.BIND_SOURCE_QQ)) {
      shareAction = new QQShareAction(context);
    } else if (platform.equals(Constants.BIND_SOURCE_QZONE)) {
      shareAction = new QzoneShareAction();
    } else if (platform.equals(Constants.BIND_SOURCE_WEIXIN)) {
      shareAction = new WeixinShareAction(context);
    } else if (platform.equals(Constants.BIND_SOURCE_SINA)) {
      shareAction = new WeiboShareAction(context, savedInstance);
    }
    return this;
  }


  public ShareApi withShareType(int shareType) {
    checkShareActionNull();
    shareAction.shareType(shareType);
    return this;
  }
  /**
   * 分享文字
   *
   * @return 本身对象
   */
  public ShareApi withContent(String content) {
    checkShareActionNull();
    shareAction.withContent(content);
    return this;
  }

  /**
   * 分享内容的标题
   */
  public ShareApi withTitle(String title) {
    checkShareActionNull();
    shareAction.withTitle(title);
    return this;
  }

  /**
   * 分享内容概要
   */
  public ShareApi withSummary(String summary) {
    checkShareActionNull();
    shareAction.withSummary(summary);
    return this;
  }

  /**
   * 外部图片链接
   *
   * @param imageUrl 图片的url
   * @return 本身对象
   */
  public ShareApi withImageUrl(String imageUrl) {
    checkShareActionNull();
    shareAction.withImageUrl(imageUrl);
    return this;
  }

  /**
   * 外部链接地址分享
   *
   * @param url 网址
   * @return 本身对象
   */
  public ShareApi withTargetUrl(String url) {
    checkShareActionNull();
    shareAction.withTargetUrl(url);
    return this;
  }

  /**
   * 本地图片对象分享
   *
   * @param bitmap 本地图片被转换为的bitmap对象
   * @return 本身对象
   */
  public ShareApi withImage(Bitmap bitmap) {
    checkShareActionNull();
    shareAction.withImage(bitmap);
    return this;
  }

  private void checkShareActionNull() {
    if (shareAction == null) {
      throw new IllegalArgumentException("Please call platform() at first.");
    }
  }

  /**
   * 分享 并且把对应的component返回
   * @return
   */
  public BaseComponent share() {
    if (shareAction != null) {
      shareAction.share();
    }

    return shareAction.component;
  }
}
