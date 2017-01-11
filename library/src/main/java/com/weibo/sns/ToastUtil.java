package com.weibo.sns;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
  private static Toast toast;

  /**
   * 显示单例的toast，能连续快速弹的吐司
   */
  public static void showToast(Context context, String text) {
    if (toast == null) {
      toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
    }
    toast.setText(text);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }

  public static void showToast(Context context, int text) {
    if (toast == null) {
      toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
    }
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.setText(text);
    toast.show();
  }
}
