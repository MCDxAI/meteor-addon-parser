package net.minecraft.world.level.block.state;

import net.minecraft.world.level.block.Block;

@SuppressWarnings("all")
public class BlockState {
  private final Block block;

  public BlockState() {
    this(null);
  }

  public BlockState(Block block) {
    this.block = block;
  }

  public Block getBlock() {
    return block;
  }

  public boolean is(Block block) {
    return this.block == block;
  }
}
