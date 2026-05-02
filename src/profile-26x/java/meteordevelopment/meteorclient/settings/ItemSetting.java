package meteordevelopment.meteorclient.settings;

public class ItemSetting extends Setting<net.minecraft.world.item.Item> {
  private java.util.function.Predicate<net.minecraft.world.item.Item> filter;

  private ItemSetting(
      String name,
      String description,
      net.minecraft.world.item.Item defaultValue,
      java.util.function.Consumer<net.minecraft.world.item.Item> onChanged,
      java.util.function.Consumer<Setting<net.minecraft.world.item.Item>> onModuleActivated,
      IVisible visible,
      java.util.function.Predicate<net.minecraft.world.item.Item> filter) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.filter = filter;
  }

  @Override
  protected net.minecraft.world.item.Item parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(net.minecraft.world.item.Item value) {
    return value != null && (filter == null || filter.test(value));
  }

  @Override
  protected net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag tag) {
    return tag;
  }

  @Override
  protected net.minecraft.world.item.Item load(net.minecraft.nbt.CompoundTag tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<Builder, net.minecraft.world.item.Item, ItemSetting> {
    private java.util.function.Predicate<net.minecraft.world.item.Item> filter;

    public Builder() {
      super(new net.minecraft.world.item.Item());
    }

    public Builder filter(java.util.function.Predicate<net.minecraft.world.item.Item> filter) {
      this.filter = filter;
      return this;
    }

    @Override
    public ItemSetting build() {
      return new ItemSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
    }
  }
}
