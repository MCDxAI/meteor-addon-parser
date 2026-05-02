package net.minecraft.resources;

@SuppressWarnings("all")
public class Identifier extends ResourceLocation {
  public Identifier() {
    super();
  }

  private Identifier(String namespace, String path) {
    super(namespace, path);
  }

  public static Identifier fromNamespaceAndPath(String namespace, String path) {
    return new Identifier(namespace, path);
  }

  public static Identifier of(String value) {
    ResourceLocation location = ResourceLocation.parse(value);
    return new Identifier(location.getNamespace(), location.getPath());
  }

  public static Identifier parse(String value) {
    return of(value);
  }
}
