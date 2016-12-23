package com.weibo.sns;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;
import com.jakewharton.disklrucache.DiskLruCache;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by leiweibo on 12/22/16.
 */

public class DiskCacheUtil {
  private Context mContext;

  //定义DiskLruCache
  private DiskLruCache mDiskCache;
  //指定磁盘缓存大小
  private static final long DISK_CACHE_SIZE = 1024 * 1024 * 10;//10MB
  //IO缓存流大小
  private static final int IO_BUFFER_SIZE = 8 * 1024;
  //缓存个数
  private static final int DISK_CACHE_INDEX = 0;
  //缓存文件是否创建
  private boolean mIsDiskLruCacheCreated = false;

  private final static String DISK_CACHE = "diskcache";

  public DiskCacheUtil(Context context) {
    mContext = context.getApplicationContext();
    //得到缓存文件
    File diskCacheDir = createOrGetDiskCacheDir(mContext, DISK_CACHE);
    //如果文件不存在 直接创建
    if (!diskCacheDir.exists()) {
      diskCacheDir.mkdirs();
    }
    if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
      try {
        mDiskCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
        mIsDiskLruCacheCreated = true;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 将一个URL转换成bitmap对象, 先从本地缓存读取，如果不存在，那么则去网络下载
   *
   * @param urlStr 图片的URL
   */
  public Bitmap getBitmapFromURL(String urlStr) {
    try {
      Bitmap cachedBitmap = getBitmapFromDiskCache(urlStr);
      if (cachedBitmap == null) {
        return writeBitmapToDiskCache(urlStr);
      } else {
        return cachedBitmap;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 将本地的bitmap写入本地缓存目录，并把文件对象返回
   *
   * @param bitmap 将要写入的bitmap
   * @param fileName 文件名
   * @return 写入之后的缓存的文件
   * @throws IOException
   */
  public File writeLocalBitMapToCache(Bitmap bitmap, String fileName) throws IOException {
    //设置key，并根据URL保存输出流的返回值决定是否提交至缓存
    String key = hashKeyFormUrl(fileName);
    //得到Editor对象
    DiskLruCache.Editor editor = mDiskCache.edit(key);
    if (editor != null) {
      OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
      if (bitmap != null) {
        /*
         * 如果bitmap compress成png，则再去尝试 JPEG compress
         */
        boolean compressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        if (!compressed) {
          compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        }
        if (compressed) {
          editor.commit();
        } else {
          mDiskCache.flush();
          editor.abort();
          return null;
        }
      }
      mDiskCache.flush();
      return new File(getDiskCacheDir(mContext).getAbsolutePath() + File.separatorChar,
          key + "." + DISK_CACHE_INDEX);
    }

    return null;
  }

  /**
   * 获取缓存目录的路径
   */
  private File getDiskCacheDir(Context context) {
    return createOrGetDiskCacheDir(context, DISK_CACHE);
  }

  /**
   * 获取缓存文件
   *
   * @param context 上下文对象
   * @param filePath 文件路径
   * @return 返回一个文件
   */
  private File createOrGetDiskCacheDir(Context context, String filePath) {
    boolean externalStorageAvailable =
        Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    final String cachePath;
    if (externalStorageAvailable) {
      cachePath = context.getExternalCacheDir().getPath();
    } else {
      cachePath = context.getCacheDir().getPath();
    }

    return new File(cachePath + File.separator + filePath);
  }

  /**
   * 得到当前可用的空间大小
   *
   * @param path 文件的路径
   */
  private long getUsableSpace(File path) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      return path.getUsableSpace();
    }
    final StatFs stats = new StatFs(path.getPath());
    return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
  }

  /**
   * 将URL转换成key
   *
   * @param url 图片的URL
   */
  private String hashKeyFormUrl(String url) {
    String cacheKey;
    try {
      final MessageDigest mDigest = MessageDigest.getInstance("MD5");
      mDigest.update(url.getBytes());
      cacheKey = bytesToHexString(mDigest.digest());
    } catch (NoSuchAlgorithmException e) {
      cacheKey = String.valueOf(url.hashCode());
    }
    return cacheKey;
  }

  /**
   * 将Url的字节数组转换成哈希字符串
   *
   * @param bytes URL的字节数组
   */
  private String bytesToHexString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      String hex = Integer.toHexString(0xFF & bytes[i]);
      if (hex.length() == 1) {
        sb.append('0');
      }
      sb.append(hex);
    }
    return sb.toString();
  }

  /**
   * 将url的图片，现在到本地并将Bitmap写入缓存
   *
   * @param url 图片的url
   * @return bitmap 对象
   * @throws IOException
   */
  private Bitmap writeBitmapToDiskCache(String url) throws IOException {
    //如果当前线程是在主线程 则异常
    if (Looper.myLooper() == Looper.getMainLooper()) {
      throw new RuntimeException("can not visit network from UI Thread.");
    }
    if (mDiskCache == null) {
      return null;
    }

    //设置key，并根据URL保存输出流的返回值决定是否提交至缓存
    String key = hashKeyFormUrl(url);
    //得到Editor对象
    DiskLruCache.Editor editor = mDiskCache.edit(key);
    if (editor != null) {
      OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
      if (downloadUrlToStream(url, outputStream)) {
        //提交写入操作
        editor.commit();
      } else {
        //撤销写入操作
        editor.abort();
      }
      mDiskCache.flush();
    }
    return getBitmapFromDiskCache(url);
  }

  /**
   * 将URL中的图片保存到输出流中
   *
   * @param urlString 图片的URL地址
   * @param outputStream 输出流
   * @return 输出流
   */
  private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
    HttpURLConnection urlConnection = null;
    BufferedOutputStream out = null;
    BufferedInputStream in = null;
    try {
      final URL url = new URL(urlString);
      urlConnection = (HttpURLConnection) url.openConnection();
      in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
      out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
      int b;
      while ((b = in.read()) != -1) {
        out.write(b);
      }
      return true;
    } catch (final IOException e) {
      e.printStackTrace();
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * 从缓存中取出Bitmap
   *
   * @param url 图片的URL
   * @return 返回Bitmap对象
   * @throws IOException
   */
  private Bitmap getBitmapFromDiskCache(String url) throws IOException {
    //如果当前线程是主线程 则异常
    if (Looper.myLooper() == Looper.getMainLooper()) {
      Log.w("DiskLruCache", "load bitmap from UI Thread, it's not recommended!");
    }
    //如果缓存中为空  直接返回为空
    if (mDiskCache == null) {
      return null;
    }

    //通过key值在缓存中找到对应的Bitmap
    Bitmap bitmap = null;
    String key = hashKeyFormUrl(url);
    //通过key得到Snapshot对象
    DiskLruCache.Snapshot snapShot = mDiskCache.get(key);
    if (snapShot != null) {
      //得到文件输入流
      FileInputStream fileInputStream = (FileInputStream) snapShot.getInputStream(DISK_CACHE_INDEX);
      //得到文件描述符
      FileDescriptor fileDescriptor = fileInputStream.getFD();
      bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
    }
    return bitmap;
  }
}
