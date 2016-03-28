package com.skyfire.hipda.misc;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public final class Util {

  static MovementMethod sLinkMovementMethod = new BaseMovementMethod() {
    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
      // Copy from source to prevent TextView from intercepting all touch events
      int action = event.getAction();

      if (action == MotionEvent.ACTION_UP ||
          action == MotionEvent.ACTION_DOWN) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

        if (link.length != 0) {
          if (action == MotionEvent.ACTION_UP) {
            link[0].onClick(widget);
          }
          return true;
        }
      }
      return false;
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
      super.initialize(widget, text);
    }
  };

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

  public static void setLinkMovementMethod(TextView textView) {
    textView.setMovementMethod(sLinkMovementMethod);
    // setMovementMethod will set these properties to true
    textView.setClickable(false);
    textView.setFocusable(false);
    textView.setLongClickable(false);
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
