package com.weibo.sns;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * TODO:将过程转成rxjava实现，暂时用回调实现
 *
 * 分享给第三方，如果是发送本地图片的话，那么需要对本地图片做一些处理
 * Created by leiweibo on 12/21/16.
 */

public class BitmapHelper {

  public void saveBitmap(final Bitmap bitmap, final ImageSaveCallback callback) {
    Thread thread = new Thread(new Runnable() {
      @Override public void run() {
        File file = null;
        // 文件名用时间戳即可，因为对于并发以及同名影响并不是很大
        String fileName =String.valueOf(System.currentTimeMillis());
        if (hasSDCard()) {
          file = Environment.getExternalStorageDirectory();
        } else {
          file = Environment.getDataDirectory();
        }
        file = new File(file.getPath(), "/wb_share/tmp/");
        if (!file.isDirectory()) {
          file.delete();
          file.mkdirs();
        }

        if (!file.exists()) {
          file.mkdirs();
        }
        String path = writeBitmap(file.getPath(), fileName + ".png", bitmap);

        if (callback != null) {
          if (TextUtils.isEmpty(path)) {
            callback.onFailed();
          } else {
            callback.onComplete(path);
          }

        }
      }
    });
    thread.start();
  }

  private boolean failed = false; //判断是否写入文件失败
  /**
   * 将bitmap写入到本地
   *
   * @param path: 本地路径
   * @param name 文件名
   * @param bitmap bitmap对象
   * @return  完整的文件名, 如果处理异常，则返回null
   */
  private String writeBitmap(String path, String name, Bitmap bitmap) {
    failed = false;
    //文件目录如果不存在，那么创建目录
    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }

    //同名文件如果已经存在那么删除文件
    file = new File(path, name);
    if (file.exists()) {
      file.delete();
    }

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      if (!TextUtils.isEmpty(name)) {
        /*
         * 对于不同的文件类型，写入文件做不同处理
         */
        int index = name.lastIndexOf(".");
        if (index != -1 && (index + 1) < name.length()) {
          String extension = name.substring(index + 1).toLowerCase();
          if ("png".equals(extension)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
          } else if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
          }
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      failed = true;
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (failed) {
        return null;
      } else {
        return file.getAbsolutePath();
      }
    }
  }

  /**
   * 判断是否有sd卡
   *
   * @return true: 有sd卡
   */
  private boolean hasSDCard() {
    String SDState = android.os.Environment.getExternalStorageState();
    if (SDState.equals(android.os.Environment.MEDIA_MOUNTED)) {
      return true;
    }
    return false;
  }

  /**
   * 图片上传回调
   */
  public interface ImageSaveCallback {
    void onFailed();
    void onComplete(String path);
  }
}
