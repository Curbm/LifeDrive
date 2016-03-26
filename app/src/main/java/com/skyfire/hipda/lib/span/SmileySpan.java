package com.skyfire.hipda.lib.span;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.style.ReplacementSpan;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by Neil
 */
public class SmileySpan extends ReplacementSpan {

  /**
   * A constant indicating that the bottom of this span should be aligned
   * with the bottom of the surrounding text, i.e., at the same level as the
   * lowest descender in the text.
   */
  public static final int ALIGN_BOTTOM = 0;

  /**
   * A constant indicating that the bottom of this span should be aligned
   * with the baseline of the surrounding text.
   */
  public static final int ALIGN_BASELINE = 1;

  protected final int mVerticalAlignment;

  private final int mSize;
  private int mResourceId;
  private Context mContext;
  private WeakReference<Drawable> mDrawableRef;

  public SmileySpan(Context context, @DrawableRes int resourceId, int size) {
    mContext = context;
    mResourceId = resourceId;
    mSize = (int) context.getResources().getDisplayMetrics().density * size;
    mVerticalAlignment = ALIGN_BOTTOM;
  }

  @Override
  public int getSize(Paint paint, CharSequence text,
                     int start, int end,
                     Paint.FontMetricsInt fm) {
    Drawable d = getCachedDrawable();

    Rect rect = d.getBounds();

    if (fm != null) {
      fm.ascent = -rect.bottom;
      fm.descent = 0;

      fm.top = fm.ascent;
      fm.bottom = 0;
    }

    return rect.right;
  }

  @Override
  public void draw(Canvas canvas, CharSequence text,
                   int start, int end, float x,
                   int top, int y, int bottom, Paint paint) {
    Drawable d = getCachedDrawable();
    canvas.save();

    int transY = bottom - d.getBounds().bottom;
    if (mVerticalAlignment == ALIGN_BASELINE) {
      transY -= paint.getFontMetricsInt().descent;
    }

    canvas.translate(x, transY);
    d.draw(canvas);
    canvas.restore();
  }

  private Drawable getCachedDrawable() {
    WeakReference<Drawable> wr = mDrawableRef;
    Drawable d = null;

    if (wr != null) {
      d = wr.get();
    }

    if (d == null) {
      d = getDrawable();
      mDrawableRef = new WeakReference<>(d);
    }

    return d;
  }

  public Drawable getDrawable() {
    Drawable drawable = null;
    try {
      drawable = mContext.getResources().getDrawable(mResourceId);
      drawable.setBounds(0, 0, mSize, mSize);
    } catch (Exception e) {
      Log.e("sms", "Unable to find resource: " + mResourceId);
    }
    return drawable;
  }

}
