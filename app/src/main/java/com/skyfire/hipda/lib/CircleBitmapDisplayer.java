/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.skyfire.hipda.lib;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;


public class CircleBitmapDisplayer implements BitmapDisplayer {

  protected final int cornerRadius;

  protected final int margin;

  public CircleBitmapDisplayer() {
    this(0, 0);
  }

  public CircleBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
    this.cornerRadius = cornerRadiusPixels;
    this.margin = marginPixels;
  }

  @Override
  public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
    if (!(imageAware instanceof ImageViewAware)) {
      throw new IllegalArgumentException(
          "ImageAware should wrap ImageView. ImageViewAware is expected.");
    }

    imageAware.setImageDrawable(new CircleDrawable(bitmap, cornerRadius, margin));
  }

  public static class CircleDrawable extends Drawable {

    protected final float cornerRadius;

    protected final int margin;

    protected final RectF mRect = new RectF(),
        mBitmapRect;

    protected final BitmapShader bitmapShader;

    protected final Paint paint;

    public CircleDrawable(Bitmap bitmap, int cornerRadius, int margin) {
      this.cornerRadius = cornerRadius;
      this.margin = margin;

      bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      mBitmapRect = new RectF(margin, margin, bitmap.getWidth() - margin,
          bitmap.getHeight() - margin);
      paint = new Paint();
      paint.setAntiAlias(true);
      paint.setShader(bitmapShader);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
      super.onBoundsChange(bounds);
      mRect.set(margin, margin, bounds.width() - margin, bounds.height() - margin);

      // Resize the original bitmap to fit the new bound
      Matrix shaderMatrix = new Matrix();
      shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
      bitmapShader.setLocalMatrix(shaderMatrix);
    }

    @Override
    public void draw(Canvas canvas) {
      float cx = mRect.centerX();
      float cy = mRect.centerY();
      float r = Math.min(mRect.width() / 2, mRect.height() / 2);
      canvas.drawCircle(cx, cy, r, paint);
    }

    @Override
    public int getOpacity() {
      return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
      paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
      paint.setColorFilter(cf);
    }
  }
}
