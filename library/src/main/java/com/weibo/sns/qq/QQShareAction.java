package com.weibo.sns.qq;

import android.content.Context;
import android.graphics.Bitmap;
import com.weibo.sns.Constants;
import com.weibo.sns.ShareAction;

/**
 * Created by leiweibo on 12/21/16.
 */

public class QQShareAction extends ShareAction {

  public QQShareAction(Context context) {
    component = new QQComponent(context);
  }

  @Override public void share() {
    switch (shareType) {
      case Constants.SHARE_IMG_LOCAL:
        component.shareImage(bitmap);
        break;

      case Constants.SHARE_IMG_URL:
        component.shareImage(imageUrl);
        break;

      case Constants.SHARE_URL_IMG_LOCAL:
        component.shareContent(title, summary, targetUrl, bitmap);
        break;

      case Constants.SHARE_URL_IMG_URL:
        component.shareContent(title, summary, targetUrl, imageUrl);
        break;
    }
  }

  private void shareImage(String imageUrl) {
    component.shareImage(imageUrl);
  }

  private void shareImage(Bitmap bitmap) {
    component.shareImage(bitmap);
  }

  public void shareUrl(String title, String summary, String content, String targetUrl,
      String image) {
    component.shareContent(title, summary, targetUrl, image);
  }

  public void shsareUrl(String title, String summary, String content, String targetUrl,
      Bitmap bitmap) {
    component.shareContent(title, summary, targetUrl, bitmap);
  }
}
