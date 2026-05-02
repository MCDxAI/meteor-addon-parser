package org.apache.catalina;

@SuppressWarnings("all")
public class LifecycleException extends Exception {
  public LifecycleException() {}

  public LifecycleException(String message) {
    super(message);
  }
}
