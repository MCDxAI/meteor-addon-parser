package net.minecraft.world.level.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("all")
public class Block {
  public Block() {}

  public BlockState defaultBlockState() {
    return new BlockState(this);
  }

  public static Block byItem(Item item) {
    return new Block();
  }

  public Item asItem() {
    return new Item();
  }
}
