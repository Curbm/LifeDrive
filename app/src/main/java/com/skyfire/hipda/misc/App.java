package com.skyfire.hipda.misc;

import android.app.Application;
import com.skyfire.hipda.lib.WrapImageLoader;

/**
 * Created by Neil
 */
public class App extends Application {

  static App mApp;

  @Override
  public void onCreate() {
    super.onCreate();
    mApp = this;
    PrefHelper.init(this);
  }

  public static App getInstance() {
    return mApp;
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    WrapImageLoader.get(this).getLoader().clearMemoryCache();
  }
}
