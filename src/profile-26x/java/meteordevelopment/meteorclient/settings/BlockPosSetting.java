package meteordevelopment.meteorclient.settings;

public class BlockPosSetting extends Setting<net.minecraft.core.BlockPos> {
  private BlockPosSetting(
      String name,
      String description,
      net.minecraft.core.BlockPos defaultValue,
      java.util.function.Consumer<net.minecraft.core.BlockPos> onChanged,
      java.util.function.Consumer<Setting<net.minecraft.core.BlockPos>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected net.minecraft.core.BlockPos parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(net.minecraft.core.BlockPos value) {
    return value != null;
  }

  @Override
  protected net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag tag) {
    return tag;
  }

  @Override
  protected net.minecraft.core.BlockPos load(net.minecraft.nbt.CompoundTag tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<Builder, net.minecraft.core.BlockPos, BlockPosSetting> {
    public Builder() {
      super(new net.minecraft.core.BlockPos());
    }

    @Override
    public BlockPosSetting build() {
      return new BlockPosSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
