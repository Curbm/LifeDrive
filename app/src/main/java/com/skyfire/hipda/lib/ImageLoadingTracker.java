package com.skyfire.hipda.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Neil
 */
public class ImageLoadingTracker implements ImageLoadingProgressListener,
    ImageLoadingListener {

  Map<String, TrackRecord> mImageLoadingMap = new HashMap<>();

  public TrackRecord track(String uri, ImageView imageView, ImageLoadingListener listener,
                           ImageLoadingProgressListener progressListener) {
    TrackRecord record = mImageLoadingMap.get(uri);
    if (record == null) {
      record = new TrackRecord();
      mImageLoadingMap.put(uri, record);
    }
    record.mProgressListener = progressListener;
    record.mListener = listener;
    record.mImageView = imageView;
    return record;
  }

  public void release(Context context) {
    for (TrackRecord trackRecord : mImageLoadingMap.values()) {
      WrapImageLoader.get(context).cancelDisplayTask(trackRecord.mViewAware);
    }
    mImageLoadingMap.clear();
  }

  @Override
  public void onProgressUpdate(String imageUri, View view, int current, int total) {
    TrackRecord record = mImageLoadingMap.get(imageUri);
    if (record != null) {
      record.mProgressListener.onProgressUpdate(imageUri, view, current, total);
    }
  }

  @Override
  public void onLoadingStarted(String imageUri, View view) {
    TrackRecord record = mImageLoadingMap.get(imageUri);
    if (record != null) {
      record.mListener.onLoadingStarted(imageUri, view);
      record.mLoading = true;
    }
  }

  @Override
  public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
    TrackRecord record = mImageLoadingMap.get(imageUri);
    if (record != null) {
      record.mListener.onLoadingFailed(imageUri, view, failReason);
      record.mLoading = false;
    }
  }

  @Override
  public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
    TrackRecord record = mImageLoadingMap.get(imageUri);
    if (record != null) {
      record.mListener.onLoadingComplete(imageUri, view, loadedImage);
      record.mLoading = false;
    }
  }

  @Override
  public void onLoadingCancelled(String imageUri, View view) {
    TrackRecord record = mImageLoadingMap.get(imageUri);
    if (record != null) {
      record.mListener.onLoadingCancelled(imageUri, view);
      record.mLoading = false;
    }
  }

  public static class TrackRecord {
    private boolean mLoading;
    private ImageView mImageView;
    private ImageLoadingProgressListener mProgressListener;
    private ImageLoadingListener mListener;
    private NonViewAware mViewAware = new NonViewAware(new ImageSize(0, 0), ViewScaleType.CROP) {
      @Override
      public boolean setImageBitmap(Bitmap bitmap) {
        if (mImageView != null) {
          mImageView.setImageBitmap(bitmap);
        }
        return true;
      }
    };

    public boolean isLoading() {
      return mLoading;
    }

    public ImageAware getImageAwareForLoad() {
      return mViewAware;
    }
  }


}
