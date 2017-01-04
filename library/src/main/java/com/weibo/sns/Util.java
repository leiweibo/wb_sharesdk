package com.weibo.sns;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by leiweibo on 12/20/16.
 */

public class Util {
  private static Toast toast;

  public static final void showResultDialog(Context context, String msg,
      String title) {
    if(msg == null) return;
    String rmsg = msg.replace(",", "\n");
    Log.d("Util", rmsg);
    new AlertDialog.Builder(context).setTitle(title).setMessage(rmsg)
        .setNegativeButton("知道了", null).create().show();
  }

  /**
   * 打印消息并且用Toast显示消息
   *
   * @param activity
   * @param message
   */
  public static final void toastMessage(final Context activity,
      final String message) {
    //Log.d("ShareSDK", message);
    ((Activity)activity).runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (toast != null) {
          toast.cancel();
          toast = null;
        }
        toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.show();
      }
    });
  }

  /**
   * 打印消息并且用Toast显示消息
   *
   * @param activity
   * @param msgRes: 消息的resource id
   */
  public static final void toastMessage(final Context activity,
      final int msgRes) {
    String message = activity.getString(msgRes);
    toastMessage(activity, message);
  }

  public static Bitmap getDefaultBitmap(Context context) {
    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
    return bitmap;
  }

  /**
   * 显示自定义信息进度条
   *
   * @param message 要显示的信息内容
   */
  protected static ProgressDialog progressDialog;
  public static void showProgressDialog(Context context, String message) {
    createProgressDialog(context);
    progressDialog.setMessage(message);
    if (!progressDialog.isShowing()) {
      progressDialog.show();
    }
  }

  public static void dismissProgressDialog() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }

  private static void createProgressDialog(Context context) {
    progressDialog = new ProgressDialog(context);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setMessage("正在加载，请稍等...");
    progressDialog.setCancelable(true);
  }

}

