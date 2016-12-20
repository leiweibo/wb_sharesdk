package com.weibo.sns.weixin.network;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by leiweibo on 12/20/16.
 */

public class ServiceManager {
  private final String URL = "https://api.weixin.qq.com/";
  private final int DEFAULT_TIME = 5;
  private ApiService apiService;

  public ServiceManager() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    builder.connectTimeout(DEFAULT_TIME, TimeUnit.SECONDS);
    builder.addInterceptor(interceptor);

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(URL)
        .client(builder.build())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    apiService = retrofit.create(ApiService.class);
  }

  public ApiService getApiService() {
    return apiService;
  }
}
