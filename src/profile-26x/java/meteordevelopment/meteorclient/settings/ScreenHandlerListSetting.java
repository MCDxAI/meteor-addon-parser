package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.List;

public class ScreenHandlerListSetting extends Setting<List<Object>> {
  private ScreenHandlerListSetting(
      String name,
      String description,
      List<Object> defaultValue,
      java.util.function.Consumer<List<Object>> onChanged,
      java.util.function.Consumer<Setting<List<Object>>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected List<Object> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(List<Object> value) {
    return value != null;
  }

  @Override
  protected net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag tag) {
    return tag;
  }

  @Override
  protected List<Object> load(net.minecraft.nbt.CompoundTag tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<Builder, List<Object>, ScreenHandlerListSetting> {
    public Builder() {
      super(new ArrayList<>());
    }

    @Override
    public ScreenHandlerListSetting build() {
      return new ScreenHandlerListSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
