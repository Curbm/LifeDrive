package com.skyfire.hipda.api.parser;

import com.skyfire.hipda.api.ApiException;
import com.skyfire.hipda.api.JsoupResponseConverter;
import com.skyfire.hipda.bean.Forum;
import com.skyfire.hipda.misc.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neil
 */
public class ForumListParser extends JsoupResponseConverter<List<Forum>> {

  @Override
  public List<Forum> parse(String response) throws ApiException {
    Document doc = Jsoup.parse(response);
    Elements elems = doc.select("tbody[id^=forum]");
    List<Forum> list = new ArrayList<>(elems.size());
    for (Element elem : elems) {
      Forum forum = parseItem(elem);
      list.add(forum);
    }
    return list;
  }

  private Forum parseItem(Element elem) {
    int id = Util.parseIntNoThrow(elem.id().replaceFirst("forum", ""));
    String title = elem.select("div[class=left] > h2 > a").text();
    return new Forum(
        id,
        title
    );
  }


}
