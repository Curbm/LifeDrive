/**
 * ****************************************************************************
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
 * *****************************************************************************
 */
package com.skyfire.hipda.lib;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class VariantRoundBitmapDisplayer implements BitmapDisplayer {

  protected final float[] cornerRadius;

  public VariantRoundBitmapDisplayer(float[] cornerRadiusPixels) {
    this.cornerRadius = cornerRadiusPixels;
  }


  @Override
  public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
    if (!(imageAware instanceof ImageViewAware)) {
      throw new IllegalArgumentException(
          "ImageAware should wrap ImageView. ImageViewAware is expected.");
    }

    imageAware.setImageDrawable(new RoundedDrawable(bitmap, cornerRadius));
  }

  public static class RoundedDrawable extends Drawable {

    protected final RectF rect = new RectF();
    protected final RectF bitmapRect;
    protected final BitmapShader bitmapShader;
    protected final Paint paint;
    protected final Path path;
    private final float[] radius;
    private final Bitmap bitmap;
    private Matrix matrix = new Matrix();


    public RoundedDrawable(Bitmap bitmap, float[] cornerRadius) {
      this.bitmap = bitmap;
      bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      bitmapRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
      radius = cornerRadius;
      path = new Path();
      paint = new Paint();
      paint.setAntiAlias(true);
      paint.setShader(bitmapShader);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
      super.onBoundsChange(bounds);
//            L.v(this + ":" + bounds.toShortString());
      rect.set(bounds);
      path.reset();
      path.addRoundRect(rect, radius, Path.Direction.CW);

      // simulate center-crop
      int dwidth = bitmap.getWidth();
      int dheight = bitmap.getHeight();

      int vwidth = bounds.width();
      int vheight = bounds.height();

      float scale;
      float dx = 0;
      float dy = 0;

      if (dwidth * vheight > vwidth * dheight) {
        scale = (float) vheight / (float) dheight;
        dx = (vwidth - dwidth * scale) * 0.5f;
      } else {
        scale = (float) vwidth / (float) dwidth;
        dy = (vheight - dheight * scale) * 0.5f;
      }

      matrix.setScale(scale, scale);
      matrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
      bitmapShader.setLocalMatrix(matrix);
    }

    @Override
    public void draw(Canvas canvas) {
      canvas.drawPath(path, paint);
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
