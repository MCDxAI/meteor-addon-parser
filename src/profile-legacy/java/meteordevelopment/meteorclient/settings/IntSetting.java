package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;

public class IntSetting extends Setting<Integer> {
  public int min;
  public int max;
  public int sliderMin;
  public int sliderMax;
  public boolean noSlider;

  private IntSetting(
      String name,
      String description,
      Integer defaultValue,
      Consumer<Integer> onChanged,
      Consumer<Setting<Integer>> onModuleActivated,
      IVisible visible,
      int min,
      int max,
      int sliderMin,
      int sliderMax,
      boolean noSlider) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.min = min;
    this.max = max;
    this.sliderMin = sliderMin;
    this.sliderMax = sliderMax;
    this.noSlider = noSlider;
  }

  @Override
  protected Integer parseImpl(String str) {
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException e) {
      // Invalid integer string - return null to indicate parse failure
      return null;
    }
  }

  @Override
  protected boolean isValueValid(Integer value) {
    return value != null && value >= min && value <= max;
  }

  @Override
  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  @Override
  protected Integer load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder extends Setting.SettingBuilder<Builder, Integer, IntSetting> {
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;
    private int sliderMin = 0;
    private int sliderMax = 0;
    private boolean noSlider;

    public Builder() {
      super(0);
    }

    public Builder min(int min) {
      this.min = min;
      return this;
    }

    public Builder max(int max) {
      this.max = max;
      return this;
    }

    public Builder range(int min, int max) {
      this.min = min;
      this.max = max;
      return this;
    }

    public Builder sliderMax(int sliderMax) {
      this.sliderMax = sliderMax;
      return this;
    }

    public Builder sliderMin(int sliderMin) {
      this.sliderMin = sliderMin;
      return this;
    }

    public Builder sliderRange(int sliderMin, int sliderMax) {
      this.sliderMin = sliderMin;
      this.sliderMax = sliderMax;
      return this;
    }

    public Builder noSlider() {
      this.noSlider = true;
      return this;
    }

    @Override
    public IntSetting build() {
      int actualSliderMin = sliderMin == 0 ? min : sliderMin;
      int actualSliderMax = sliderMax == 0 ? max : sliderMax;
      return new IntSetting(
          name,
          description,
          defaultValue,
          onChanged,
          onModuleActivated,
          visible,
          min,
          max,
          actualSliderMin,
          actualSliderMax,
          noSlider);
    }
  }
}
