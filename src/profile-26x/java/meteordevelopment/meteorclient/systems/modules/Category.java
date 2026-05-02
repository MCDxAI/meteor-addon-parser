package meteordevelopment.meteorclient.systems.modules;

import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;

public class Category {
  public final String name;
  public final Supplier<ItemStack> icon;
  private final int nameHash;

  public Category(String name, Supplier<ItemStack> icon) {
    this.name = name;
    this.icon = icon == null ? ItemStack::new : icon;
    this.nameHash = name == null ? 0 : name.hashCode();
  }

  public Category(String name) {
    this(name, null);
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Category category)) return false;
    return Objects.equals(this.name, category.name);
  }

  @Override
  public int hashCode() {
    return nameHash;
  }
}
