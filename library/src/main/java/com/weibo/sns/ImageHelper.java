//package com.weibo.sns;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.text.TextUtils;
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import rx.Observable;
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
///**
// * 图片处理的帮助类
// * Created by leiweibo on 12/21/16.
// */
//
//public class ImageHelper {
//
//  private Context context;
//
//  public ImageHelper(Context context) {
//    this.context = context;
//  }
//
//  /**
//   * 将bitmap保存到本地sdcard目录
//   * 分享给QQ，如果是发送本地图片的话，那么需要对本地图片做一些处理.
//   *
//   * @param bitmap bitmap对象
//   * @param callback 保存之后的回调
//   */
//  public void saveBitmapToDisk(final Bitmap bitmap, final ImageSaveCallback callback) {
//    Observable.create(new Observable.OnSubscribe<String>() {
//      @Override public void call(Subscriber<? super String> subscriber) {
//        String fileName = String.valueOf(System.currentTimeMillis());
//        File file = new DiskHelper().createDir(context, "/wb_share/tmp/");
//        String path = writeBitmap(file.getPath(), fileName, bitmap);
//
//        subscriber.onNext(path);
//        subscriber.onCompleted();
//      }
//    })
//        .observeOn(Schedulers.io())
//        .subscribeOn(AndroidSchedulers.mainThread())
//        .subscribe(new Subscriber<String>() {
//          @Override public void onCompleted() {
//
//          }
//
//          @Override public void onError(Throwable e) {
//
//          }
//
//          @Override public void onNext(String s) {
//            if (callback != null) {
//              if (TextUtils.isEmpty(s)) {
//                callback.onFailed();
//              } else {
//                callback.onComplete(s);
//              }
//            }
//          }
//        });
//  }
//
//  private boolean failed = false; //判断是否写入文件失败
//
//  /**
//   * 图片上传回调
//   */
//  public interface ImageSaveCallback {
//    void onFailed();
//
//    void onComplete(String path);
//  }
//}
