package com.weibo.sns.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.weibo.sns.BaseComponent;
import com.weibo.sns.Constants;
import com.weibo.sns.DiskCacheUtil;
import com.weibo.sns.LoginCallback;
import com.weibo.sns.SharePlatformConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.tencent.connect.common.Constants.REQUEST_QZONE_SHARE;

/**
 * 分享到QQ目前只支持图文分享
 * Created by leiweibo on 12/21/16.
 */

public class QzoneComponent extends BaseComponent {

  private Context context;
  private Tencent tencent;

  public QzoneComponent(Context context) {
    super();
    this.context = context;
    this.tencent = Tencent.createInstance(SharePlatformConfig.getQQAppId(), context);
  }

  /**
   * 跟QQ一样
   */
  @Override public void login(LoginCallback callback) {
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_QZONE_SHARE) {
      Tencent.onActivityResultData(requestCode, resultCode, data, buildShareListener());
    }
  }

  @Override protected String getSource() {
    return Constants.BIND_SOURCE_QZONE;
  }

  /**
   * SDK不支持, 只支持图文类型
   *
   * @param imageUrl 图片的链接
   */
  @Deprecated @Override public void shareImage(String imageUrl) {
    final Bundle params = new Bundle();
    params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE);
    ArrayList<String> imgUrls = new ArrayList<>();
    imgUrls.add(imageUrl);
    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrls);
    tencent.shareToQzone((Activity) context, params, buildShareListener());
  }

  /**
   * SDK不支持, 只支持图文类型
   */
  @Deprecated @Override public void shareImage(Bitmap image) {
    Observable.just(image)
        .observeOn(Schedulers.io())
        .subscribeOn(AndroidSchedulers.mainThread())
        .map(new Func1<Bitmap, String>() {
          @Override public String call(Bitmap bitmap) {
            try {
              DiskCacheUtil diskCacheUtil = new DiskCacheUtil(context);
              File file =
                  diskCacheUtil.writeLocalBitMapToCache(bitmap, System.currentTimeMillis() + "");
              return file.getAbsolutePath();
            } catch (IOException e) {
              e.printStackTrace();
            }
            return null;
          }
        })
        .subscribe(new Action1<String>() {
          @Override public void call(String path) {
            if (path != null) {
              final Bundle params = new Bundle();
              params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
              params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path);

              tencent.shareToQzone((Activity) context, params, buildShareListener());
            }
          }
        });
  }

  /**
   * 分享网络图片到QQ空间
   */
  @Override public void shareContent(String title, String summary, String targetUrl,
      String imageUrl) {
    Bundle params = new Bundle();
    params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
    params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
    params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);//选填
    params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);//必填
    ArrayList<String> urls = new ArrayList<>();
    urls.add(imageUrl);
    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
    tencent.shareToQzone((Activity) context, params, buildShareListener());
  }

  /**
   * 分享本地图片到QQ空间
   */
  @Override public void shareContent(final String title, final String summary,
      final String targetUrl, final Bitmap image) {

    Observable.just(image)
        .observeOn(Schedulers.io())
        .subscribeOn(AndroidSchedulers.mainThread())
        .map(new Func1<Bitmap, String>() {
          @Override public String call(Bitmap bitmap) {
            try {
              DiskCacheUtil diskCacheUtil = new DiskCacheUtil(context);
              File file =
                  diskCacheUtil.writeLocalBitMapToCache(bitmap, System.currentTimeMillis() + "");
              return file.getAbsolutePath();
            } catch (IOException e) {
              e.printStackTrace();
            }
            return null;
          }
        })
        .subscribe(new Action1<String>() {
          @Override public void call(String path) {
            if (path != null) {
              final Bundle params = new Bundle();
              params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                  QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
              params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
              params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);//选填
              params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);//必填
              /*
               * 这里必须要把SHARE_TO_QQ_IMAGE_URL 和 SHARE_TO_QQ_IMAGE_LOCAL_URL都加上，
               * 否则无法分享本地图片
               */
              ArrayList<String> images = new ArrayList<>();
              images.add(path);
              params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, images);
              params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path);
              tencent.shareToQzone((Activity) context, params, buildShareListener());
            }
          }
        });
  }

  private IUiListener buildShareListener() {
    IUiListener uiListener = new IUiListener() {
      @Override public void onComplete(Object o) {
        Toast.makeText(context, "分享完成", Toast.LENGTH_SHORT).show();
      }

      @Override public void onError(UiError uiError) {
        ((Activity) context).runOnUiThread(new Runnable() {
          @Override public void run() {
            Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
          }
        });
      }

      @Override public void onCancel() {
        Toast.makeText(context, "分享取消", Toast.LENGTH_SHORT).show();
      }
    };
    return uiListener;
  }
}
