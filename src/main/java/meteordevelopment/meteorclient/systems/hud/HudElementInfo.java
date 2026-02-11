package meteordevelopment.meteorclient.systems.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HudElementInfo<T extends HudElement> {
  public final HudGroup group;
  public final String name;
  public final String title;
  public final String description;
  private final Supplier<T> supplier;
  public final List<Preset> presets = new ArrayList<>();

  public HudElementInfo(HudGroup group, String name, String description, Supplier<T> supplier) {
    this.group = group;
    this.name = name;
    this.title = name;
    this.description = description;
    this.supplier = supplier;
  }

  public T create() {
    return supplier.get();
  }

  public Preset addPreset(String title, Consumer<T> callback) {
    Preset preset = new Preset(this, title, (Consumer<HudElement>) callback);
    presets.add(preset);
    return preset;
  }

  public static class Preset {
    public final HudElementInfo<?> info;
    public final String title;
    public final Consumer<HudElement> callback;

    public Preset(HudElementInfo<?> info, String title, Consumer<HudElement> callback) {
      this.info = info;
      this.title = title;
      this.callback = callback;
    }
  }
}
