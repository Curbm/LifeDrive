package com.skyfire.hipda.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Neil
 */
public class AutoFitImageView extends ImageView {

  public AutoFitImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    Drawable drawable = getDrawable();
    if (drawable == null) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    } else if (widthMode != MeasureSpec.UNSPECIFIED) {
      int drawableWidth = drawable.getIntrinsicWidth();
      int drawableHeight = drawable.getIntrinsicHeight();
      heightSize = (int) ((float) drawableHeight * widthSize / drawableWidth + .5f);
      setMeasuredDimension(widthSize, heightSize);
    }
  }
}
