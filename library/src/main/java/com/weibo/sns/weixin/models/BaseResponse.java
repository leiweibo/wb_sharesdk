package com.weibo.sns.weixin.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leiweibo on 12/20/16.
 */

public class BaseResponse {
  @SerializedName("errorcode") public String code;
  @SerializedName("errmsg") public String msg;
}
