package com.skyfire.hipda.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SafeViewPager extends ViewPager {

  public SafeViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    boolean handled = false;
    try {
      handled = super.dispatchTouchEvent(ev);
    } catch (IllegalArgumentException e) {
      //Ignore
    }
    return handled;
  }
}
