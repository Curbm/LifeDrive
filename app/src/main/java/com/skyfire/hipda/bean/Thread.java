package com.skyfire.hipda.bean;

import java.util.Date;

/**
 * Created by Neil
 */
public class Thread {

  private final int id;
  private final int uid;
  private final String author;
  private final String avatarUrl;
  private final String subject;
  private final Date publishTime;
  private final int viewNum;
  private final int commentNum;
  private final int pageCount;
  private final boolean hasImg;
  private final boolean sticky;

  public Thread(int id, int uid, String author, String avatarUrl, String subject, Date
      publishTime, int viewNum, int commentNum, int pageCount, boolean hasImg, boolean sticky) {
    this.id = id;
    this.uid = uid;
    this.author = author;
    this.avatarUrl = avatarUrl;
    this.subject = subject;
    this.publishTime = publishTime;
    this.viewNum = viewNum;
    this.commentNum = commentNum;
    this.pageCount = pageCount;
    this.hasImg = hasImg;
    this.sticky = sticky;
  }

  public int getId() {
    return id;
  }

  public int getUid() {
    return uid;
  }

  public String getAuthor() {
    return author;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public String getSubject() {
    return subject;
  }

  public Date getPublishTime() {
    return publishTime;
  }

  public int getViewNum() {
    return viewNum;
  }

  public int getCommentNum() {
    return commentNum;
  }

  public int getPageCount() {
    return pageCount;
  }

  public boolean isHasImg() {
    return hasImg;
  }

  public boolean isSticky() {
    return sticky;
  }
}
