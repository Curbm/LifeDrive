package com.skyfire.hipda.api.parser;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import com.skyfire.hipda.api.ApiException;
import com.skyfire.hipda.api.JsoupResponseConverter;
import com.skyfire.hipda.bean.Post;
import com.skyfire.hipda.bean.content.*;
import com.skyfire.hipda.lib.html.Color;
import com.skyfire.hipda.lib.span.BulletSpan;
import com.skyfire.hipda.lib.span.SmileySpan;
import com.skyfire.hipda.misc.SmileyHelper;
import com.skyfire.hipda.misc.UrlHelper;
import com.skyfire.hipda.misc.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostListParser extends JsoupResponseConverter<List<Post>> {

  static final Pattern TIME_PATTERN = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}");
  static final Pattern SIZE_PATTERN = Pattern.compile("(?<=\\().+(?=\\))");
  static final Pattern POST_ID_PATTERN = Pattern.compile("(?<=pid=)\\d+");
  static final Pattern REPLY_PATTERN = Pattern.compile("^\u56de\u590d\\s*\\d+#");

  @Override
  public List<Post> parse(String response) throws ApiException {
    Elements elems = Jsoup.parse(response)
        .select("div[id=postlist] > div[id^=post] > table[id^=pid]");
    List<Post> list = new ArrayList<>(elems.size());
    for (Element elem : elems) {
      list.add(parseItem(elem));
    }
    return list;
  }

  private Post parseItem(Element elem) {
    int id = Util.parseIntNoThrow(elem.attr("id").replaceFirst("pid", ""));
    int uid = Util.parseIntNoThrow(elem.select("td[class=postauthor] > div > a").attr("href")
        .replaceFirst("space\\.php\\?uid=", ""));
    int floor = Util.parseIntNoThrow(elem.select("td[class=postcontent] > div[class=postinfo] > " +
        "strong > a > em").text());
    String author = elem.select("td[class=postauthor] > div > a").text();
    String avatarUrl = UrlHelper.getAvatarUrl(uid);
    String title = elem.select("div[id=threadtitle]").text();
    Date publishTime = parsePublishTime(elem);
    Date modifiedTime = parseModifyTime(elem);

    // Parse refined html element at last
    List<Content> contentList = parseContentList(elem);

    return new Post(
        id,
        uid,
        author,
        avatarUrl,
        title,
        contentList,
        publishTime,
        modifiedTime,
        floor);
  }

  private List<Content> parseContentList(Element elem) {
    List<Content> list = new ArrayList<>();
    // FIXME: Have to call first() otherwise img elements' size is not correct
    Element contentElem = elem.select("td[id^=postmessage]").first();
    if (contentElem == null) {
      contentElem = elem.select("div[class=postmessage] > div[class=locked]").first();
    }
    if (contentElem == null) {
      // Should not happen
      return list;
    }

    SpannableStringBuilder spanStr = new SpannableStringBuilder();
    recursivelyParse(contentElem, contentElem, spanStr);
    Content[] contentSpans = spanStr.getSpans(0, spanStr.length(), Content.class);

    int start = 0;
    SpannableStringBuilder subStr;

    for (Content span : contentSpans) {
      int spanStart = spanStr.getSpanStart(span);
      int spanEnd = spanStr.getSpanEnd(span);

      if (start < spanStart) {
        subStr = (SpannableStringBuilder) spanStr.subSequence(start, spanStart);
        subStr = trimText(subStr);
        if (subStr != null) {
          list.add(new TextContent(subStr));
        }
      }
      list.add(span);

      start = spanEnd;
    }

/**
 * 1. Catch last segment
 * 2. Handle spannable which without any content spans
 */
    if (start < spanStr.length()) {
      if (start == 0) {
        subStr = spanStr;
      } else {
        subStr = (SpannableStringBuilder) spanStr.subSequence(start, spanStr.length());
      }
      subStr = trimText(subStr);
      if (subStr != null) {
        list.add(new TextContent(subStr));
      }
    }

    return list;
  }

  private void recursivelyParse(Element root, Element parent, SpannableStringBuilder builder) {
    for (Node node : parent.childNodes()) {
      if (node instanceof TextNode) {
        TextNode textNode = (TextNode) node;
        builder.append(textNode.text());
      } else if (node instanceof Element) {

        int length = builder.length();
        Element elemNode = (Element) node;
        String tag = elemNode.tagName();

        if (tag.equalsIgnoreCase("br")) {
          appendLineBreak(builder);
        } else if (tag.equalsIgnoreCase("li")) {
          appendLineBreak(builder);
          int lineBreakCount = builder.length() - length;
          recursivelyParse(root, elemNode, builder);
          builder.setSpan(new BulletSpan(), length + lineBreakCount, builder.length(), Spanned
              .SPAN_EXCLUSIVE_EXCLUSIVE);
          appendLineBreak(builder);
        } else if (tag.equalsIgnoreCase("div")) {
          if (elemNode.attr("class").equalsIgnoreCase("t_attach")) {
            continue;
          }
          appendBlockBreak(builder);
          recursivelyParse(root, elemNode, builder);
          appendBlockBreak(builder);
        } else if (tag.equalsIgnoreCase("p")) {
          appendBlockBreak(builder);
          recursivelyParse(root, elemNode, builder);
          appendBlockBreak(builder);
        } else if (tag.equalsIgnoreCase("u")) {
          recursivelyParse(root, elemNode, builder);
          builder.setSpan(new UnderlineSpan(), length, builder.length(), Spanned
              .SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (tag.equalsIgnoreCase("i")) {
          recursivelyParse(root, elemNode, builder);
          builder.setSpan(new StyleSpan(Typeface.ITALIC), length, builder.length(), Spanned
              .SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (tag.equalsIgnoreCase("b")) {
          recursivelyParse(root, elemNode, builder);
          builder.setSpan(new StyleSpan(Typeface.BOLD), length, builder.length(), Spanned
              .SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (tag.equalsIgnoreCase("strong")) {
          if (REPLY_PATTERN.matcher(elemNode.text()).lookingAt()) {
            ReplyContent content = parseReplyContent(elemNode);
            if (content != null) {
              builder.setSpan(content, length, length, Spanned.SPAN_MARK_MARK);
            }
          } else {
            recursivelyParse(root, elemNode, builder);
            builder.setSpan(new StyleSpan(Typeface.BOLD), length, builder.length(), Spanned
                .SPAN_EXCLUSIVE_EXCLUSIVE);
          }
        } else if (tag.equalsIgnoreCase("span")) {
          if (elemNode.id().startsWith("attach_")) {
            continue;
          }
          recursivelyParse(root, elemNode, builder);
        } else if (tag.equalsIgnoreCase("a")) {
          recursivelyParse(root, elemNode, builder);
          String url = elemNode.attr("href");
          builder.setSpan(new URLSpan(url), length, builder.length(), Spanned
              .SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (tag.equalsIgnoreCase("font")) {
          recursivelyParse(root, elemNode, builder);
          String colorStr = elemNode.attr("color");
          if (!TextUtils.isEmpty(colorStr)) {
            int color = Color.getHtmlColor(colorStr) | 0xFF000000;
            builder.setSpan(new ForegroundColorSpan(color), length, builder.length(), Spanned
                .SPAN_EXCLUSIVE_EXCLUSIVE);
          }
        } else if (tag.equalsIgnoreCase("blockquote")) {
          QuoteContent content = parseBlockQuote(elemNode);
          if (content != null) {
            builder.setSpan(content, length, length, Spanned.SPAN_MARK_MARK);
          }
        } else if (tag.equalsIgnoreCase("img")) {
          if (elemNode.hasAttr("smilieid")) {
            int smileid = Util.parseIntNoThrow(elemNode.attr("smilieid"));
            SmileySpan span = SmileyHelper.getSmileySpan(smileid);
            if (span != null) {
              builder.append("\uFFFC");
              builder.setSpan(span, length, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
          } else if (elemNode.id().startsWith("aimg_")) {
            ImageContent content = parseAttachImageContent(elemNode);
            if (content != null) {
              builder.setSpan(content, length, length, Spanned.SPAN_MARK_MARK);
            }
          } else {
            ImageContent content = parseWebImageContent(elemNode);
            if (content != null) {
              builder.setSpan(content, length, length, Spanned.SPAN_MARK_MARK);
            }
          }
        } else {
          recursivelyParse(root, elemNode, builder);
        }
      }
    }
  }

  private void appendBlockBreak(SpannableStringBuilder builder) {
    int len = builder.length();

    if (len >= 1 && builder.charAt(len - 1) == '\n') {
      if (len >= 2 && builder.charAt(len - 2) == '\n') {
        return;
      }

      builder.append("\n");
      return;
    }

    if (len != 0) {
      builder.append("\n\n");
    }
  }

  private void appendLineBreak(SpannableStringBuilder builder) {
    int len = builder.length();

    if (len >= 1 && builder.charAt(len - 1) == '\n') {
      if (len >= 2 && builder.charAt(len - 2) == '\n') {
        return;
      }

      builder.append("\n");
      return;
    }

    if (len != 0) {
      builder.append("\n");
    }
  }

  private ImageContent parseWebImageContent(Element elem) {
    String attrSrc = elem.attr("src");
    return new ImageContent(attrSrc);
  }

  private ImageContent parseAttachImageContent(Element elem) {
    String attrId = elem.attr("id");
    String attrFile = elem.attr("file");

    int id = Util.parseIntNoThrow(attrId.replaceFirst("aimg_", ""));
    String url = UrlHelper.BASE_URL + attrFile;
    String size = null;
    Date uploadTime = null;

    Element infoElem = elem.siblingElements().select("div[id=aimg_" + id + "_menu").first();
    Matcher matcher;
    if (infoElem != null) {
      String infoText = infoElem.text();
      matcher = SIZE_PATTERN.matcher(infoText);
      if (matcher.find()) {
        size = matcher.group();
      }
      matcher = TIME_PATTERN.matcher(infoText);
      if (matcher.find()) {
        try {
          uploadTime = Util.getDateFormat("yyyy-MM-dd hh:mm").parse(matcher.group());
        } catch (ParseException e) {
          e.printStackTrace();
        }
      }
    }
    return new UploadImageContent(
        id,
        url,
        size,
        uploadTime
    );
  }


  private QuoteContent parseBlockQuote(Element elem) {
    int id = -1;
    Element aElem = elem.select("font[size=2] > a[href*=redirect.php?goto=findpost]").first();
    if (aElem != null) {
      aElem.parent().remove();
      String attr = aElem.attr("href");
      Matcher matcher = POST_ID_PATTERN.matcher(attr);
      if (matcher.find()) {
        id = Util.parseIntNoThrow(matcher.group());
      }
    }
    SpannableStringBuilder text = new SpannableStringBuilder();
    recursivelyParse(elem, elem, text);
    text = trimText(text);
    return text != null ? new QuoteContent(id, text) : null;
  }

  private ReplyContent parseReplyContent(Element elem) {
    Element aElem = elem.select("a[href*=redirect.php?goto=findpost]").first();
    if (aElem == null) {
      return null;
    }
    String attr = aElem.attr("href");
    Matcher matcher = POST_ID_PATTERN.matcher(attr);
    if (!matcher.find()) {
      return null;
    }
    int id = Util.parseIntNoThrow(matcher.group());
    return new ReplyContent(id);
  }


  private Date parsePublishTime(Element elem) {
    SimpleDateFormat formatter = Util.getDateFormat("yyyy-MM-dd hh:mm");
    try {
      return formatter.parse(elem.select("em[id^=authorposton]").text().replaceFirst
          ("\\u53d1\\u8868\\u4e8e\\u0020", ""));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Date parseModifyTime(Element ele) {
    SimpleDateFormat formatter = Util.getDateFormat("yyyy-MM-dd hh:mm");
    try {
      Elements modifyTimeElems = ele.select("td[id^=postmessage] > i[class=pstatus]");
      if (modifyTimeElems.size() == 1) {

        modifyTimeElems.remove();
        Matcher matcher = TIME_PATTERN.matcher(modifyTimeElems.text());
        if (matcher.find()) {
          return formatter.parse(matcher.group());
        }

      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  private SpannableStringBuilder trimText(SpannableStringBuilder str) {
    int length = str.length();
    int startCount = 0;
    for (int i = 0; i < length; i++) {
      char c = str.charAt(i);
      if (c == '\n' || c == ' ') {
        startCount++;
      } else {
        break;
      }
    }
    str.delete(0, startCount);

    length = str.length();
    int endCount = 0;
    for (int i = length - 1; i >= 0; i--) {
      char c = str.charAt(i);
      if (c == '\n' || c == ' ') {
        endCount++;
      } else {
        break;
      }
    }
    str.delete(length - endCount, length);

    return str.length() > 0 ? str : null;
  }
}
