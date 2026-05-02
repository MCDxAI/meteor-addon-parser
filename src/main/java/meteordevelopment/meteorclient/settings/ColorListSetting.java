package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class ColorListSetting extends Setting<List<SettingColor>> {
  public ColorListSetting(
      String name,
      String description,
      List<SettingColor> defaultValue,
      java.util.function.Consumer<List<SettingColor>> onChanged,
      java.util.function.Consumer<Setting<List<SettingColor>>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected List<SettingColor> parseImpl(String str) {
    return new ArrayList<>();
  }

  @Override
  protected boolean isValueValid(List<SettingColor> value) {
    return value != null;
  }

  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  protected List<SettingColor> load(net.minecraft.class_2487 tag) {
    return value;
  }
}
