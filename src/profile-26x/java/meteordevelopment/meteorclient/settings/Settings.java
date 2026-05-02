package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.RainbowColors;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class Settings implements Iterable<SettingGroup> {
  private SettingGroup defaultGroup;

  public final List<SettingGroup> groups = new ArrayList<>(1);

  public void onActivated() {
    for (SettingGroup group : groups) {
      for (Setting<?> setting : group) {
        setting.onActivated();
      }
    }
  }

  public Setting<?> get(String name) {
    for (SettingGroup sg : this) {
      for (Setting<?> setting : sg) {
        if (name.equalsIgnoreCase(setting.name)) return setting;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public <T> Setting<T> get(String name, Class<T> tClass) {
    for (SettingGroup sg : this) {
      for (Setting<?> setting : sg) {
        Object dv = setting.getDefaultValue();
        if (dv != null && name.equalsIgnoreCase(setting.name) && tClass.equals(dv.getClass())) {
          return (Setting<T>) setting;
        }
      }
    }
    return null;
  }

  public void reset() {
    for (SettingGroup group : groups) {
      for (Setting<?> setting : group) {
        setting.reset();
      }
    }
  }

  public SettingGroup getGroup(String name) {
    for (SettingGroup sg : this) {
      if (sg.name.equals(name)) return sg;
    }
    return null;
  }

  public SettingGroup getDefaultGroup() {
    if (defaultGroup == null) defaultGroup = createGroup("General");
    return defaultGroup;
  }

  public SettingGroup createGroup(String name, boolean expanded) {
    SettingGroup group = new SettingGroup(name, expanded);
    groups.add(group);
    return group;
  }

  public SettingGroup createGroup(String name) {
    return createGroup(name, true);
  }

  @SuppressWarnings("unchecked")
  public void registerColorSettings(Module module) {
    for (SettingGroup group : this) {
      for (Setting<?> setting : group) {
        setting.module = module;

        if (setting instanceof ColorSetting) {
          RainbowColors.addSetting((Setting<SettingColor>) setting);
        } else if (setting instanceof ColorListSetting) {
          RainbowColors.addSettingList((Setting<List<SettingColor>>) setting);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void unregisterColorSettings() {
    for (SettingGroup group : this) {
      for (Setting<?> setting : group) {
        if (setting instanceof ColorSetting) {
          RainbowColors.removeSetting((Setting<SettingColor>) setting);
        } else if (setting instanceof ColorListSetting) {
          RainbowColors.removeSettingList((Setting<List<SettingColor>>) setting);
        }
      }
    }
  }

  public void tick(
      meteordevelopment.meteorclient.gui.widgets.containers.WContainer settings,
      meteordevelopment.meteorclient.gui.GuiTheme theme) {}

  @Override
  public Iterator<SettingGroup> iterator() {
    return groups.iterator();
  }

  @Override
  public void forEach(Consumer<? super SettingGroup> action) {
    groups.forEach(action);
  }

  public net.minecraft.nbt.CompoundTag toTag() {
    return new net.minecraft.nbt.CompoundTag();
  }

  public net.minecraft.nbt.CompoundTag toTag(net.minecraft.nbt.CompoundTag tag) {
    return tag;
  }


  public Settings fromTag(net.minecraft.nbt.CompoundTag tag) {
    return this;
  }
}
