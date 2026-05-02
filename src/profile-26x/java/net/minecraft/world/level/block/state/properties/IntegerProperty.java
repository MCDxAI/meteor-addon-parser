package net.minecraft.world.level.block.state.properties;

@SuppressWarnings("all")
public class IntegerProperty extends Property<Integer> {
  public IntegerProperty() {}

  public static IntegerProperty create(String name, int min, int max) {
    return new IntegerProperty();
  }
}
