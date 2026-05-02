package net.minecraft.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("all")
public class SimpleRegistry<T> {
  private final Map<ResourceLocation, T> values = new LinkedHashMap<>();

  public void add(T value) {
    if (value != null) values.put(ResourceLocation.parse(value.getClass().getSimpleName().toLowerCase()), value);
  }

  public Set<Map.Entry<ResourceLocation, T>> entrySet() {
    return values.entrySet();
  }

  public ResourceLocation getKey(T value) {
    for (Map.Entry<ResourceLocation, T> entry : values.entrySet()) {
      if (entry.getValue() == value) return entry.getKey();
    }
    return ResourceLocation.parse("minecraft:air");
  }

  public T getValue(ResourceLocation id) {
    return values.get(id);
  }

  public Optional<Holder.Reference<T>> getRandom(Random random) {
    return values.values().stream().findFirst().map(SimpleHolder::new);
  }

  public Holder.Reference<T> get(ResourceKey<T> key) {
    T value = getValue(key == null ? null : key.location());
    return value == null ? null : new SimpleHolder<>(value);
  }

  private record SimpleHolder<T>(T value) implements Holder.Reference<T> {}
}
