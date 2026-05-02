package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockListSetting extends Setting<List<net.minecraft.world.level.block.Block>> {
  private java.util.function.Predicate<net.minecraft.world.level.block.Block> filter;

  private BlockListSetting(
      String name,
      String description,
      List<net.minecraft.world.level.block.Block> defaultValue,
      java.util.function.Consumer<List<net.minecraft.world.level.block.Block>> onChanged,
      java.util.function.Consumer<Setting<List<net.minecraft.world.level.block.Block>>> onModuleActivated,
      IVisible visible,
      java.util.function.Predicate<net.minecraft.world.level.block.Block> filter) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.filter = filter;
  }

  @Override
  protected List<net.minecraft.world.level.block.Block> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(List<net.minecraft.world.level.block.Block> value) {
    return value != null;
  }

  @Override
  protected net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag tag) {
    return tag;
  }

  @Override
  protected List<net.minecraft.world.level.block.Block> load(net.minecraft.nbt.CompoundTag tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<Builder, List<net.minecraft.world.level.block.Block>, BlockListSetting> {
    private java.util.function.Predicate<net.minecraft.world.level.block.Block> filter;

    public Builder() {
      super(new ArrayList<>());
    }

    public Builder defaultValue(net.minecraft.world.level.block.Block... blocks) {
      this.defaultValue = new ArrayList<>(Arrays.asList(blocks));
      return this;
    }

    public Builder filter(java.util.function.Predicate<net.minecraft.world.level.block.Block> filter) {
      this.filter = filter;
      return this;
    }

    @Override
    public BlockListSetting build() {
      return new BlockListSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
    }
  }
}
