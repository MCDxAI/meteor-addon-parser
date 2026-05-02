package meteordevelopment.meteorclient.settings;

public class GenericSetting<T> extends Setting<T> {
  private GenericSetting(
      String name,
      String description,
      T defaultValue,
      java.util.function.Consumer<T> onChanged,
      java.util.function.Consumer<Setting<T>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected T parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(T value) {
    return value != null;
  }

  @Override
  protected net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag tag) {
    return tag;
  }

  @Override
  protected T load(net.minecraft.nbt.CompoundTag tag) {
    return value;
  }

  public static class Builder<T> extends Setting.SettingBuilder<Builder<T>, T, GenericSetting<T>> {
    public Builder() {
      super(null);
    }

    @Override
    public GenericSetting<T> build() {
      return new GenericSetting<>(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
