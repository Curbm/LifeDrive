package com.skyfire.hipda.api.parser;

import com.skyfire.hipda.api.ApiException;
import com.skyfire.hipda.api.ResponseConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class LoginForumHashParser extends ResponseConverter<String> {

  @Override
  public String parse(String response) throws ApiException {
    Document doc = Jsoup.parse(response);
    Element elem = doc.select("input[name=formhash][type=hidden]").first();
    if (elem != null && elem.hasAttr("value")) {
      return elem.attr("value");
    }
    throw new ApiException("Can't get form-hash");
  }
}
