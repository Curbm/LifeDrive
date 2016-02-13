package com.skyfire.hipda.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.skyfire.hipda.bean.Forum;
import com.skyfire.hipda.misc.App;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public final class DbHelper extends SQLiteOpenHelper {


  private static DbHelper sInstance;
  private SQLiteDatabase rDb;
  private SQLiteDatabase wDb;

  public DbHelper(Context context) {
    super(context, "app.db", null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(TableCookie.CREATE_SQL);
    db.execSQL(TableForum.CREATE_SQL);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }

  public static DbHelper getInstance() {
    if (sInstance == null) {
      synchronized (DbHelper.class) {
        if (sInstance == null) {
          sInstance = new DbHelper(App.getInstance());
          sInstance.rDb = sInstance.getReadableDatabase();
          sInstance.wDb = sInstance.getWritableDatabase();
        }
      }
    }
    return sInstance;
  }

  public void updateForumList(List<Forum> list) {
    wDb.delete(TableForum.TABLE_NAME, null, null);
    ContentValues cv = new ContentValues(3);
    for (Forum forum : list) {
      cv.put(TableForum.COL_ID, forum.getId());
      cv.put(TableForum.COL_TITLE, forum.getTitle());
      wDb.insert(TableForum.TABLE_NAME, null, cv);
    }
  }

  public List<Forum> getForumList() {
    Cursor c = rDb.rawQuery("SELECT * FROM " + TableForum.TABLE_NAME, null);
    try {
      List<Forum> list = new ArrayList<>(c.getCount());
      while (c.moveToNext()) {
        Forum forum = new Forum(
            c.getInt(TableForum.INDEX_ID),
            c.getString(TableForum.INDEX_TITLE)
        );
        list.add(forum);
      }
      return list;
    } finally {
      c.close();
    }
  }

  public void addCookie(URI uri, HttpCookie cookie) {
    ContentValues cv = new ContentValues(12);
    cv.put(TableCookie.COL_COMMENT, cookie.getComment());
    cv.put(TableCookie.COL_COMMENT_URL, cookie.getCommentURL());
    cv.put(TableCookie.COL_DISCARD, cookie.getDiscard());
    cv.put(TableCookie.COL_DOMAIN, cookie.getDomain());
    cv.put(TableCookie.COL_MAX_AGE, cookie.getMaxAge());
    cv.put(TableCookie.COL_NAME, cookie.getName());
    cv.put(TableCookie.COL_PATH, cookie.getPath());
    cv.put(TableCookie.COL_PORT_LIST, cookie.getPortlist());
    cv.put(TableCookie.COL_SECURE, cookie.getSecure());
    cv.put(TableCookie.COL_URI, uri.getHost());
    cv.put(TableCookie.COL_VALUE, cookie.getValue());
    cv.put(TableCookie.COL_VERSION, cookie.getVersion());
    wDb.insertWithOnConflict(TableCookie.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
  }

  public List<HttpCookie> getCookie(URI uri) {
    Cursor c = rDb.rawQuery("SELECT * FROM " + TableCookie.TABLE_NAME + " WHERE " + TableCookie
        .COL_URI + "=?", new String[]{uri.getHost()});
    try {
      List<HttpCookie> list = new ArrayList<>(c.getCount());
      while (c.moveToNext()) {
        list.add(parseCookie(c));
      }
      return list;
    } finally {
      c.close();
    }
  }

  public List<HttpCookie> getCookies() {
    Cursor c = rDb.rawQuery("SELECT * FROM " + TableCookie.TABLE_NAME, null);
    try {
      List<HttpCookie> list = new ArrayList<>(c.getCount());
      while (c.moveToNext()) {
        list.add(parseCookie(c));
      }
      return list;
    } finally {
      c.close();
    }
  }

  private static HttpCookie parseCookie(Cursor c) {
    HttpCookie cookie = new HttpCookie(c.getString(TableCookie.INDEX_NAME), c.getString
        (TableCookie.INDEX_VALUE));
    cookie.setComment(c.getString(TableCookie.INDEX_COMMENT));
    cookie.setCommentURL(c.getString(TableCookie.INDEX_COMMENT_URL));
    cookie.setDiscard(c.getInt(TableCookie.INDEX_DISCARD) == 1);
    cookie.setDomain(c.getString(TableCookie.INDEX_DOMAIN));
    cookie.setMaxAge(c.getLong(TableCookie.INDEX_MAX_AGE));
    cookie.setPath(c.getString(TableCookie.INDEX_PATH));
    cookie.setPortlist(c.getString(TableCookie.INDEX_PORT_LIST));
    cookie.setSecure(c.getInt(TableCookie.INDEX_SECURE) == 1);
    cookie.setVersion(c.getInt(TableCookie.INDEX_VERSION));
    return cookie;
  }

  public List<URI> getCookieURIs() {
    Cursor c = rDb.rawQuery("SELECT DISTINCT " + TableCookie.COL_URI + " FROM " + TableCookie
        .TABLE_NAME, null);
    try {
      List<URI> list = new ArrayList<>(c.getCount());
      while (c.moveToNext()) {
        list.add(new URI(c.getString(0)));
      }
      return list;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return null;
    } finally {
      c.close();
    }
  }

  public int removeCookie(URI uri, HttpCookie cookie) {
    return wDb.delete(TableCookie.TABLE_NAME, TableCookie.COL_URI + "=? AND " + TableCookie
        .COL_NAME + "=?s", new String[]{uri.getHost(), cookie.getName()});
  }

  public int removeAllCookie() {
    return wDb.delete(TableCookie.TABLE_NAME, "1", null);
  }

  // TODO: 2016/2/13 check expire time?
  public boolean isAuthCookieValid() {
    Cursor c = rDb.rawQuery("SELECT * FROM " + TableCookie.TABLE_NAME + " WHERE " + TableCookie
        .COL_NAME + "=?", new String[]{"cdb_auth"});
    try {
      return c.getCount() > 0;
    } finally {
      c.close();
    }
  }
}
