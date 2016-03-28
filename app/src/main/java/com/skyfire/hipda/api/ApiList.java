package com.skyfire.hipda.api;

import com.skyfire.hipda.BuildConfig;
import com.skyfire.hipda.api.parser.*;
import com.skyfire.hipda.bean.AccountInfo;
import com.skyfire.hipda.bean.Forum;
import com.skyfire.hipda.bean.Post;
import com.skyfire.hipda.bean.Thread;
import com.skyfire.hipda.db.DbHelper;
import com.skyfire.hipda.misc.UrlHelper;
import com.squareup.okhttp.*;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.List;

/**
 * Created by Neil
 */
public class ApiList {

  private static ApiList sInstance = new ApiList();
  private static final String ENCODE_CHARSET = "GBK";
  private OkHttpClient client;

  private ApiList() {
    client = new OkHttpClient();
    CookieManager cookieManager = new CookieManager(new DbCookieStore(), new HiPdaPolicy());
    client.setCookieHandler(cookieManager);

    if (BuildConfig.DEBUG) {
      client.interceptors().add(new HttpLoggingInterceptor());
    }
  }

  public static ApiList api() {
    return sInstance;
  }

  private String encode(String value) {
    try {
      return URLEncoder.encode(value, ENCODE_CHARSET);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return "";
    }
  }


  public Observable<Void> login(final String username, final String password) {
    return getLoginForumHash()
        .flatMap(new Func1<String, Observable<Void>>() {
          @Override
          public Observable<Void> call(String s) {
            return login(username, password, s);
          }
        });
  }

  private Observable<Void> login(String username, String password, String forumHash) {
    RequestBody requestBody = new FormEncodingBuilder()
        .addEncoded("username", encode(username))
        .addEncoded("password", encode(password))
        .addEncoded("formHash", forumHash)
        .addEncoded("loginfield", "username")
        .addEncoded("loginsubmit", "true")
        .addEncoded("cookietime", "2592000")
        .build();
    Request request = new Request.Builder()
        .url(UrlHelper.BASE_URL + "logging.php?action=login&loginsubmit=yes")
        .post(requestBody)
        .build();
    Call call = client.newCall(request);
    return Observable.create(new CallOnSubscribe<>(call, new LoginParser()));
  }

  private Observable<String> getLoginForumHash() {
    Request request = new Request.Builder()
        .url(UrlHelper.BASE_URL + "logging.php?action=login")
        .build();
    Call call = client.newCall(request);
    return Observable.create(new CallOnSubscribe<>(call, new LoginForumHashParser()));
  }

  public Observable<AccountInfo> getAccountInfo() {
    Request request = new Request.Builder()
        .url(UrlHelper.BASE_URL + "memcp.php?action=profile&typeid=5")
        .build();
    Call call = client.newCall(request);
    return Observable.create(new CallOnSubscribe<>(call, new AccountInfoParser()));
  }

  public Observable<List<Forum>> getForumList() {
    Request request = new Request.Builder()
        .url(UrlHelper.BASE_URL + "index.php")
        .build();
    Call call = client.newCall(request);
    return Observable.create(new CallOnSubscribe<>(call, new ForumListParser()));
  }

  public Observable<List<Thread>> getThreadList(int forumId, int page) {
    HttpUrl url = HttpUrl.parse(UrlHelper.BASE_URL + "forumdisplay.php")
        .newBuilder()
        .addEncodedQueryParameter("fid", String.valueOf(forumId))
        .addEncodedQueryParameter("page", String.valueOf(page))
        .build();
    Request request = new Request.Builder()
        .url(url)
        .build();
    Call call = client.newCall(request);
    return Observable.create(new CallOnSubscribe<>(call, new ThreadListParser()));
  }

  public Observable<List<Post>> getPostList(int threadId, int page) {
    HttpUrl url = HttpUrl.parse(UrlHelper.BASE_URL + "viewthread.php")
        .newBuilder()
        .addEncodedQueryParameter("tid", String.valueOf(threadId))
        .addEncodedQueryParameter("page", String.valueOf(page))
        .build();
    Request request = new Request.Builder()
        .url(url)
        .build();
    Call call = client.newCall(request);
    return Observable.create(new CallOnSubscribe<>(call, new PostListParser()));

  }

  static class CallOnSubscribe<T> implements Observable.OnSubscribe<T> {

    private final Call call;
    private final ResponseConverter<T> responseConverter;

    CallOnSubscribe(Call call, ResponseConverter<T> responseConverter) {
      this.call = call;
      this.responseConverter = responseConverter;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
      subscriber.add(Subscriptions.create(new Action0() {
        @Override
        public void call() {
          call.cancel();
        }
      }));

      if (subscriber.isUnsubscribed()) {
        return;
      }

      try {
        Response response = call.execute();
        if (!subscriber.isUnsubscribed()) {
          T t = responseConverter.convert(response.body());
          subscriber.onNext(t);
        }
      } catch (Throwable t) {
        Exceptions.throwIfFatal(t);
        if (!subscriber.isUnsubscribed()) {
          subscriber.onError(t);
        }
        return;
      }

      if (!subscriber.isUnsubscribed()) {
        subscriber.onCompleted();
      }
    }
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
