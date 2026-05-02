package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemListSetting extends Setting<List<net.minecraft.world.item.Item>> {
  private java.util.function.Predicate<net.minecraft.world.item.Item> filter;

  private ItemListSetting(
      String name,
      String description,
      List<net.minecraft.world.item.Item> defaultValue,
      java.util.function.Consumer<List<net.minecraft.world.item.Item>> onChanged,
      java.util.function.Consumer<Setting<List<net.minecraft.world.item.Item>>> onModuleActivated,
      IVisible visible,
      java.util.function.Predicate<net.minecraft.world.item.Item> filter) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.filter = filter;
  }

  @Override
  protected List<net.minecraft.world.item.Item> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(List<net.minecraft.world.item.Item> value) {
    return value != null;
  }

  @Override
  protected net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag tag) {
    return tag;
  }

  @Override
  protected List<net.minecraft.world.item.Item> load(net.minecraft.nbt.CompoundTag tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<Builder, List<net.minecraft.world.item.Item>, ItemListSetting> {
    private java.util.function.Predicate<net.minecraft.world.item.Item> filter;

    public Builder() {
      super(new ArrayList<>());
    }

    public Builder defaultValue(net.minecraft.world.item.Item... items) {
      this.defaultValue = new ArrayList<>(Arrays.asList(items));
      return this;
    }

    public Builder filter(java.util.function.Predicate<net.minecraft.world.item.Item> filter) {
      this.filter = filter;
      return this;
    }

    @Override
    public ItemListSetting build() {
      return new ItemListSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
    }
  }
}
