package com.skyfire.hipda.api;

import com.squareup.okhttp.ResponseBody;
import retrofit.Converter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface ResponseParser {

  Class<? extends Converter<ResponseBody, ?>> value();
}
