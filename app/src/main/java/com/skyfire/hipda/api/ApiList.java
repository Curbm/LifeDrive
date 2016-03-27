package com.skyfire.hipda.api;

import com.skyfire.hipda.api.parser.*;
import com.skyfire.hipda.bean.AccountInfo;
import com.skyfire.hipda.bean.Forum;
import com.skyfire.hipda.bean.Post;
import com.skyfire.hipda.bean.ThreadListItem;
import retrofit.http.*;
import rx.Observable;

import java.util.List;
import java.util.Map;

public interface ApiList {

  @GET("logging.php?action=login")
  @ResponseParser(GetForumHashParser.class)
  Observable<String> getLoginForumHash();

  @POST("logging.php?action=login&loginsubmit=yes")
  @ResponseParser(LoginParser.class)
  @FormUrlEncoded
  Observable<String> login(@Field("username") String username, @Field("password") String
      password, @FieldMap Map<String, String> extraParams);

  @GET("memcp.php?action=profile&typeid=5")
  @ResponseParser(AccountInfoParser.class)
  Observable<AccountInfo> getAccountInfo();

  @GET("index.php")
  @ResponseParser(ForumListParser.class)
  Observable<List<Forum>> getForumList();

  @GET("forumdisplay.php")
  @ResponseParser(ThreadListParser.class)
  Observable<List<ThreadListItem>> getThreadList(@Query("fid") int forumId, @Query("page") int
      page);

  @GET("viewthread.php")
  @ResponseParser(PostListParser.class)
  Observable<List<Post>> getPostList(@Query("tid") int threadId, @Query("page") int page);
}
