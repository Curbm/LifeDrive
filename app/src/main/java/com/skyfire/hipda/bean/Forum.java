package com.skyfire.hipda.bean;

/**
 * Created by Neil
 */
public class Forum {

  private final int id;
  private final String title;

  public Forum(int id, String title) {
    this.id = id;
    this.title = title;
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

}
