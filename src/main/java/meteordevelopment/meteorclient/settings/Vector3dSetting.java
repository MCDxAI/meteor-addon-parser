package meteordevelopment.meteorclient.settings;

public class Vector3dSetting extends Setting<org.joml.Vector3d> {
  private Vector3dSetting(
      String name,
      String description,
      org.joml.Vector3d defaultValue,
      java.util.function.Consumer<org.joml.Vector3d> onChanged,
      java.util.function.Consumer<Setting<org.joml.Vector3d>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected org.joml.Vector3d parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(org.joml.Vector3d value) {
    return value != null;
  }

  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  protected org.joml.Vector3d load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder
      extends Setting.SettingBuilder<Builder, org.joml.Vector3d, Vector3dSetting> {
    public Builder() {
      super(new org.joml.Vector3d());
    }

    public Builder defaultValue(org.joml.Vector3d value) {
      this.defaultValue = value;
      return this;
    }

    @Override
    public Vector3dSetting build() {
      return new Vector3dSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
