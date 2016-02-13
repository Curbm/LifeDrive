package com.skyfire.hipda.api.parser;

import com.skyfire.hipda.api.ApiException;
import com.skyfire.hipda.api.JsoupResponseConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class LoginParser extends JsoupResponseConverter<String> {

  @Override
  public String parse(String response) throws ApiException {
    Element elem = Jsoup.parse(response).select("div[class=postbox] > div[class^=alert] > p")
        .first();
    if (elem != null) {
      String text = elem.text();
      if (text.contains("\u6b22\u8fce\u60a8\u56de\u6765")) {
        // Succeeded
        return text;
      } else {
        throw new ApiException(text);
      }
    }
    throw new ApiException("Can not parse login info");
  }
}
