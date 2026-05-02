package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;

public class StringSetting extends Setting<String> {
  public String placeholder;
  public Class<?> renderer;
  public boolean wide;
  public meteordevelopment.meteorclient.gui.utils.CharFilter filter;

  private StringSetting(
      String name,
      String description,
      String defaultValue,
      Consumer<String> onChanged,
      Consumer<Setting<String>> onModuleActivated,
      IVisible visible,
      String placeholder,
      Class<?> renderer,
      boolean wide,
      meteordevelopment.meteorclient.gui.utils.CharFilter filter) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.placeholder = placeholder;
    this.renderer = renderer;
    this.wide = wide;
    this.filter = filter;
  }

  @Override
  protected String parseImpl(String str) {
    return str;
  }

  @Override
  protected boolean isValueValid(String value) {
    return value != null;
  }

  @Override
  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  @Override
  protected String load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder extends Setting.SettingBuilder<Builder, String, StringSetting> {
    private String placeholder;
    private Class<?> renderer;
    private boolean wide;
    private meteordevelopment.meteorclient.gui.utils.CharFilter filter;

    public Builder() {
      super("");
    }

    public Builder placeholder(String placeholder) {
      this.placeholder = placeholder;
      return this;
    }

    public Builder renderer(Class<?> renderer) {
      this.renderer = renderer;
      return this;
    }

    public Builder wide() {
      this.wide = true;
      return this;
    }

    public Builder filter(meteordevelopment.meteorclient.gui.utils.CharFilter filter) {
      this.filter = filter;
      return this;
    }

    @Override
    public StringSetting build() {
      return new StringSetting(
          name,
          description,
          defaultValue,
          onChanged,
          onModuleActivated,
          visible,
          placeholder,
          renderer,
          wide,
          filter);
    }
  }
}
