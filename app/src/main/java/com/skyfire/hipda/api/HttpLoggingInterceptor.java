package com.skyfire.hipda.api;

import android.util.Log;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.internal.http.HttpEngine;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import okio.Buffer;
import okio.BufferedSource;

class HttpLoggingInterceptor implements Interceptor {

  static final Charset GBK = Charset.forName("gbk");

  @Override
  public Response intercept(Chain chain) throws IOException {

    StringBuilder builder = new StringBuilder();

    Request request = chain.request();

    RequestBody requestBody = request.body();
    boolean hasRequestBody = requestBody != null;

    builder.append("-\n--> ")
        .append(request.method())
        .append(' ')
        .append(request.httpUrl())
        .append("\n");

    if (hasRequestBody) {
      Buffer buffer = new Buffer();
      requestBody.writeTo(buffer);

      Charset charset = GBK;
      MediaType contentType = requestBody.contentType();
      if (contentType != null) {
        contentType.charset(GBK);
      }

      builder.append(buffer.readString(charset)).append("\n");
    }

    long startNs = System.nanoTime();
    Response response = chain.proceed(request);
    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

    ResponseBody responseBody = response.body();
    builder.append("<-- ")
        .append(response.code())
        .append(' ')
        .append(response.message())
        .append(" (")
        .append(tookMs)
        .append("ms")
        .append(")\n");

    if (HttpEngine.hasBody(response)) {
      BufferedSource source = responseBody.source();
      source.request(Long.MAX_VALUE); // Buffer the entire body.
      Buffer buffer = source.buffer();

      Charset charset = GBK;
      MediaType contentType = responseBody.contentType();
      if (contentType != null) {
        charset = contentType.charset(GBK);
      }

      if (responseBody.contentLength() != 0) {
        builder.append(buffer.clone().readString(charset));
      }
    }

    Log.d("OkHttp", builder.toString());

    return response;
  }
}
