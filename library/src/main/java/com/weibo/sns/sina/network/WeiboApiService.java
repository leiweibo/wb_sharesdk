package com.weibo.sns.sina.network;

import com.weibo.sns.sina.models.WeiboRawUserInfoResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by leiweibo on 12/21/16.
 */

public interface WeiboApiService {
  @GET("2/users/show.json") Observable<WeiboRawUserInfoResponse> getUserInfo(
      @Query("access_token") String accessToken, @Query("uid") String uid);
}
