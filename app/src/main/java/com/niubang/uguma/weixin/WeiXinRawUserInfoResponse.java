package com.niubang.uguma.weixin;

import com.google.gson.annotations.SerializedName;
import com.niubang.uguma.Constants;
import com.niubang.uguma.UserInfoResponse;

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
