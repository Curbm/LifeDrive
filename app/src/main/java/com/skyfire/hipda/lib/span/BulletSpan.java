package com.skyfire.hipda.lib.span;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

public class BulletSpan implements LeadingMarginSpan {

  private final int mGapWidth;
  private final boolean mWantColor;
  private final int mColor;

  private int mBulletRadius = 3;
  private static Path sBulletPath = null;
  public static final int STANDARD_GAP_WIDTH = (int) (Resources.getSystem().getDisplayMetrics()
      .density * 12);

  public BulletSpan() {
    mGapWidth = STANDARD_GAP_WIDTH;
    mWantColor = false;
    mColor = 0;
  }

  public BulletSpan(int gapWidth) {
    mGapWidth = gapWidth;
    mWantColor = false;
    mColor = 0;
  }

  public BulletSpan(int gapWidth, int color) {
    mGapWidth = gapWidth;
    mWantColor = true;
    mColor = color;
  }

  public BulletSpan(int gapWidth, int color, int bulletRadius) {
    mGapWidth = gapWidth;
    mWantColor = true;
    mColor = color;
    mBulletRadius = bulletRadius;
  }


  public int getLeadingMargin(boolean first) {
    return 2 * mBulletRadius + mGapWidth;
  }

  public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                int top, int baseline, int bottom,
                                CharSequence text, int start, int end,
                                boolean first, Layout l) {
    if (((Spanned) text).getSpanStart(this) == start) {
      Paint.Style style = p.getStyle();
      int oldcolor = 0;

      if (mWantColor) {
        oldcolor = p.getColor();
        p.setColor(mColor);
      }

      p.setStyle(Paint.Style.FILL);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
          c.isHardwareAccelerated()) {
        if (sBulletPath == null) {
          sBulletPath = new Path();
          // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
          sBulletPath.addCircle(0.0f, 0.0f, mBulletRadius, Path.Direction.CW);
        }

        c.save();
        c.translate(x - dir * mBulletRadius*2+mGapWidth, (top + bottom) / 2.0f);
        c.drawPath(sBulletPath, p);
        c.restore();
      } else {
        c.drawCircle(x - dir * mBulletRadius*2+mGapWidth, (top + bottom) / 2.0f, mBulletRadius, p);
      }

      if (mWantColor) {
        p.setColor(oldcolor);
      }

      p.setStyle(style);
    }
  }
}