package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;

public class BlockSetting extends Setting<net.minecraft.class_2248> {
  private java.util.function.Predicate<net.minecraft.class_2248> filter;

  private BlockSetting(
      String name,
      String description,
      net.minecraft.class_2248 defaultValue,
      Consumer<net.minecraft.class_2248> onChanged,
      Consumer<Setting<net.minecraft.class_2248>> onModuleActivated,
      IVisible visible,
      java.util.function.Predicate<net.minecraft.class_2248> filter) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.filter = filter;
  }

  @Override
  protected net.minecraft.class_2248 parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(net.minecraft.class_2248 value) {
    return value != null && (filter == null || filter.test(value));
  }

  @Override
  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  @Override
  protected net.minecraft.class_2248 load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder
      extends SettingBuilder<Builder, net.minecraft.class_2248, BlockSetting> {
    private java.util.function.Predicate<net.minecraft.class_2248> filter;

    public Builder() {
      super(null);
    }

    public Builder defaultValue(net.minecraft.class_2248 block) {
      this.defaultValue = block;
      return this;
    }

    public Builder filter(java.util.function.Predicate<net.minecraft.class_2248> filter) {
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
