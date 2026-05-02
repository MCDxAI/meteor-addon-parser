package meteordevelopment.meteorclient.systems;

public class System<T extends System<?>> {
  public final String name;

  public final meteordevelopment.meteorclient.settings.Settings settings =
      new meteordevelopment.meteorclient.settings.Settings();

  public System(String name) {
    this.name = name;
  }

  public void init() {}

  public void load(java.io.File folder) {}

  public void save() {}

  public void save(java.io.File folder) {}

  public net.minecraft.nbt.CompoundTag toTag() {
    return new net.minecraft.nbt.CompoundTag();
  }

  @SuppressWarnings("unchecked")
  public T fromTag(net.minecraft.nbt.CompoundTag tag) {
    return (T) this;
  }
}
