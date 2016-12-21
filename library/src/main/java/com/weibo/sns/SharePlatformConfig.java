package com.weibo.sns;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leiweibo on 12/21/16.
 */

public class SharePlatformConfig {

  private static Map<String, String> configurations = new HashMap<>();

  public static void setWeixin(String appid, String secret) {
    configurations.put(Constants.WEIXIN_APP_ID_KEY, appid);
    configurations.put(Constants.WEIXIN_APP_SCRECT_KEY, secret);
  }

  public static void setSina(String appKey) {
    configurations.put(Constants.WEIBO_APP_KEY, appKey);
  }

  public static void setQQ(String appKey) {
    configurations.put(Constants.QQ_APP_ID_KEY, appKey);
  }

  public static String getQQAppId() {
    return configurations.get(Constants.QQ_APP_ID_KEY);
  }

  public static String getSinaAppKey() {
    return configurations.get(Constants.WEIBO_APP_KEY);
  }

  public static String getWeixinAppKey() {
    return configurations.get(Constants.WEIXIN_APP_ID_KEY);
  }

  public static String getWeixinSceret(){
    return configurations.get(Constants.WEIXIN_APP_SCRECT_KEY);
  }
}
