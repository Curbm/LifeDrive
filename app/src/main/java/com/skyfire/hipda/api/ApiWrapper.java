package com.skyfire.hipda.api;

import com.skyfire.hipda.BuildConfig;
import com.skyfire.hipda.db.DbHelper;
import com.skyfire.hipda.misc.UrlHelper;
import com.squareup.okhttp.OkHttpClient;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import java.net.*;
import java.util.List;

public class ApiWrapper {

  private static ApiList sInstance;

  public static ApiList api() {
    return sInstance;
  }

  static {
    OkHttpClient httpClient = new OkHttpClient();
    CookieManager cookieManager = new CookieManager(new DbCookieStore(), new HiPdaPolicy());
    httpClient.setCookieHandler(cookieManager);

    if (BuildConfig.DEBUG) {
      httpClient.interceptors().add(new HttpLoggingInterceptor());
    }
    Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlHelper.BASE_URL)
        .client(httpClient)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(ResponseConverterFactory.create())
        .build();
    sInstance = retrofit.create(ApiList.class);
  }

  static class HiPdaPolicy implements CookiePolicy {

    @Override
    public boolean shouldAccept(URI uri, HttpCookie cookie) {
      return uri.getHost().equals("www.hi-pda.com");
    }
  }

  static class DbCookieStore implements CookieStore {

    DbHelper db = DbHelper.getInstance();

    @Override
    public void add(URI uri, HttpCookie cookie) {
      db.addCookie(uri, cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
      return db.getCookie(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
      return db.getCookies();
    }

    @Override
    public List<URI> getURIs() {
      return db.getCookieURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
      return db.removeCookie(uri, cookie) > 0;
    }

    @Override
    public boolean removeAll() {
      return db.removeAllCookie() > 0;
    }
  }
}
