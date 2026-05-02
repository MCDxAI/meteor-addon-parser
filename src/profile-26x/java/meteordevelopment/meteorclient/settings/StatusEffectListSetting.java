package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public class StatusEffectListSetting extends Setting<List<Holder<MobEffect>>> {
  private StatusEffectListSetting(
      String name,
      String description,
      List<Holder<MobEffect>> defaultValue,
      Consumer<List<Holder<MobEffect>>> onChanged,
      Consumer<Setting<List<Holder<MobEffect>>>> onModuleActivated,
      IVisible visible) {
    super(name, description, defaultValue, onChanged, onModuleActivated, visible);
  }

  @Override
  protected List<Holder<MobEffect>> parseImpl(String str) {
    return value;
  }

  @Override
  protected boolean isValueValid(List<Holder<MobEffect>> value) {
    return value != null;
  }

  @Override
  protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
    return tag;
  }

  @Override
  protected List<Holder<MobEffect>> load(net.minecraft.class_2487 tag) {
    return value;
  }

  public static class Builder
      extends SettingBuilder<Builder, List<Holder<MobEffect>>, StatusEffectListSetting> {
    public Builder() {
      super(new ArrayList<>());
    }

    @SafeVarargs
    public final Builder defaultValue(Holder<MobEffect>... effects) {
      this.defaultValue = new ArrayList<>(Arrays.asList(effects));
      return this;
    }

    @Override
    public StatusEffectListSetting build() {
      return new StatusEffectListSetting(
          name, description, defaultValue, onChanged, onModuleActivated, visible);
    }
  }
}
