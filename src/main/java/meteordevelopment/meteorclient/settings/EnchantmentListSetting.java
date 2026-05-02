package meteordevelopment.meteorclient.settings;

import com.cope.addonparser.stubs.StubSupport;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EnchantmentListSetting extends Setting<Set<net.minecraft.class_5321>> {
  private EnchantmentListSetting(
      String name,
      String description,
      Set<net.minecraft.class_5321> defaultValue,
      Consumer<Set<net.minecraft.class_5321>> onChanged,
      Consumer<Setting<Set<net.minecraft.class_5321>>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected Set<net.minecraft.class_5321> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(Set<net.minecraft.class_5321> value) {
    return value != null;
  }

  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  protected Set<net.minecraft.class_5321> load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder
      extends SettingBuilder<Builder, Set<net.minecraft.class_5321>, EnchantmentListSetting> {
    public Builder() {
      super(new LinkedHashSet<>());
    }

    public Builder defaultValue(net.minecraft.class_5321... enchantments) {
      this.defaultValue = new LinkedHashSet<>(Arrays.asList(enchantments));
      return this;
    }

    public Builder defaultValue(net.minecraft.class_1887... enchantments) {
      LinkedHashSet<net.minecraft.class_5321> keys = new LinkedHashSet<>();
      if (enchantments != null) {
        for (net.minecraft.class_1887 ignored : enchantments) {
          keys.add(
              (net.minecraft.class_5321) StubSupport.defaultValue(net.minecraft.class_5321.class));
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
