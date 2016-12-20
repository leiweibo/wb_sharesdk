package com.weibo.sns.weixin.network;

import com.weibo.sns.weixin.models.AccessTokenResponse;
import com.weibo.sns.weixin.models.WeiXinRawUserInfoResponse;
import java.util.Map;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by leiweibo on 12/20/16.
 */

public interface ApiService {
  @GET("/sns/oauth2/access_token") Observable<AccessTokenResponse> getToken(
      @QueryMap Map<String, String> prams);

  @GET("/sns/userinfo") Observable<WeiXinRawUserInfoResponse> getUserInfo(
      @Query("access_token") String token, @Query("openid") String openId);
}
