package com.weibo.sns.sina;

import android.content.Context;
import android.os.Bundle;
import com.weibo.sns.Constants;
import com.weibo.sns.ShareAction;

/**
 * Created by leiweibo on 12/21/16.
 */

public class WeiboShareAction extends ShareAction {

  public WeiboShareAction(Context context, Bundle savedInstance) {
    component = WeiboComponent.getInstance(context, savedInstance);
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
