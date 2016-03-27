package com.skyfire.hipda.misc;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Neil
 */
public class PrefHelper {

  public static final String PREF_FILE = "app_preferences";

  private enum Key {
    THREAD_PER_PAGE,
    POST_PER_PAGE,
    DRAWER_SELECTED_POSITION,
    FORUM_HASH
  }

  private static SharedPreferences mSP;

  public static void init(Context context) {
    mSP = context.getSharedPreferences(PREF_FILE, 0);
  }

  public static int getThreadPageCount() {
    return mSP.getInt(Key.THREAD_PER_PAGE.name(), 50);
  }

  public static int getPostPageCount() {
    return mSP.getInt(Key.POST_PER_PAGE.name(), 50);
  }

  public static void setThreadPageCount(int count) {
    mSP.edit().putInt(Key.THREAD_PER_PAGE.name(), count).apply();
  }


  public static void setPostPageCount(int count) {
    mSP.edit().putInt(Key.POST_PER_PAGE.name(), count).apply();
  }

  public static int getDrawerSelectedPosition() {
    return mSP.getInt(Key.DRAWER_SELECTED_POSITION.name(), 0);
  }

  public static void setDrawerSelectedPosition(int position) {
    mSP.edit().putInt(Key.DRAWER_SELECTED_POSITION.name(), position).apply();
  }

  public static void setForumHash(String forumHash) {
    mSP.edit().putString(Key.FORUM_HASH.name(), forumHash).apply();
  }

  public static String getForumHash() {
    return mSP.getString(Key.FORUM_HASH.name(), null);
  }

}
