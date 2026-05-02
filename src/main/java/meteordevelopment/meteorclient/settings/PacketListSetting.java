package meteordevelopment.meteorclient.settings;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class PacketListSetting extends Setting<Set<Class<?>>> {
  private java.util.function.Predicate<Class<?>> filter;

  private PacketListSetting(
      String name,
      String description,
      Set<Class<?>> defaultValue,
      Consumer<Set<Class<?>>> onChanged,
      Consumer<Setting<Set<Class<?>>>> onModuleActivated,
      IVisible visible,
      java.util.function.Predicate<Class<?>> filter) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.filter = filter;
  }

  @Override
  protected Set<Class<?>> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(Set<Class<?>> value) {
    return value != null;
  }

  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  @Override
  protected Set<Class<?>> load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder extends SettingBuilder<Builder, Set<Class<?>>, PacketListSetting> {
    private java.util.function.Predicate<Class<?>> filter;

    public Builder() {
      super(new LinkedHashSet<>());
    }

    public Builder filter(java.util.function.Predicate<Class<?>> filter) {
      this.filter = filter;
      return this;
    }

    @Override
    public PacketListSetting build() {
      return new PacketListSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
    }
  }
}
