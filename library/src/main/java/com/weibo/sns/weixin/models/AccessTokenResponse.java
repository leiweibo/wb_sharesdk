package com.weibo.sns.weixin.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leiweibo on 12/20/16.
 */

public class AccessTokenResponse extends BaseResponse {
  @SerializedName("access_token") public String accessToken;
  @SerializedName("expires_in") public long expiresIn;
  @SerializedName("openid") public String openId;
  @SerializedName("scope") public String scope;
}
