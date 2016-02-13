package com.skyfire.hipda.bean.content;

import android.text.SpannableStringBuilder;

/**
 * Created by Neil.
 */
public class TextContent implements Content {
  private final SpannableStringBuilder text;

  public TextContent(SpannableStringBuilder text) {
    this.text = text;
  }

  public SpannableStringBuilder getText() {
    return text;
  }
}
