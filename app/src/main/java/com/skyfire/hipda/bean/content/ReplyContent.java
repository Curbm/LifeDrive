package com.skyfire.hipda.bean.content;

/**
 * Created by Neil
 */
public class  ReplyContent implements Content {

  private final int replyId;

  public ReplyContent(int replyId) {
    this.replyId = replyId;
  }

  public int getReplyId() {
    return replyId;
  }

}
