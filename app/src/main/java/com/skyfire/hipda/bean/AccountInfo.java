package com.skyfire.hipda.bean;

/**
 * Created by Neil
 */
public class AccountInfo {

  private final int threadPerPage;
  private final int postPerPage;
  private final String forumHash;

  public AccountInfo(int threadPerPage, int postPerPage, String forumHash) {
    this.threadPerPage = threadPerPage;
    this.postPerPage = postPerPage;
    this.forumHash = forumHash;
  }

  public int getThreadPerPage() {
    return threadPerPage;
  }

  public int getPostPerPage() {
    return postPerPage;
  }

  public String getForumHash() {
    return forumHash;
  }
}
