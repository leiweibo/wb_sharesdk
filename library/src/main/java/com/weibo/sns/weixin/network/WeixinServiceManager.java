package com.weibo.sns.weixin.network;

import com.weibo.sns.BaseServiceManager;

/**
 * Created by leiweibo on 12/20/16.
 */

public class WeixinServiceManager extends BaseServiceManager {

  private WeixinApiService apiService;

  public WeixinServiceManager() {
    super();
    apiService = retrofit.create(WeixinApiService.class);
  }

  @Override protected String getBaseUrl() {
    return "https://api.weixin.qq.com/";
  }

  public WeixinApiService getApiService() {
    return apiService;
  }
}
