package com.skyfire.hipda.bean.content;

import java.util.Date;

/**
 * Created by Neil.
 */
public class UploadImageContent extends ImageContent {

  private final int id;
  private final Date uploadTime;
  private final int size;//bytes

  public UploadImageContent(int id, String url, int size, Date uploadTime) {
    super(url);
    this.id = id;
    this.size = size;
    this.uploadTime = uploadTime;
  }

  public int getId() {
    return id;
  }

  public int getSize() {
    return size;
  }

  public Date getUploadTime() {
    return uploadTime;
  }
}
