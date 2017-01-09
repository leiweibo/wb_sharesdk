package com.weibo.sns.qq.network;

import com.weibo.sns.BaseServiceManager;

/**
 * Created by leiweibo on 1/9/17.
 */

public class QQServiceManager extends BaseServiceManager {
  private QQApiService apiService;

  public QQServiceManager() {
    super();
    apiService = retrofit.create(QQApiService.class);
  }

  @Override protected String getBaseUrl() {
    return "https://graph.qq.com/";
  }

  public QQApiService getApiService() {
    return apiService;
  }
}
