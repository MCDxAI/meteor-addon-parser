package meteordevelopment.meteorclient.settings;

import java.util.LinkedHashSet;
import java.util.Set;

public class EntityTypeListSetting extends Setting<Set<net.minecraft.class_1299>> {
  private boolean onlyAttackable;

  private EntityTypeListSetting(
      String name,
      String description,
      Set<net.minecraft.class_1299> defaultValue,
      java.util.function.Consumer<Set<net.minecraft.class_1299>> onChanged,
      java.util.function.Consumer<Setting<Set<net.minecraft.class_1299>>> onModuleActivated,
      IVisible visible,
      boolean onlyAttackable) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.onlyAttackable = onlyAttackable;
  }

  @Override
  protected Set<net.minecraft.class_1299> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(Set<net.minecraft.class_1299> value) {
    return value != null;
  }

  @Override
  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  @Override
  protected Set<net.minecraft.class_1299> load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<
          Builder, Set<net.minecraft.class_1299>, EntityTypeListSetting> {
    private boolean onlyAttackable;
    private java.util.function.Predicate<net.minecraft.class_1299> filter;

    public Builder() {
      super(new LinkedHashSet<>());
    }

    @SuppressWarnings("unchecked")
    public Builder defaultValue(net.minecraft.class_1299... values) {
      LinkedHashSet<net.minecraft.class_1299> set = new LinkedHashSet<>();
      for (net.minecraft.class_1299 value : values) set.add(value);
      this.defaultValue = set;
      return this;
    }

    public Builder onlyAttackable() {
      this.onlyAttackable = true;
      return this;
    }

    public Builder filter(java.util.function.Predicate<net.minecraft.class_1299> filter) {
      this.filter = filter;
      return this;
    }

    @Override
    public EntityTypeListSetting build() {
      return new EntityTypeListSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible, onlyAttackable);
    }
  }
}
