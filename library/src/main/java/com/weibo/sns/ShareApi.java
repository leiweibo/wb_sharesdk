package com.weibo.sns;

import com.weibo.sns.qq.QQShareAction;
import com.weibo.sns.sina.WeiboShareAction;

/**
 * 分享API接口，给外部调用
 * Created by leiweibo on 12/21/16.
 */

public class ShareApi {
  private static ShareApi instance;
  private ShareAction shareAction;

  private ShareApi() {

  }

  public ShareApi getInstance() {
    if (instance == null) {
      instance = new ShareApi();
    }
    return instance;
  }

  public ShareApi platform(String platform) {
    if (platform.equals(Constants.BIND_SOURCE_QQ)) {
      shareAction = new QQShareAction();
    } else if(platform.equals(Constants.BIND_SOURCE_QZONE)) {
      shareAction = new QQShareAction();
    } else if(platform.equals(Constants.BIND_SOURCE_WEIXIN)) {
      shareAction = new QQShareAction();
    } else if (platform.equals(Constants.BIND_SOURCE_SINA)) {
      shareAction = new WeiboShareAction();
    }
    return this;
  }

}
