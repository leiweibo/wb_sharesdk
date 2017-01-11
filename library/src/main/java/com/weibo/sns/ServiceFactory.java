package com.weibo.sns;

import com.weibo.sns.sina.network.WeiboApiService;
import com.weibo.sns.sina.network.WeiboApiServiceManager;
import com.weibo.sns.weixin.network.WeixinApiService;
import com.weibo.sns.weixin.network.WeixinServiceManager;

/**
 * Created by leiweibo on 12/20/16.
 */

import com.weibo.sns.qq.network.QQApiService;
import com.weibo.sns.qq.network.QQServiceManager;
import com.weibo.sns.sina.network.WeiboApiService;
import com.weibo.sns.sina.network.WeiboApiServiceManager;
import com.weibo.sns.weixin.network.WeixinApiService;
import com.weibo.sns.weixin.network.WeixinServiceManager;

/**
 * Created by leiweibo on 12/20/16.
 */

public class ServiceFactory {
  private static final Object monitor = new Object();
  private static WeixinApiService apiService;
  private static WeiboApiService weiboApiService;
  private static QQApiService qqApiService;

  public static WeixinApiService getWeixinApiService() {
    synchronized (monitor) {
      if (apiService == null) {
        apiService = new WeixinServiceManager().getApiService();
      }
      return apiService;
    }
  }

  public static WeiboApiService getWeiboApiService() {
    synchronized (monitor) {
      if (weiboApiService == null) {
        weiboApiService = new WeiboApiServiceManager().getApiService();
      }
      return weiboApiService;
    }
  }

  public static QQApiService getQQApiService() {
    synchronized (monitor) {
      if (qqApiService == null) {
        qqApiService = new QQServiceManager().getApiService();
      }
      return qqApiService;
    }
  }
}
