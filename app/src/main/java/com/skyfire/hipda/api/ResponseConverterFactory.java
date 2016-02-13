package com.skyfire.hipda.api;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import retrofit.Converter;

public class ResponseConverterFactory extends Converter.Factory {

  private ResponseConverterFactory() {
  }

  public static ResponseConverterFactory create() {
    return new ResponseConverterFactory();
  }

  @Override
  public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if (annotation instanceof ResponseParser) {
        Class<? extends Converter<ResponseBody, ?>> cls = ((ResponseParser) annotation).value();
        try {
          return cls.newInstance();
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return super.fromResponseBody(type, annotations);
  }

  @Override public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
    return super.toRequestBody(type, annotations);
  }
}
