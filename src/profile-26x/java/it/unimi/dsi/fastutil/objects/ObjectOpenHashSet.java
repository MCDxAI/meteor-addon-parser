package it.unimi.dsi.fastutil.objects;

import java.util.Collection;
import java.util.LinkedHashSet;

@SuppressWarnings("all")
public class ObjectOpenHashSet<K> extends LinkedHashSet<K> {
  public ObjectOpenHashSet() {}

  public ObjectOpenHashSet(int expected) {
    super(Math.max(0, expected));
  }

  public ObjectOpenHashSet(Collection<? extends K> values) {
    super(values == null ? java.util.List.of() : values);
  }
}
