/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skyfire.hipda.lib.html;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.*;
import android.text.style.*;
import org.xml.sax.*;

/**
 * This class processes HTML strings into displayable styled text.
 * Not all HTML tags are supported.
 */
public class Html {
  /**
   * Retrieves images for HTML &lt;img&gt; tags.
   */
  public static interface ImageGetter {
    /**
     * This method is called when the HTML parser encounters an
     * &lt;img&gt; tag.  The <code>source</code> argument is the
     * string from the "src" attribute; the return value should be
     * a Drawable representation of the image or <code>null</code>
     * for a generic replacement image.  Make sure you call
     * setBounds() on your Drawable if it doesn't already have
     * its bounds set.
     */
    public Drawable getDrawable(String source);
  }

  /**
   * Is notified when HTML tags are encountered that the parser does
   * not know how to interpret.
   */
  public static interface TagHandler {
    /**
     * This method will be called whenn the HTML parser encounters
     * a tag that it does not know how to interpret.
     */
    public void handleTag(boolean opening, String tag,
                          Editable output, XMLReader xmlReader);
  }

  private Html() {
  }


  /**
   * Returns an HTML representation of the provided Spanned text. A best effort is
   * made to add HTML tags corresponding to spans. Also note that HTML metacharacters
   * (such as "&lt;" and "&amp;") within the input text are escaped.
   *
   * @param text input text to convert
   * @return string containing input converted to HTML
   */
  public static String toHtml(Spanned text) {
    StringBuilder out = new StringBuilder();
    withinHtml(out, text);
    return out.toString();
  }

  /**
   * Returns an HTML escaped representation of the given plain text.
   */
  public static String escapeHtml(CharSequence text) {
    StringBuilder out = new StringBuilder();
    withinStyle(out, text, 0, text.length());
    return out.toString();
  }

