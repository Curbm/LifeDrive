package com.skyfire.hipda.bean;

import com.skyfire.hipda.bean.content.Content;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Neil
 */
public class Post {

  private final int id;
  private final int uid;
  private final String author;
  private final String avatarUrl;
  private final String title;
  private final List<Content> contentList;
  private final Date publishTime;
  private final Date modifyTime;
  private final int floor;


  public Post(int id, int uid, String author, String avatarUrl, String title,
              List<Content> contentList, Date publishTime, Date modifyTime, int floor) {
    this.id = id;
    this.uid = uid;
    this.author = author;
    this.avatarUrl = avatarUrl;
    this.title = title;
    this.floor = floor;
    this.contentList = Collections.unmodifiableList(contentList);
    this.publishTime = publishTime;
    this.modifyTime = modifyTime;
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

  public String getTitle() {
    return title;
  }

  public List<Content> getContentList() {
    return contentList;
  }

  public Date getPublishTime() {
    return publishTime;
  }

  public Date getModifyTime() {
    return modifyTime;
  }

  public int getFloor() {
    return floor;
  }
}
