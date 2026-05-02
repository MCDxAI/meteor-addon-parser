package meteordevelopment.meteorclient.settings;

import com.cope.addonparser.stubs.StubSupport;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EnchantmentListSetting extends Setting<Set<net.minecraft.resources.ResourceKey>> {
  private EnchantmentListSetting(
      String name,
      String description,
      Set<net.minecraft.resources.ResourceKey> defaultValue,
      Consumer<Set<net.minecraft.resources.ResourceKey>> onChanged,
      Consumer<Setting<Set<net.minecraft.resources.ResourceKey>>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected Set<net.minecraft.resources.ResourceKey> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(Set<net.minecraft.resources.ResourceKey> value) {
    return value != null;
  }

  @Override
  protected net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag tag) {
    return tag;
  }

  @Override
  protected Set<net.minecraft.resources.ResourceKey> load(net.minecraft.nbt.CompoundTag tag) {
    return value;
  }

  public static class Builder
      extends SettingBuilder<Builder, Set<net.minecraft.resources.ResourceKey>, EnchantmentListSetting> {
    public Builder() {
      super(new LinkedHashSet<>());
    }

    public Builder defaultValue(net.minecraft.resources.ResourceKey... enchantments) {
      this.defaultValue = new LinkedHashSet<>(Arrays.asList(enchantments));
      return this;
    }

    public Builder defaultValue(net.minecraft.world.item.enchantment.Enchantment... enchantments) {
      LinkedHashSet<net.minecraft.resources.ResourceKey> keys = new LinkedHashSet<>();
      if (enchantments != null) {
        for (net.minecraft.world.item.enchantment.Enchantment ignored : enchantments) {
          keys.add(
              (net.minecraft.resources.ResourceKey) StubSupport.defaultValue(net.minecraft.resources.ResourceKey.class));
        }
      }
      this.defaultValue = keys;
      return this;
    }

    @Override
    public EnchantmentListSetting build() {
      return new EnchantmentListSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
