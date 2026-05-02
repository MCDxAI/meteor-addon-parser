package net.minecraft.world.level.block.state.properties;

@SuppressWarnings("all")
public class BooleanProperty extends Property<Boolean> {
  public BooleanProperty() {}

  public static BooleanProperty create(String name) {
    return new BooleanProperty();
  }
}
