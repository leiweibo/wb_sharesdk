package com.weibo.sns.qq.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leiweibo on 1/9/17.
 */

public class QQResponse {
  public String ret;
  public String msg;
  @SerializedName("client_id") public String clientId;
  @SerializedName("openid") public String openId;
  @SerializedName("unionid")public String unionId;
}
