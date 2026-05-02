package org.apache.catalina;

import jakarta.servlet.Servlet;

@SuppressWarnings("all")
public interface Wrapper extends Container {
  void setName(String name);

  void setServlet(Servlet servlet);

  void setLoadOnStartup(int loadOnStartup);

  void setAsyncSupported(boolean asyncSupported);
}
