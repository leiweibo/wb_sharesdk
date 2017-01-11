package com.weibo.sns.weixin;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.weibo.sns.BaseComponent;
import com.weibo.sns.DiskCacheUtil;
import com.weibo.sns.Util;
import java.io.ByteArrayOutputStream;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 微信组件基类，提供给微信和朋友圈
 * Created by leiweibo on 12/22/16.
 */

public abstract class WeixinBaseComponent extends BaseComponent {

  protected IWXAPI wxapi;
  protected static Context context;
  private final static int THUMB_SIZE_WIDTH = 100;

  /**
   * 分享图文内容到微信 或者 朋友圈，图片为本地图片
   *
   * @param title 标题
   * @param summary 概要
   * @param targetUrl url地址
   * @param bitmap 图片的bitmap
   * @param target 朋友圈或者微信会话
   */
  protected void shareContent(String title, String summary, String targetUrl, Bitmap bitmap,
      int target) {
    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = String.valueOf(System.currentTimeMillis());
    req.scene = target;

    //初始化一个WXTextObject对象
    WXWebpageObject webpageObject = new WXWebpageObject();
    webpageObject.webpageUrl = targetUrl;
    //用WXTextObject对象初始化一个WXMediaMessage对象
    WXMediaMessage msg = new WXMediaMessage(webpageObject);
    msg.title = title;
    msg.description = summary;
    if (bitmap != null) {
      Bitmap thumbBmp = generateThumBitmap(bitmap);
      if (thumbBmp != null) {
        bitmap.recycle();
        msg.thumbData = bitMap2Bytes(thumbBmp);  //设置缩略图
      }
    }
    req.message = msg;

    wxapi.sendReq(req);
  }

  /**
   * 生成缩略图，宽度100，高度等比缩放
   * @param bitmap 原图
   * @return 缩略图biamap
   */
  private Bitmap generateThumBitmap(Bitmap bitmap) {
    Bitmap thumbBmp = null;
    if (bitmap != null) {
      int bitmapHeight = bitmap.getHeight();
      int bitmapWidth = bitmap.getWidth();
      float ratio = (float) bitmapHeight / bitmapWidth;
      thumbBmp =
          Bitmap.createScaledBitmap(bitmap, THUMB_SIZE_WIDTH, (int)(THUMB_SIZE_WIDTH * ratio), true);

    }
    return thumbBmp;
  }

  /**
   * 将bitmap转成byte数组
   *
   * @param bitmap bitmap对象
   * @return 转化之后的byte数组
   */
  private byte[] bitMap2Bytes(Bitmap bitmap) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
    bitmap.recycle();

    byte[] result = output.toByteArray();
    try {
      output.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }



  /**
   * 分享图文内容到微信，图片为远程图片
   *
   * @param title 标题
   * @param summary 概要
   * @param targetUrl url地址
   * @param imageUrl 图片的url
   * @param target 朋友圈或者微信会话列表
   */
  protected void shareContent(final String title, final String summary, final String targetUrl,
      final String imageUrl, final int target) {
    if (TextUtils.isEmpty(imageUrl)) {
      shareContent(title, summary, targetUrl, Util.getDefaultBitmap(context), target);
    } else {
      Observable.just(imageUrl).observeOn(Schedulers.io()).map(new Func1<String, Bitmap>() {
        @Override public Bitmap call(String s) {
          DiskCacheUtil diskCacheUtil = new DiskCacheUtil(context);
          Bitmap bitmap = diskCacheUtil.getBitmapFromURL(s);
          return bitmap;
        }
      }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
        @Override public void call(Bitmap bitmap) {
          shareContent(title, summary, targetUrl, bitmap, target);
        }
      });
    }
  }

  /**
   * 本地图片分享图片到微信
   *
   * @param bitmap 本地图片被转化bitmap
   * @param target 朋友圈或者微信会话列表
   */
  protected void shareImage(Bitmap bitmap, int target) {
    if (bitmap != null) {

      WXImageObject imgObj = new WXImageObject(bitmap);

      WXMediaMessage msg = new WXMediaMessage();
      msg.mediaObject = imgObj;
      if (bitmap != null) {
        Bitmap thumbBmp = generateThumBitmap(bitmap);
        if (thumbBmp != null) {
          bitmap.recycle();
          msg.thumbData = bitMap2Bytes(thumbBmp);  //设置缩略图
        }
      }

      SendMessageToWX.Req req = new SendMessageToWX.Req();
      req.transaction = String.valueOf("img" + System.currentTimeMillis());
      req.message = msg;
      req.scene = target;
      wxapi.sendReq(req);
    }
  }

  /**
   * 网络图片地址分享图片到微信
   *
   * @param imageUrl 图片的链接
   * @param target 朋友圈或者微信会话列表
   */
  protected void shareImage(final String imageUrl, final int target) {
    Observable.just(imageUrl).observeOn(Schedulers.io()).map(new Func1<String, Bitmap>() {
      @Override public Bitmap call(String s) {
        DiskCacheUtil diskCacheUtil = new DiskCacheUtil(context);
        Bitmap bitmap = diskCacheUtil.getBitmapFromURL(imageUrl);
        return bitmap;
      }
    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
      @Override public void call(Bitmap bitmap) {
        shareImage(bitmap, target);
      }
    });
  }
}
