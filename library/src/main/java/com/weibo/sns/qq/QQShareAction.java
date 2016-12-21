package com.weibo.sns.qq;

import android.content.Context;
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
}
