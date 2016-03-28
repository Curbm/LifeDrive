package com.skyfire.hipda.api.parser;

import com.skyfire.hipda.api.ApiException;
import com.skyfire.hipda.api.ResponseConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 * Created by Neil
 */
public class ReplyPostParser extends ResponseConverter<Void> {
  @Override
  public Void parse(String response) throws ApiException {
    Element elem = Jsoup.parse(response).select("div[class=postbox] > div[class^=alert] > p")
        .first();
    if (elem != null) {
      throw new ApiException(elem.text());
    }
    return null;
  }
}
