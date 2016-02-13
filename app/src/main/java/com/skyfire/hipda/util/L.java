package com.skyfire.hipda.util;


import android.util.Log;
import com.skyfire.hipda.BuildConfig;

public class L {

  private static final String TAG = "[LifeDrive]";

  private L() {
  }


  public static void v(String message, Object... args) {
    log(Log.VERBOSE, null, message, args);
  }

  public static void d(String message, Object... args) {
    log(Log.DEBUG, null, message, args);
  }

  public static void i(String message, Object... args) {
    log(Log.INFO, null, message, args);
  }

  public static void w(String message, Object... args) {
    log(Log.WARN, null, message, args);
  }

  public static void e(String message, Object... args) {
    log(Log.ERROR, null, message, args);
  }

  public static void e(Throwable ex) {
    log(Log.ERROR, ex, null);
  }

  public static void e(Throwable ex, String message, Object... args) {
    log(Log.ERROR, ex, message, args);
  }

  private static void log(int priority, Throwable ex, String message, Object... args) {
    if (!BuildConfig.DEBUG) {
      return;
    }
    if (args.length > 0) {
      message = String.format(message, args);
    }

    String log;
    if (ex == null) {
      log = message == null ? "" : message;
    } else {
      String logMessage = message == null ? ex.getMessage() : message;
      String logBody = Log.getStackTraceString(ex);
      log = String.format("$1%s\n$2%s", logMessage, logBody);
    }
    Log.println(priority, TAG, log);
  }

}
