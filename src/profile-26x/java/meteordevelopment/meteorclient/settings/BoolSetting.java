package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;

public class BoolSetting extends Setting<Boolean> {
  private BoolSetting(
      String name,
      String description,
      Boolean defaultValue,
      Consumer<Boolean> onChanged,
      Consumer<Setting<Boolean>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected Boolean parseImpl(String str) {
    return Boolean.parseBoolean(str);
  }

  @Override
  protected boolean isValueValid(Boolean value) {
    return value != null;
  }

  @Override
  protected net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag tag) {
    return tag;
  }

  @Override
  protected Boolean load(net.minecraft.nbt.CompoundTag tag) {
    return value;
  }

  public static class Builder extends Setting.SettingBuilder<Builder, Boolean, BoolSetting> {
    public Builder() {
      super(false);
    }

    @Override
    public BoolSetting build() {
      return new BoolSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
