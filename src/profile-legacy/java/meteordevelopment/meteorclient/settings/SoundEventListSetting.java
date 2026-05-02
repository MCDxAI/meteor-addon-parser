package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.List;

public class SoundEventListSetting extends Setting<List<net.minecraft.class_3414>> {
  private SoundEventListSetting(
      String name,
      String description,
      List<net.minecraft.class_3414> defaultValue,
      java.util.function.Consumer<List<net.minecraft.class_3414>> onChanged,
      java.util.function.Consumer<Setting<List<net.minecraft.class_3414>>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected List<net.minecraft.class_3414> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(List<net.minecraft.class_3414> value) {
    return value != null;
  }

  @Override
  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  @Override
  protected List<net.minecraft.class_3414> load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<
          Builder, List<net.minecraft.class_3414>, SoundEventListSetting> {
    public Builder() {
      super(new ArrayList<>());
    }

    public Builder defaultValue(net.minecraft.class_3414... values) {
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
