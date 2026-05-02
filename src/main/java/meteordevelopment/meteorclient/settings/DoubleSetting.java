package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;

public class DoubleSetting extends Setting<Double> {
  public double min;
  public double max;
  public double sliderMin;
  public double sliderMax;
  public boolean noSlider;
  public int decimalPlaces = 3;

  private DoubleSetting(
      String name,
      String description,
      Double defaultValue,
      Consumer<Double> onChanged,
      Consumer<Setting<Double>> onModuleActivated,
      IVisible visible,
      double min,
      double max,
      double sliderMin,
      double sliderMax,
      boolean noSlider) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    this.min = min;
    this.max = max;
    this.sliderMin = sliderMin;
    this.sliderMax = sliderMax;
    this.noSlider = noSlider;
  }

  @Override
  protected Double parseImpl(String str) {
    try {
      return Double.parseDouble(str);
    } catch (NumberFormatException e) {
      // Invalid double string - return null to indicate parse failure
      return null;
    }
  }

  @Override
  protected boolean isValueValid(Double value) {
    return value != null && value >= min && value <= max;
  }

  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  protected Double load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder extends Setting.SettingBuilder<Builder, Double, DoubleSetting> {
    private double min = -Double.MAX_VALUE;
    private double max = Double.MAX_VALUE;
    private double sliderMin;
    private double sliderMax;
    private boolean noSlider;

    public Builder() {
      super(0.0d);
    }

    public Builder defaultValue(double value) {
      this.defaultValue = value;
      return this;
    }

    public Builder min(double min) {
      this.min = min;
      return this;
    }

    public Builder max(double max) {
      this.max = max;
      return this;
    }

    public Builder range(double min, double max) {
      this.min = min;
      this.max = max;
      return this;
    }

    public Builder sliderMin(double sliderMin) {
      this.sliderMin = sliderMin;
      return this;
    }

    public Builder sliderMax(double sliderMax) {
      this.sliderMax = sliderMax;
      return this;
    }

    public Builder sliderRange(double sliderMin, double sliderMax) {
      this.sliderMin = sliderMin;
      this.sliderMax = sliderMax;
      return this;
    }

    public Builder noSlider() {
      this.noSlider = true;
      return this;
    }

    @Override
    public DoubleSetting build() {
      double actualSliderMin = sliderMin == 0.0d ? min : sliderMin;
      double actualSliderMax = sliderMax == 0.0d ? max : sliderMax;
      return new DoubleSetting(
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
