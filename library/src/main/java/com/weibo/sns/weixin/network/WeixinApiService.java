package com.weibo.sns.weixin.network;

import com.weibo.sns.weixin.models.AccessTokenResponse;
import com.weibo.sns.weixin.models.WeiXinRawUserInfoResponse;
import java.util.Map;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * 微信API 服务
 * Created by leiweibo on 12/20/16.
 */

public interface WeixinApiService {
  /**
   * 获取token接口
   * @param prams
   * @return
   */
  @GET("/sns/oauth2/access_token") Observable<AccessTokenResponse> getToken(
      @QueryMap Map<String, String> prams);

  /**
   * 获取用户信息
   * @param token
   * @param openId
   * @return
   */
  @GET("/sns/userinfo") Observable<WeiXinRawUserInfoResponse> getUserInfo(
      @Query("access_token") String token, @Query("openid") String openId);
}
