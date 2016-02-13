package com.skyfire.hipda.db;

/**
 * Created by Neil
 */
public class TableForum {

  public static final String COL_ID = "id";
  public static final String COL_TITLE = "title";

  public static final int INDEX_ID = 0;
  public static final int INDEX_TITLE = 1;

  public static final String TABLE_NAME = "forum";

  public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "("
      + COL_ID + " INTEGER,"
      + COL_TITLE + " TEXT)";
}
