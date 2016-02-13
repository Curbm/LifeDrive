package com.skyfire.hipda.api.parser;

import com.skyfire.hipda.api.ApiException;
import com.skyfire.hipda.api.JsoupResponseConverter;
import com.skyfire.hipda.bean.ThreadListItem;
import com.skyfire.hipda.misc.UrlHelper;
import com.skyfire.hipda.misc.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThreadListParser extends JsoupResponseConverter<List<ThreadListItem>> {

  @Override
  public List<ThreadListItem> parse(String response) throws ApiException {
    Document doc = Jsoup.parse(response);
    Elements stickyElements = doc
        .select("div[id=threadlist] table[class=datatable] > tbody[id^=stickthread]");
    Elements normalElements = doc
        .select("div[id=threadlist] table[class=datatable] > tbody[id^=normalthread]");
    List<ThreadListItem> list = new ArrayList<>(normalElements.size() + stickyElements.size());
    for (Element elem : stickyElements) {
      list.add(parseItem(elem, true));
    }
    for (Element elem : normalElements) {
      list.add(parseItem(elem, false));
    }
    return list;
  }

  private ThreadListItem parseItem(Element elem, boolean sticky) {
    int id = Util.parseIntNoThrow(elem.attr("id").replaceFirst("stickthread_|normalthread_", ""));

    int uid = 0;
    Element uidElem = elem.select("td[class=author] > cite > a").first();
    if (uidElem != null) {
      uid = Util.parseIntNoThrow(uidElem.attr("href").replaceFirst("space\\.php\\?uid=", ""));
    }

    String author = elem.select("td[class=author] > cite > a").text();
    String avatarUrl = UrlHelper.getAvatarUrl(uid);
    String subject = elem.select("th[class^=subject] > span[id^=thread]").text();
    Date publishTime = parsePublishTime(elem);

    int viewNum = Util.parseIntNoThrow(elem.select("td[class=nums] > em").text());
    int commentNum = Util.parseIntNoThrow(elem.select("td[class=nums] > strong").text());
    int pageCount = parsePageCount(elem);
    boolean hasImg = elem.select("th[class=^subject] > img[class=attach]") != null;

    return new ThreadListItem(
        id,
        uid,
        author,
        avatarUrl,
        subject,
        publishTime,
        viewNum,
        commentNum,
        pageCount,
        hasImg,
        sticky
    );
  }

  private Date parsePublishTime(Element elem) {
    try {
      return Util.getDateFormat("yyyy-MM-dd").parse(elem.select("td[class=author] > em")
          .text());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  private int parsePageCount(Element elem) {
    Elements pagesElem = elem.select("th[class^=subject] > span[class=threadpages] > a");
    int pageCount = 1;
    if (pagesElem.size() > 0) {
      pageCount = Util.parseIntNoThrow(pagesElem.last().text());
    }
    return pageCount;
  }
}
