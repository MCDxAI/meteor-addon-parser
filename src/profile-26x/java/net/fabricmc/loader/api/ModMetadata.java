package net.fabricmc.loader.api;

import net.fabricmc.loader.api.metadata.CustomValue;

@SuppressWarnings("all")
public class ModMetadata {
  public String getId() {
    return "";
  }

  public String getName() {
    return "";
  }

  public Version getVersion() {
    return new Version();
  }

  public CustomValue getCustomValue(String key) {
    return new CustomValue() {};
  }
}
