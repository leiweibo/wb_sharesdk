package com.weibo.sns;


/**
 * 登录完成回调，方法给外部业务逻辑去实现
 * Created by leiweibo on 12/20/16.
 */

public interface LoginCallback {
  /**
   * 登录完成回调
   * @param userInfo 返回用户信息
   */
  void onComplete(UserInfoResponse userInfo);

}
