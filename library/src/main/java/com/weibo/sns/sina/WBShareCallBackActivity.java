package com.weibo.sns.sina;

/**
 * Created by leiweibo on 1/4/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;

public class WBShareCallBackActivity extends Activity implements Response {
  private Bundle savedInstance;
  private WeiboComponent component;
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.savedInstance = savedInstanceState;

    if (component == null) {
      component = WeiboComponent.getInstance(this, savedInstance);
    }
    component.onNewIntent(this.getIntent(), this);
  }

  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    this.setIntent(intent);

    if (component == null) {
      component = WeiboComponent.getInstance(this, savedInstance);
    }
    component.onNewIntent(intent, this);
  }

  public void onResponse(BaseResponse baseResponse) {
    if (component == null) {
      component = WeiboComponent.getInstance(this, savedInstance);
    }
    component.onResponse(baseResponse);
    this.finish();
  }
}