package org.lwjgl.opengl;

@SuppressWarnings("all")
public final class GL {
  private GL() {}

  public static GLCapabilities getCapabilities() {
    return new GLCapabilities();
  }

  public static GLCapabilities createCapabilities() {
    return new GLCapabilities();
  }
}
