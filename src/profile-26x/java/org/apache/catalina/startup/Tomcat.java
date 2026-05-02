package org.apache.catalina.startup;

import jakarta.servlet.Servlet;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;

@SuppressWarnings("all")
public class Tomcat {
  private final Connector connector = new Connector();

  public Tomcat() {}

  public void setPort(int port) {}

  public void setBaseDir(String baseDir) {}

  public Context addContext(String contextPath, String docBase) {
    return new StandardContext();
  }

  public Connector getConnector() {
    return connector;
  }

  public void start() throws LifecycleException {}

  public void stop() throws LifecycleException {}

  public void destroy() throws LifecycleException {}

  public static class SimpleContext implements Context {
    public SimpleContext() {}

    @Override
    public Wrapper createWrapper() {
      return new SimpleWrapper();
    }

    @Override
    public void addChild(Container child) {}

    @Override
    public void addServletMappingDecoded(String pattern, String name) {}
  }

  public static class SimpleWrapper implements Wrapper {
    public SimpleWrapper() {}

    @Override
    public void setName(String name) {}

    @Override
    public void setServlet(Servlet servlet) {}

    @Override
    public void setLoadOnStartup(int loadOnStartup) {}

    @Override
    public void setAsyncSupported(boolean asyncSupported) {}
  }
}
