package com.niubang.uguma;

import android.app.Activity;
import android.content.Context;
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
    Log.d("ShareSDK", message);
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
}

