package meteordevelopment.meteorclient.settings;

public class EnumSetting<T> extends Setting<T> {
  private EnumSetting(
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
  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  @Override
  protected T load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder<T> extends Setting.SettingBuilder<Builder<T>, T, EnumSetting<T>> {
    public Builder() {
      super(null);
    }

    @Override
    public EnumSetting<T> build() {
      return new EnumSetting<>(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
