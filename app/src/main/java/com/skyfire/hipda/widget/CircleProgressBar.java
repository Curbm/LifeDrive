package com.skyfire.hipda.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.skyfire.hipda.misc.Util;

/**
 * Created by Neil
 */
public class CircleProgressBar extends View {

  static final int[] ATTR = new int[]{android.R.attr.textColor};
  private final int mProgressColor;
  private final float mStrokeWidth;
  private float mProgress;

  private Paint mPaint;
  private RectF mRectF = new RectF();

  public CircleProgressBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray ta = context.obtainStyledAttributes(attrs, ATTR);
    mProgressColor = ta.getColor(0, Color.BLACK);
    ta.recycle();

    mStrokeWidth = Util.dp(context, 4);
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint.setStrokeWidth(mStrokeWidth);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setColor(mProgressColor);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mRectF.set(0, 0, w, h);
    mRectF.inset(mStrokeWidth / 2f, mStrokeWidth / 2f);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    float startAngle = -90;
    float endAngle = 360 * mProgress;

    canvas.drawArc(mRectF, startAngle, endAngle, false, mPaint);

  }

  public void setProgress(float progress) {
    mProgress = progress;
    invalidate();
  }

}
