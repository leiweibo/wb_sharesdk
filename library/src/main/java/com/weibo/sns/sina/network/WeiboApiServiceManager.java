package com.weibo.sns.sina.network;

import com.weibo.sns.BaseServiceManager;

/**
 * Created by leiweibo on 12/21/16.
 */

public class WeiboApiServiceManager extends BaseServiceManager {

  private WeiboApiService weiboApiService;

  public WeiboApiServiceManager() {
    super();
    weiboApiService = retrofit.create(WeiboApiService.class);
  }

  @Override protected String getBaseUrl() {
    return "https://api.weibo.com/";
  }

  public WeiboApiService getApiService() {
    return weiboApiService;
  }
}
