package com.skyfire.hipda.api;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.nio.charset.Charset;

public abstract class ResponseConverter<T> {

  public T convert(ResponseBody value) throws IOException {
    try {
      String response = new String(value.bytes(), Charset.forName("gbk"));
      value.close();
      return parse(response);
    } catch (ApiException e) {
      throw new IllegalStateException(e);
    }
  }

  public abstract T parse(String response) throws ApiException;
}
