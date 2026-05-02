package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.List;

public class SoundEventListSetting extends Setting<List<net.minecraft.sounds.SoundEvent>> {
  private SoundEventListSetting(
      String name,
      String description,
      List<net.minecraft.sounds.SoundEvent> defaultValue,
      java.util.function.Consumer<List<net.minecraft.sounds.SoundEvent>> onChanged,
      java.util.function.Consumer<Setting<List<net.minecraft.sounds.SoundEvent>>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected List<net.minecraft.sounds.SoundEvent> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(List<net.minecraft.sounds.SoundEvent> value) {
    return value != null;
  }

  @Override
  protected net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag tag) {
    return tag;
  }

  @Override
  protected List<net.minecraft.sounds.SoundEvent> load(net.minecraft.nbt.CompoundTag tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<
          Builder, List<net.minecraft.sounds.SoundEvent>, SoundEventListSetting> {
    public Builder() {
      super(new ArrayList<>());
    }

    public Builder defaultValue(net.minecraft.sounds.SoundEvent... values) {
      this.defaultValue =
          values == null ? new ArrayList<>() : new ArrayList<>(java.util.Arrays.asList(values));
      return this;
    }

    @Override
    public SoundEventListSetting build() {
      return new SoundEventListSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
