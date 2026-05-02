package net.minecraft.world.level.block.state.properties;

import java.util.function.Predicate;

@SuppressWarnings("all")
public class EnumProperty<T extends Enum<T> & Comparable<T>> extends Property<T> {
  public EnumProperty() {}

  public static <T extends Enum<T> & Comparable<T>> EnumProperty<T> create(
      String name, Class<T> enumClass) {
    return new EnumProperty<>();
  }

  @SafeVarargs
  public static <T extends Enum<T> & Comparable<T>> EnumProperty<T> create(
      String name, Class<T> enumClass, T... values) {
    return new EnumProperty<>();
  }

  public static <T extends Enum<T> & Comparable<T>> EnumProperty<T> create(
      String name, Class<T> enumClass, Predicate<T> filter) {
    return new EnumProperty<>();
  }
}