  private static void withinHtml(StringBuilder out, Spanned text) {
    int len = text.length();

    int next;
    for (int i = 0; i < text.length(); i = next) {
      next = text.nextSpanTransition(i, len, ParagraphStyle.class);
      ParagraphStyle[] style = text.getSpans(i, next, ParagraphStyle.class);
      String elements = " ";
      boolean needDiv = false;

      for (int j = 0; j < style.length; j++) {
        if (style[j] instanceof AlignmentSpan) {
          Layout.Alignment align =
              ((AlignmentSpan) style[j]).getAlignment();
          needDiv = true;
          if (align == Layout.Alignment.ALIGN_CENTER) {
            elements = "align=\"center\" " + elements;
          } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
            elements = "align=\"right\" " + elements;
          } else {
            elements = "align=\"left\" " + elements;
          }
        }
      }
      if (needDiv) {
        out.append("<div ").append(elements).append(">");
      }

      withinDiv(out, text, i, next);

      if (needDiv) {
        out.append("</div>");
      }
    }
  }

  private static void withinDiv(StringBuilder out, Spanned text,
                                int start, int end) {
    int next;
    for (int i = start; i < end; i = next) {
      next = text.nextSpanTransition(i, end, QuoteSpan.class);
      QuoteSpan[] quotes = text.getSpans(i, next, QuoteSpan.class);

      for (QuoteSpan quote : quotes) {
        out.append("<blockquote>");
      }

      withinBlockquote(out, text, i, next);

      for (QuoteSpan quote : quotes) {
        out.append("</blockquote>\n");
      }
    }
  }

  private static String getOpenParaTagWithDirection(Spanned text, int start, int end) {

//    final int len = end - start;
//    final byte[] levels = new byte[len];
//    final char[] buffer = new char[len];
//    TextUtils.getChars(text, start, end, buffer, 0);
//
//    int paraDir = AndroidBidi.bidi(Layout.DIR_REQUEST_DEFAULT_LTR, buffer, levels, len,
//        false /* no info */);
//
//    switch (paraDir) {
//      case Layout.DIR_RIGHT_TO_LEFT:
//        return "<p dir=\"rtl\">";
//      case Layout.DIR_LEFT_TO_RIGHT:
//      default:
//        return "<p dir=\"ltr\">";
//    }

    // TODO: Mostly Chinese
    return "<p dir=\"ltr\">";

  }

  private static void withinBlockquote(StringBuilder out, Spanned text,
                                       int start, int end) {
    out.append(getOpenParaTagWithDirection(text, start, end));

    int next;
    for (int i = start; i < end; i = next) {
      next = TextUtils.indexOf(text, '\n', i, end);
      if (next < 0) {
        next = end;
      }

      int nl = 0;

      while (next < end && text.charAt(next) == '\n') {
        nl++;
        next++;
      }

      if (withinParagraph(out, text, i, next - nl, nl, next == end)) {
                /* Paragraph should be closed */
        out.append("</p>\n");
        out.append(getOpenParaTagWithDirection(text, next, end));
      }
    }

    out.append("</p>\n");
  }

  /* Returns true if the caller should close and reopen the paragraph. */
  private static boolean withinParagraph(StringBuilder out, Spanned text,
                                         int start, int end, int nl,
                                         boolean last) {
    int next;
    for (int i = start; i < end; i = next) {
      next = text.nextSpanTransition(i, end, CharacterStyle.class);
      CharacterStyle[] style = text.getSpans(i, next,
          CharacterStyle.class);

      for (int j = 0; j < style.length; j++) {
        if (style[j] instanceof StyleSpan) {
          int s = ((StyleSpan) style[j]).getStyle();

          if ((s & Typeface.BOLD) != 0) {
            out.append("<b>");
          }
          if ((s & Typeface.ITALIC) != 0) {
            out.append("<i>");
          }
        }
        if (style[j] instanceof TypefaceSpan) {
          String s = ((TypefaceSpan) style[j]).getFamily();

          if ("monospace".equals(s)) {
            out.append("<tt>");
          }
        }
        if (style[j] instanceof SuperscriptSpan) {
          out.append("<sup>");
        }
        if (style[j] instanceof SubscriptSpan) {
          out.append("<sub>");
        }
        if (style[j] instanceof UnderlineSpan) {
          out.append("<u>");
        }
        if (style[j] instanceof StrikethroughSpan) {
          out.append("<strike>");
        }
        if (style[j] instanceof URLSpan) {
          out.append("<a href=\"");
          out.append(((URLSpan) style[j]).getURL());
          out.append("\">");
        }
        if (style[j] instanceof ImageSpan) {
          out.append("<img src=\"");
          out.append(((ImageSpan) style[j]).getSource());
          out.append("\">");

          // Don't output the dummy character underlying the image.
          i = next;
        }
        if (style[j] instanceof AbsoluteSizeSpan) {
          out.append("<font size =\"");
          out.append(((AbsoluteSizeSpan) style[j]).getSize() / 6);
          out.append("\">");
        }
        if (style[j] instanceof ForegroundColorSpan) {
          out.append("<font color =\"#");
          String color = Integer.toHexString(((ForegroundColorSpan)
              style[j]).getForegroundColor() + 0x01000000);
          while (color.length() < 6) {
            color = "0" + color;
          }
          out.append(color);
          out.append("\">");
        }
      }

      withinStyle(out, text, i, next);

      for (int j = style.length - 1; j >= 0; j--) {
        if (style[j] instanceof ForegroundColorSpan) {
          out.append("</font>");
        }
        if (style[j] instanceof AbsoluteSizeSpan) {
          out.append("</font>");
        }
        if (style[j] instanceof URLSpan) {
          out.append("</a>");
        }
        if (style[j] instanceof StrikethroughSpan) {
          out.append("</strike>");
        }
        if (style[j] instanceof UnderlineSpan) {
          out.append("</u>");
        }
        if (style[j] instanceof SubscriptSpan) {
          out.append("</sub>");
        }
        if (style[j] instanceof SuperscriptSpan) {
          out.append("</sup>");
        }
        if (style[j] instanceof TypefaceSpan) {
          String s = ((TypefaceSpan) style[j]).getFamily();

          if (s.equals("monospace")) {
            out.append("</tt>");
          }
        }
        if (style[j] instanceof StyleSpan) {
          int s = ((StyleSpan) style[j]).getStyle();

          if ((s & Typeface.BOLD) != 0) {
            out.append("</b>");
          }
          if ((s & Typeface.ITALIC) != 0) {
            out.append("</i>");
          }
        }
      }
    }

    if (nl == 1) {
      out.append("<br>\n");
      return false;
    } else {
      for (int i = 2; i < nl; i++) {
        out.append("<br>");
      }
      return !last;
    }
  }

  private static void withinStyle(StringBuilder out, CharSequence text,
                                  int start, int end) {
    for (int i = start; i < end; i++) {
      char c = text.charAt(i);

      if (c == '<') {
        out.append("&lt;");
      } else if (c == '>') {
        out.append("&gt;");
      } else if (c == '&') {
        out.append("&amp;");
      } else if (c >= 0xD800 && c <= 0xDFFF) {
        if (c < 0xDC00 && i + 1 < end) {
          char d = text.charAt(i + 1);
          if (d >= 0xDC00 && d <= 0xDFFF) {
            i++;
            int codepoint = 0x010000 | (int) c - 0xD800 << 10 | (int) d - 0xDC00;
            out.append("&#").append(codepoint).append(";");
          }
        }
      } else if (c > 0x7E || c < ' ') {
        out.append("&#").append((int) c).append(";");
      } else if (c == ' ') {
        while (i + 1 < end && text.charAt(i + 1) == ' ') {
          out.append("&nbsp;");
          i++;
        }

        out.append(' ');
      } else {
        out.append(c);
      }
    }
  }
}

