package com.weibo.sns;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by leiweibo on 12/21/16.
 */

public abstract class BaseServiceManager {

  private final int DEFAULT_TIME = 5;
  protected Retrofit retrofit;

  public BaseServiceManager() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    builder.connectTimeout(DEFAULT_TIME, TimeUnit.SECONDS);
    builder.addInterceptor(interceptor);

    retrofit = new Retrofit.Builder().baseUrl(getBaseUrl())
        .client(builder.build())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();
  }

  protected abstract String getBaseUrl();
}
