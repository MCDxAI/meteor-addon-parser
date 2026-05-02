package org.apache.catalina.loader;

@SuppressWarnings("all")
public abstract class WebappClassLoaderBase extends ClassLoader {
  private boolean delegate;

  public WebappClassLoaderBase() {
    super(Thread.currentThread().getContextClassLoader());
  }

  public WebappClassLoaderBase(ClassLoader parent) {
    super(parent);
  }

  public boolean getDelegate() {
    return delegate;
  }

  public void setDelegate(boolean delegate) {
    this.delegate = delegate;
  }

  public abstract WebappClassLoaderBase copyWithoutTransformers();

  protected void clearReferences() {}
}
