package com.weibo.sns.weixin.models;

import com.google.gson.annotations.SerializedName;
import com.weibo.sns.Constants;
import com.weibo.sns.UserInfoResponse;

/**
 * Created by leiweibo on 12/20/16.
 */

public class WeiXinRawUserInfoResponse extends BaseResponse {

  @SerializedName("openid") public String openId;
  @SerializedName("nickname") public String nickName;
  @SerializedName("headimgurl") public String imageUrl;

  public UserInfoResponse converToUserInfo() {
    UserInfoResponse userInfoResponse =
        new UserInfoResponse(Constants.BIND_SOURCE_WEIXIN, openId, nickName, imageUrl);
    return userInfoResponse;
  }
}
