package com.skyfire.hipda.misc;

/**
 * Created by Neil
 */
public final class UrlHelper {

  public static final String BASE_URL = "http://www.hi-pda.com/forum/";

  public static String getAvatarUrl(int uid) {
    int i1 = uid / 10000;
    int i2 = uid / 100 % 100;
    int i3 = uid % 100;
    return String.format(BASE_URL + "uc_server/data/avatar/000/%1$02d/%2$02d/%3$02d_avatar_middle" +
        ".jpg", i1, i2, i3);
  }


}
