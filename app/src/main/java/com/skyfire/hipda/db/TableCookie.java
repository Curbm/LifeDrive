package com.skyfire.hipda.db;

/**
 * Created by Neil
 */
public class TableCookie {

  public static final String COL_COMMENT = "comment";
  public static final String COL_COMMENT_URL = "comment_url";
  public static final String COL_DISCARD = "discard";
  public static final String COL_DOMAIN = "domain";
  public static final String COL_MAX_AGE = "max_age";
  public static final String COL_NAME = "name";
  public static final String COL_PATH = "path";
  public static final String COL_PORT_LIST = "port_list";
  public static final String COL_SECURE = "secure";
  public static final String COL_URI = "uri";
  public static final String COL_VALUE = "value";
  public static final String COL_VERSION = "version";

  public static final int INDEX_COMMENT = 0;
  public static final int INDEX_COMMENT_URL = 1;
  public static final int INDEX_DISCARD = 2;
  public static final int INDEX_DOMAIN = 3;
  public static final int INDEX_MAX_AGE = 4;
  public static final int INDEX_NAME = 5;
  public static final int INDEX_PATH = 6;
  public static final int INDEX_PORT_LIST = 7;
  public static final int INDEX_SECURE = 8;
  public static final int INDEX_URI = 9;
  public static final int INDEX_VALUE = 10;
  public static final int INDEX_VERSION = 11;

  public static final String TABLE_NAME = "cookie";
  public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "("
      + COL_COMMENT + " TEXT,"
      + COL_COMMENT_URL + " TEXT,"
      + COL_DISCARD + " INTEGER,"
      + COL_DOMAIN + " TEXT,"
      + COL_MAX_AGE + " INTEGER,"
      + COL_NAME + " TEXT UNIQUE,"
      + COL_PATH + " TEXT,"
      + COL_PORT_LIST + " TEXT,"
      + COL_SECURE + " INTEGER,"
      + COL_URI + " TEXT,"
      + COL_VALUE + " TEXT,"
      + COL_VERSION + " INTEGER)";
}
