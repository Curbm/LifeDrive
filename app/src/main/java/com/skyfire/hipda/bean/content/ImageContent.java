package com.skyfire.hipda.bean.content;

/**
 * Created by Neil
 */
public class ImageContent implements Content {

  private final String url;

  public ImageContent(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

}
