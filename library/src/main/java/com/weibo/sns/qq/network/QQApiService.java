package com.weibo.sns.qq.network;

import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by leiweibo on 1/9/17.
 */

public interface QQApiService {
  @GET("/oauth2.0/me") Observable<ResponseBody> getUnionId(@QueryMap Map<String, String> prams);
}
