package com.niubang.uguma.qq;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import java.util.ArrayList;
import org.json.JSONObject;

/**
 * QQ分享和登录组件
 * Created by leiweibo on 12/16/16.
 */

public class QQComponent {
  private Context context;
  private Tencent tencent;
  private final String SCOPE = "get_user_info,add_t";

  public QQComponent(Context context) {
    this.context = context;
    this.tencent = Tencent.createInstance(Constants.APP_ID, context);
  }

  public void login() {
    if (!tencent.isSessionValid()) {
      tencent.login((Activity) context, SCOPE, new IUiListener() {
        @Override public void onComplete(Object response) {
          if (null == response) {
            //Util.showResultDialog(MainActivity.this, "返回为空", "登录失败");
            return;
          }
          JSONObject jsonResponse = (JSONObject) response;
          if (null != jsonResponse && jsonResponse.length() == 0) {
            //Util.showResultDialog(MainActivity.this, "返回为空", "登录失败");
            return;
          }
          //doComplete((JSONObject)response);

        }


        @Override public void onError(UiError uiError) {

        }

        @Override public void onCancel() {

        }
      });
    }
  }

  /**
   * 发送给QQ用户（单向）
   */
  public void shareToQQ() {
    final Bundle params = new Bundle();
    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
    params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "要分享的摘要");
    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://www.qq.com/news/1.html");
    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "测试应用222222");
    //params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
    tencent.shareToQQ((Activity) context, params, new IUiListener() {
      @Override public void onComplete(Object o) {

      }

      @Override public void onError(UiError uiError) {

      }

      @Override public void onCancel() {

      }
    });
  }

  /**
   * 分享到QQ空间，目前只支持图文分享
   */
  public void shareToQzone() {
    Bundle params = new Bundle();
    params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
    params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "标题");//必填
    params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "摘要");//选填
    params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://www.uguma.com");//必填
    ArrayList<String> urls = new ArrayList<>();
    urls.add("http://photo.iyaxin.com/attachement/jpg/site2/20111122/001966a9235310358ab51f.jpg");
    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
    tencent.shareToQzone((Activity) context, params, new IUiListener() {
      @Override public void onComplete(Object o) {
        Log.e("weibooo", "onComplete");
      }

      @Override public void onError(UiError uiError) {
        Log.e("weibooo", "onError");
      }

      @Override public void onCancel() {
        Log.e("weibooo", "onCancel");
      }
    });
  }
}
