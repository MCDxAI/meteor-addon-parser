package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;

public class BlockSetting extends Setting<net.minecraft.world.level.block.Block> {
  private java.util.function.Predicate<net.minecraft.world.level.block.Block> filter;

  private BlockSetting(
      String name,
      String description,
      net.minecraft.world.level.block.Block defaultValue,
      Consumer<net.minecraft.world.level.block.Block> onChanged,
      Consumer<Setting<net.minecraft.world.level.block.Block>> onModuleActivated,
      IVisible visible,
      java.util.function.Predicate<net.minecraft.world.level.block.Block> filter) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.filter = filter;
  }

  @Override
  protected net.minecraft.world.level.block.Block parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(net.minecraft.world.level.block.Block value) {
    return value != null && (filter == null || filter.test(value));
  }

  @Override
  protected net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag tag) {
    return tag;
  }

  @Override
  protected net.minecraft.world.level.block.Block load(net.minecraft.nbt.CompoundTag tag) {
    return value;
  }

  public static class Builder
      extends SettingBuilder<Builder, net.minecraft.world.level.block.Block, BlockSetting> {
    private java.util.function.Predicate<net.minecraft.world.level.block.Block> filter;

    public Builder() {
      super(null);
    }

    public Builder defaultValue(net.minecraft.world.level.block.Block block) {
      this.defaultValue = block;
      return this;
    }

    public Builder filter(java.util.function.Predicate<net.minecraft.world.level.block.Block> filter) {
      this.filter = filter;
      return this;
    }

    @Override
    public BlockSetting build() {
      return new BlockSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
    }
  }
}
