package com.niubang.uguma.weixin;

/**
 * Created by leiweibo on 12/20/16.
 */

public class ServiceFactory {
  private static final Object monitor = new Object();
  private static ApiService apiService;

  public static ApiService getService() {
    synchronized (monitor) {
      if (apiService == null) {
        apiService = new ServiceManager().getApiService();
      }
      return apiService;
    }
  }
}
