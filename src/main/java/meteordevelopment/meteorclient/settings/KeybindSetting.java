package meteordevelopment.meteorclient.settings;

import meteordevelopment.meteorclient.utils.misc.Keybind;

public class KeybindSetting extends Setting<Keybind> {
  public KeybindSetting(
      String name,
      String description,
      Keybind defaultValue,
      java.util.function.Consumer<Keybind> onChanged,
      java.util.function.Consumer<Setting<Keybind>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  public KeybindSetting(
      String name,
      String description,
      Keybind defaultValue,
      java.util.function.Consumer<Keybind> onChanged,
      java.util.function.Consumer<Setting<Keybind>> onModuleActivated,
      IVisible visible,
      Runnable onAction) {
    this(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected Keybind parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(Keybind value) {
    return value != null;
  }

  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  protected Keybind load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder extends Setting.SettingBuilder<Builder, Keybind, KeybindSetting> {
    public Builder() {
      super(Keybind.none());
    }

    @Override
    public KeybindSetting build() {
      return new KeybindSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
