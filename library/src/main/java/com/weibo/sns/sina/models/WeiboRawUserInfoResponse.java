package com.weibo.sns.sina.models;

import com.google.gson.annotations.SerializedName;
import com.weibo.sns.Constants;
import com.weibo.sns.UserInfoResponse;

/**
 * 微博原生返回的对象，需要转换成第三方登录平台同意的UserInfoResponse
 * Created by leiweibo on 12/21/16.
 */

public class WeiboRawUserInfoResponse {
  public String id;
  @SerializedName("screen_name") public String nickName;
  @SerializedName("profile_image_url") public String avatarUrl;

  /**
   * 将RawUserInfo转成UserInfoResponse
   * @return UserInfoResponse
   */
  public UserInfoResponse converToUserInfo() {

    return new UserInfoResponse(Constants.BIND_SOURCE_SINA, id, nickName, avatarUrl);
  }
}
