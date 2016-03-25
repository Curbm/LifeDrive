package com.skyfire.hipda.lib;

import android.content.Context;
import android.widget.ImageView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.Arrays;

public final class WrapImageLoader {

  private static WrapImageLoader singleton;
  private ImageLoader mLoader;

  public static WrapImageLoader get(Context context) {
    if (singleton == null) {
      singleton = new WrapImageLoader(context);
    }
    return singleton;
  }


  private WrapImageLoader(Context context) {
    if (mLoader == null) {
      mLoader = ImageLoader.getInstance();
    }
    if (!mLoader.isInited()) {
      ImageLoaderConfiguration config = new ImageLoaderConfiguration
          .Builder(context)
          .diskCacheFileNameGenerator(new Md5FileNameGenerator())
          .build();
      mLoader.init(config);
    }
  }


  public ImageLoader getLoader() {
    return mLoader;
  }

  public Builder load(String uri) {
    return new Builder(uri, mLoader);
  }

  public void cancelDisplayTask(ImageView imageView) {
    mLoader.cancelDisplayTask(imageView);
  }

  public void cancelDisplayTask(ImageAware imageAware) {
    mLoader.cancelDisplayTask(imageAware);
  }

  public static class Builder {

    boolean cacheOnDisk;
    boolean cacheInMemory;
    boolean resetBeforeLoad;
    int delayBeforeLoad;
    int loadingImageRes;
    int emptyImageRes;
    int failImageRes;

    float[] variantRound;
    float round;
    boolean circle;
    boolean fadeIn;

    ImageScaleType scaleType = ImageScaleType.IN_SAMPLE_POWER_OF_2;
    ImageLoadingListener loadingListener;
    ImageLoadingProgressListener progressListener;
    ImageLoader loader;
    String uri;

    Builder(String uri, ImageLoader loader) {
      this.uri = uri;
      this.loader = loader;
    }

    public Builder cacheOnDisk() {
      cacheOnDisk = true;
      return this;
    }

    public Builder cacheInMemory() {
      cacheInMemory = true;
      return this;
    }

    public Builder delayBeforeLoad(int millis) {
      delayBeforeLoad = millis;
      return this;
    }

    private void resetDisplayerTypes() {
      circle = false;
      round = 0;
      variantRound = null;
    }

    public Builder circle() {
      resetDisplayerTypes();
      circle = true;
      return this;
    }

    public Builder round(float px) {
      resetDisplayerTypes();
      round = px;
      return this;
    }

    public Builder round(float topLeft, float topRight, float bottomRight, float bottomLeft) {
      resetDisplayerTypes();
      if (variantRound == null) {
        variantRound = new float[8];
      }
      Arrays.fill(variantRound, 0);
      if (topLeft > 0) {
        variantRound[0] = topLeft;
        variantRound[1] = topLeft;
      }
      if (topRight > 0) {
        variantRound[2] = topRight;
        variantRound[3] = topRight;
      }
      if (bottomRight > 0) {
        variantRound[4] = bottomRight;
        variantRound[5] = bottomRight;
      }
      if (bottomLeft > 0) {
        variantRound[6] = bottomLeft;
        variantRound[7] = bottomLeft;
      }
      return this;
    }


    public Builder loadingImage(int loadingImageRes) {
      this.loadingImageRes = loadingImageRes;
      return this;
    }

    public Builder emptyImage(int emptyImageRes) {
      this.emptyImageRes = emptyImageRes;
      return this;
    }

    public Builder failImage(int failImageRes) {
      this.failImageRes = failImageRes;
      return this;
    }


    /**
     * for image loading process. Listener fires events on UI thread if this method is called
     * on UI thread.
     */
    public Builder loadingListener(ImageLoadingListener listener) {
      loadingListener = listener;
      return this;
    }

    /**
     * for image loading progress. Listener fires events on UI thread if this method is
     * called on UI thread. Caching on disk should be enabled in {@linkplain com.nostra13
     * .universalimageloader.core .DisplayImageOptions options} to make this listener work.
     */
    public Builder progressListener(ImageLoadingProgressListener listener) {
      progressListener = listener;
      return this;
    }

    /**
     * Sets whether {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
     * image aware view} will be reset (set <b>null</b>) before image loading start
     */
    public Builder resetBeforeLoad() {
      resetBeforeLoad = true;
      return this;
    }

    /**
     * Sets {@linkplain ImageScaleType scale type} for decoding image. This parameter is used
     * while define scale size for decoding image to Bitmap. Default value - {@link
     * ImageScaleType#IN_SAMPLE_POWER_OF_2}
     */
    public Builder scaleType(ImageScaleType scaleType) {
      this.scaleType = scaleType;
      return this;
    }

    public void into(ImageView imageView) {
      into(new ImageViewAware(imageView));
    }

    public void into(ImageAware imageAware) {
      DisplayImageOptions.Builder builder = new DisplayImageOptions
          .Builder()
          .cacheOnDisk(cacheOnDisk)
          .cacheInMemory(cacheInMemory)
          .delayBeforeLoading(delayBeforeLoad)
          .resetViewBeforeLoading(resetBeforeLoad)
          .imageScaleType(scaleType);
      if (loadingImageRes != 0) {
        builder.showImageOnLoading(loadingImageRes);
      }
      if (emptyImageRes != 0) {
        builder.showImageForEmptyUri(emptyImageRes);
      }
      if (failImageRes != 0) {
        builder.showImageOnFail(failImageRes);
      }
      if (round > 0) {
        builder.displayer(new RoundedBitmapDisplayer((int) round));
      } else {
        if (variantRound != null) {
          builder.displayer(new VariantRoundBitmapDisplayer(variantRound));
        } else {
          if (circle) {
            builder.displayer(new CircleBitmapDisplayer());
          }
        }
      }

      loader.displayImage(uri, imageAware, builder.build(), loadingListener, progressListener);
    }
  }

}
