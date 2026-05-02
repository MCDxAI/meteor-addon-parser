package net.minecraft.resources;

@SuppressWarnings("all")
public class ResourceLocation {
  private final String namespace;
  private final String path;

  public ResourceLocation() {
    this("minecraft", "air");
  }

  protected ResourceLocation(String namespace, String path) {
    this.namespace = namespace == null || namespace.isBlank() ? "minecraft" : namespace;
    this.path = path == null || path.isBlank() ? "air" : path;
  }

  public static ResourceLocation parse(String value) {
    if (value == null) return new ResourceLocation();
    int separator = value.indexOf(':');
    if (separator >= 0) {
      return new ResourceLocation(value.substring(0, separator), value.substring(separator + 1));
    }
    return new ResourceLocation("minecraft", value);
  }

  public static ResourceLocation fromNamespaceAndPath(String namespace, String path) {
    return new ResourceLocation(namespace, path);
  }

  public String getNamespace() {
    return namespace;
  }

  public String getPath() {
    return path;
  }

  @Override
  public String toString() {
    return namespace + ":" + path;
  }
}
