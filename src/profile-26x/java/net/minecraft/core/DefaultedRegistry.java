package net.minecraft.core;

import java.util.Iterator;

@SuppressWarnings("all")
public class DefaultedRegistry<T> extends Registry<T> implements Iterable<T> {
  public DefaultedRegistry() {}

  @Override
  public Iterator<T> iterator() {
    return entrySet().stream().map(java.util.Map.Entry::getValue).iterator();
  }
}
