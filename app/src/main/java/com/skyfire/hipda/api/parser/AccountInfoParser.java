package com.skyfire.hipda.api.parser;

import com.skyfire.hipda.api.ApiException;
import com.skyfire.hipda.api.JsoupResponseConverter;
import com.skyfire.hipda.bean.AccountInfo;
import com.skyfire.hipda.misc.Util;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;

import java.util.List;

/**
 * Created by Neil
 */
public class AccountInfoParser extends JsoupResponseConverter<AccountInfo> {

  @Override
  public AccountInfo parse(String response) throws ApiException {
    Document doc = Jsoup.parse(response);
    FormElement form = (FormElement) doc.select("form[name=reg]").first();
    List<Connection.KeyVal> data = form.formData();

    int threadPerPage = 0;
    int postPerPage = 0;

    for (Connection.KeyVal keyVal : data) {
      if (keyVal.key().equals("tppnew")) {
        threadPerPage = mapThreadPerPage(Util.parseIntNoThrow(keyVal.value()));
      } else if (keyVal.key().equals("pppnew")) {
        postPerPage = mapPostPerPage(Util.parseIntNoThrow(keyVal.value()));
      }
    }
    return new AccountInfo(
        threadPerPage,
        postPerPage
    );
  }

  private int mapThreadPerPage(int val) {
    switch (val) {
      case 10:
        return 10;
      case 20:
        return 20;
      case 30:
        return 30;
      default:
        return 50;
    }
  }

  private int mapPostPerPage(int val) {
    switch (val) {
      case 5:
        return 5;
      case 10:
        return 10;
      case 15:
        return 15;
      default:
        return 50;
    }
  }
}
