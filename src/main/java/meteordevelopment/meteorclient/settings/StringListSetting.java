package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringListSetting extends Setting<List<String>> {
  private StringListSetting(
      String name,
      String description,
      List<String> defaultValue,
      java.util.function.Consumer<List<String>> onChanged,
      java.util.function.Consumer<Setting<List<String>>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected List<String> parseImpl(String str) {
    return new ArrayList<>(Arrays.asList(str.split(",")));
  }

  @Override
  protected boolean isValueValid(List<String> value) {
    return value != null;
  }

  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  protected List<String> load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<Builder, List<String>, StringListSetting> {
    public Builder() {
      super(new ArrayList<>());
    }

    public Builder defaultValue(String... values) {
      this.defaultValue = new ArrayList<>(Arrays.asList(values));
      return this;
    }

    public Builder renderer(Class<?> renderer) {
      return this;
    }

    @Override
    public StringListSetting build() {
      return new StringListSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
