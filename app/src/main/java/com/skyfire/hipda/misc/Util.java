package com.skyfire.hipda.misc;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public final class Util {

  static MovementMethod sLinkMovementMethod = new LinkMovementMethod();

  static ThreadLocal<HashMap<String, SimpleDateFormat>> sDateFormatPool = new
      ThreadLocal<HashMap<String, SimpleDateFormat>>() {
        @Override
        protected HashMap<String, SimpleDateFormat> initialValue() {
          return new HashMap<>();
        }
      };

  public static SimpleDateFormat getDateFormat(String format) {
    HashMap<String, SimpleDateFormat> map = sDateFormatPool.get();
    SimpleDateFormat sdf = map.get(format);
    if (sdf == null) {
      sdf = new SimpleDateFormat(format, Locale.getDefault());
      map.put(format, sdf);
    }
    return sdf;
  }

  public static MovementMethod getLinkMovementMethod() {
    return sLinkMovementMethod;
  }


  public static float dp(Context context, float dp) {
    return context.getResources().getDisplayMetrics().density * dp;
  }

  public static float sp(Context context, float sp) {
    return context.getResources().getDisplayMetrics().scaledDensity * sp;
  }

  public static int parseIntNoThrow(String str) {
    int i = -1;
    try {
      i = Integer.parseInt(str);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return i;
  }


}
