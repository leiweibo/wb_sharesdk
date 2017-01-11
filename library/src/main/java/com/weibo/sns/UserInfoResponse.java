package com.weibo.sns;

import java.io.Serializable;

/**
 * Created by leiweibo on 12/20/16.
 */

public class UserInfoResponse implements Serializable {

  private String snsKind;
  private String uid;
  private String nickname;
  private String imageUrl;
  private String unionId; //Just for qq

  public UserInfoResponse(String snsKind, String uid, String nickName, String imgUrl) {
    this.snsKind = snsKind;
    this.uid = uid;
    this.nickname = nickName;
    this.imageUrl = imgUrl;
  }

  @Override public String toString() {
    return "sns:"
        + snsKind
        + ", uId:"
        + uid
        + ", nickName:"
        + nickname
        + ", icon:"
        + imageUrl
        + ", unionId:"
        + unionId;
  }

  public String getUid() {
    return uid;
  }

  public String getNickname() {
    return nickname;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getUnionId() {
    return unionId;
  }

  public void setUnionId(String unionId) {
    this.unionId = unionId;
  }
}
