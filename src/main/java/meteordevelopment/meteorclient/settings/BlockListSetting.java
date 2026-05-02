package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockListSetting extends Setting<List<net.minecraft.class_2248>> {
  private java.util.function.Predicate<net.minecraft.class_2248> filter;

  private BlockListSetting(
      String name,
      String description,
      List<net.minecraft.class_2248> defaultValue,
      java.util.function.Consumer<List<net.minecraft.class_2248>> onChanged,
      java.util.function.Consumer<Setting<List<net.minecraft.class_2248>>> onModuleActivated,
      IVisible visible,
      java.util.function.Predicate<net.minecraft.class_2248> filter) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.filter = filter;
  }

  @Override
  protected List<net.minecraft.class_2248> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(List<net.minecraft.class_2248> value) {
    return value != null;
  }

  @Override
  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  @Override
  protected List<net.minecraft.class_2248> load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<Builder, List<net.minecraft.class_2248>, BlockListSetting> {
    private java.util.function.Predicate<net.minecraft.class_2248> filter;

    public Builder() {
      super(new ArrayList<>());
    }

    public Builder defaultValue(net.minecraft.class_2248... blocks) {
      this.defaultValue = new ArrayList<>(Arrays.asList(blocks));
      return this;
    }

    public Builder filter(java.util.function.Predicate<net.minecraft.class_2248> filter) {
      this.filter = filter;
      return this;
    }

    public Builder defaultValue(net.minecraft.world.level.block.Block... blocks) {
      return this;
    }

    @Override
    public BlockListSetting build() {
      return new BlockListSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
    }
  }
}
