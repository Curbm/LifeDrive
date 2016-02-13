package com.skyfire.hipda.api;

public class ApiException extends Exception {

  private int mCode = -1;

  public ApiException() {
    super("Unknown error");
  }


  public ApiException(int code) {
    super();
    mCode = code;
  }

  public ApiException(String msg) {
    super(msg);
  }


  public ApiException(String msg, Throwable throwable) {
    super(msg, throwable);
  }


  public ApiException(int code, String msg) {
    super(msg);
    mCode = code;
  }

  public ApiException(int code, String msg, Throwable throwable) {
    super(msg, throwable);
    mCode = code;
  }

  public int getErrorCode() {
    return mCode;
  }
}
