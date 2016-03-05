package com.skyfire.hipda.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.skyfire.hipda.misc.Util;


/**
 * Created by Neil
 */
public class LoadingBar extends View {

  static final int[] ATTR = new int[]{android.R.attr.textColor};
  static final int UNIT_DURATION_MS = 800;// segment + gap
  static final int BACKGROUND_ALPHA_MASK = 0x40000000;

  private final Paint mPaint;
  private final int mProgressColor;
  private final float mSegmentWidth;
  private final float mGapWidth;
  private long mStartTime;


  public LoadingBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray ta = context.obtainStyledAttributes(attrs, ATTR);
    mProgressColor = ta.getColor(0, Color.BLACK);
    ta.recycle();

    setBackgroundColor(mProgressColor & 0x00FFFFFF | BACKGROUND_ALPHA_MASK);
    mPaint = new Paint();
    mPaint.setColor(mProgressColor);
    mPaint.setStyle(Paint.Style.STROKE);
    mSegmentWidth = Util.dp(context, 100);
    mGapWidth = Util.dp(context, 8);

    mStartTime = AnimationUtils.currentAnimationTimeMillis();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    int width = canvas.getWidth();
    int height = canvas.getHeight();

    long now = AnimationUtils.currentAnimationTimeMillis();
    long elapsed = (now - mStartTime) % UNIT_DURATION_MS;
    float progress = 1 - (float) elapsed / UNIT_DURATION_MS;
    float startX = -(mSegmentWidth + mGapWidth) * progress;

    mPaint.setStrokeWidth(height);
    while (startX < width) {
      float y = height / 2f;
      canvas.drawLine(startX, y, startX + mSegmentWidth, y, mPaint);
      startX += mSegmentWidth;
      startX += mGapWidth;
    }


    ViewCompat.postInvalidateOnAnimation(this);
  }
}
