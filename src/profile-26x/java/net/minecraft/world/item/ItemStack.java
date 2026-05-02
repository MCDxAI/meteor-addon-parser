package net.minecraft.world.item;

@SuppressWarnings("all")
public class ItemStack {
  private Item item;

  public ItemStack() {
    this(new Item(), 1);
  }

  public ItemStack(Item item) {
    this(item, 1);
  }

  public ItemStack(Item item, int count) {
    this.item = item;
  }

  public Item getItem() {
    return item;
  }
}
