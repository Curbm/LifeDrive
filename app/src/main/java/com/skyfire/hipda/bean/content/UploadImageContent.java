package com.skyfire.hipda.bean.content;

import java.util.Date;

/**
 * Created by Neil.
 */
public class UploadImageContent extends ImageContent {

  private final int id;
  private final Date uploadTime;
  private final String sizeText;

  public UploadImageContent(int id, String url, String sizeText, Date uploadTime) {
    super(url);
    this.id = id;
    this.sizeText = sizeText;
    this.uploadTime = uploadTime;
  }

  public int getId() {
    return id;
  }

  public String getSizeText() {
    return sizeText;
  }

  public Date getUploadTime() {
    return uploadTime;
  }
}
