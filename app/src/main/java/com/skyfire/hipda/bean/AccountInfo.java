package com.skyfire.hipda.bean;

/**
 * Created by Neil
 */
public class AccountInfo {

  public final int threadPerPage;
  public final int postPerPage;


  public AccountInfo(int threadPerPage, int postPerPage) {
    this.threadPerPage = threadPerPage;
    this.postPerPage = postPerPage;
  }

  public int getThreadPerPage() {
    return threadPerPage;
  }

  public int getPostPerPage() {
    return postPerPage;
  }
}
