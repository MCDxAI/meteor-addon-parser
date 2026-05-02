package net.fabricmc.loader.api;

@SuppressWarnings("all")
public class Version {
  public String getFriendlyString() {
    return "";
  }

  @Override
  public String toString() {
    return getFriendlyString();
  }
}
