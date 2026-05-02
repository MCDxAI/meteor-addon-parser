package meteordevelopment.meteorclient.utils.misc;

public final class Names {
  private Names() {}

  public static String get(net.minecraft.world.item.Item item) {
    return String.valueOf(item);
  }

  public static String get(net.minecraft.world.level.block.Block block) {
    return String.valueOf(block);
  }

  public static String get(net.minecraft.core.Holder sound) {
    return String.valueOf(sound);
  }
}
