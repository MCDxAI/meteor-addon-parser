package net.minecraft.core;

@SuppressWarnings("all")
public interface Holder<T> {
  T value();

  interface Reference<T> extends Holder<T> {}
}
