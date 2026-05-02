package net.minecraft.core;

import net.minecraft.resources.ResourceKey;

@SuppressWarnings("all")
public class Registry<T> extends SimpleRegistry<T> {
  public static <V, T extends V> T register(Registry<V> registry, ResourceKey<V> key, T value) {
    if (registry != null && value != null) registry.add(value);
    return value;
  }
}
