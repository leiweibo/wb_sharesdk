package com.niubang.uguma;

/**
 * Created by leiweibo on 12/20/16.
 */

public class UserInfoResponse {

  private String snsKind;
  private String uid;
  private String nickname;
  private String imageUrl;

  public UserInfoResponse(String snsKind, String uid, String nickName, String imgUrl) {
    this.snsKind = snsKind;
    this.uid = uid;
    this.nickname = nickName;
    this.imageUrl = imgUrl;
  }

  @Override public String toString() {
    return "sns:" + snsKind + ", uId:" + uid + ", nickName:" + nickname + ", icon:" + imageUrl;
  }
}
