package meteordevelopment.meteorclient.settings;

import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.world.entity.EntityType;

public class EntityTypeListSetting extends Setting<Set<EntityType<?>>> {
  private boolean onlyAttackable;

  private EntityTypeListSetting(
      String name,
      String description,
      Set<EntityType<?>> defaultValue,
      java.util.function.Consumer<Set<EntityType<?>>> onChanged,
      java.util.function.Consumer<Setting<Set<EntityType<?>>>> onModuleActivated,
      IVisible visible,
      boolean onlyAttackable) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.onlyAttackable = onlyAttackable;
  }

  @Override
  protected Set<EntityType<?>> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(Set<EntityType<?>> value) {
    return value != null;
  }

  @Override
  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  @Override
  protected Set<EntityType<?>> load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<Builder, Set<EntityType<?>>, EntityTypeListSetting> {
    private boolean onlyAttackable;
    private java.util.function.Predicate<EntityType<?>> filter;

    public Builder() {
      super(new LinkedHashSet<>());
    }

    @SuppressWarnings("unchecked")
    public Builder defaultValue(EntityType<?>... values) {
      LinkedHashSet<EntityType<?>> set = new LinkedHashSet<>();
      for (EntityType<?> value : values) set.add(value);
      this.defaultValue = set;
      return this;
    }

    public Builder onlyAttackable() {
      this.onlyAttackable = true;
      return this;
    }

    public Builder filter(java.util.function.Predicate<EntityType<?>> filter) {
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
