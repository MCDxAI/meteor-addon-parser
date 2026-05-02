package net.minecraft.core;

@SuppressWarnings("all")
public class BlockPos {
  public static final BlockPos ZERO = new BlockPos();

  public BlockPos() {}

  public BlockPos(int x, int y, int z) {}

  public static class MutableBlockPos extends BlockPos {
    public MutableBlockPos() {}

    public MutableBlockPos(int x, int y, int z) {
      super(x, y, z);
    }

    public MutableBlockPos set(BlockPos pos) {
      return this;
    }

    public MutableBlockPos set(int x, int y, int z) {
      return this;
    }
  }
}
