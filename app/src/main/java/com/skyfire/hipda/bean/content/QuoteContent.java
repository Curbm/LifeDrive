package com.skyfire.hipda.bean.content;

import android.text.SpannableStringBuilder;

/**
 * Created by Neil
 */
public class QuoteContent implements Content {

  private final int quoteId;
  private final SpannableStringBuilder quoteText;


  public QuoteContent(int quoteId, SpannableStringBuilder quoteText) {
    this.quoteId = quoteId;
    this.quoteText = quoteText;
  }

  public int getQuoteId() {
    return quoteId;
  }

  public SpannableStringBuilder getQuoteText() {
    return quoteText;
  }
}
