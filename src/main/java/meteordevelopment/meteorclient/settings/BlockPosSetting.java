package meteordevelopment.meteorclient.settings;

public class BlockPosSetting extends Setting<net.minecraft.class_2338> {
  private BlockPosSetting(
      String name,
      String description,
      net.minecraft.class_2338 defaultValue,
      java.util.function.Consumer<net.minecraft.class_2338> onChanged,
      java.util.function.Consumer<Setting<net.minecraft.class_2338>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected net.minecraft.class_2338 parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(net.minecraft.class_2338 value) {
    return value != null;
  }

  @Override
  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  @Override
  protected net.minecraft.class_2338 load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<Builder, net.minecraft.class_2338, BlockPosSetting> {
    public Builder() {
      super(new net.minecraft.class_2338());
    }

    @Override
    public BlockPosSetting build() {
      return new BlockPosSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
