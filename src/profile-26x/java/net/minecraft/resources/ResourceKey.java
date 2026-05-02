package net.minecraft.resources;

@SuppressWarnings("all")
public class ResourceKey<T> {
  private final ResourceLocation location;

  private ResourceKey(ResourceLocation location) {
    this.location = location == null ? new ResourceLocation() : location;
  }

  public static <T> ResourceKey<T> create(ResourceKey<? extends Object> registry, ResourceLocation location) {
    return new ResourceKey<>(location);
  }

  public static <T> ResourceKey<T> create(ResourceKey<? extends Object> registry, Identifier location) {
    return new ResourceKey<>(location);
  }

  public static <T> ResourceKey<T> createRegistryKey(ResourceLocation location) {
    return new ResourceKey<>(location);
  }

  public ResourceLocation location() {
    return location;
  }
}
