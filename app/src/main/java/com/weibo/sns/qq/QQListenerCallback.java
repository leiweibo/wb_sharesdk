package com.weibo.sns.qq;

import org.json.JSONObject;

/**
 * Created by leiweibo on 12/20/16.
 */

public interface QQListenerCallback {
  void doComplete(JSONObject object);
}
