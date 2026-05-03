package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class SettingGroup implements Iterable<Setting<?>> {
  public final String name;
  public boolean sectionExpanded;

  final List<Setting<?>> settings = new ArrayList<>(1);

  SettingGroup(String name, boolean sectionExpanded) {
    this.name = name;
    this.sectionExpanded = sectionExpanded;
  }

  public Setting<?> get(String name) {
    for (Setting<?> setting : this) {
      if (setting.name.equals(name)) return setting;
    }
    return null;
  }

  public <T extends Setting<?>> T add(T setting) {
    settings.add(setting);
    return setting;
  }

  public Setting<?> getByIndex(int index) {
    return settings.get(index);
  }

  public boolean wasChanged() {
    for (Setting<?> setting : settings) {
      if (setting.wasChanged()) return true;
    }
    return false;
  }

  @Override
  public Iterator<Setting<?>> iterator() {
    return settings.iterator();
  }

  @Override
  public void forEach(Consumer<? super Setting<?>> action) {
    settings.forEach(action);
  }

  public net.minecraft.class_2487 toTag() {
    return new net.minecraft.class_2487();
  }

  public SettingGroup fromTag(net.minecraft.class_2487 tag) {
    return this;
  }
}
