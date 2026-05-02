package org.apache.catalina.core;

import org.apache.catalina.Context;
import org.apache.catalina.Loader;

@SuppressWarnings("all")
public class StandardContext extends org.apache.catalina.startup.Tomcat.SimpleContext
    implements Context {
  public StandardContext() {}

  public void setLoader(Loader loader) {}
}
