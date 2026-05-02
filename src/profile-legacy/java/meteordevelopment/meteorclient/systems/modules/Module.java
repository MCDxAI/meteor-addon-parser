package meteordevelopment.meteorclient.systems.modules;

import com.cope.addonparser.util.NameUtils;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.render.color.Color;

public abstract class Module
    implements ISerializable<Module>, Comparable<Module>, dev.jfronny.meteoradditions.util.IModule {
  protected final net.minecraft.class_310 mc;

  public final Category category;
  public final String name;
  public final String title;
  public final String description;
  public final String[] aliases;
  public final Color color;

  public final MeteorAddon addon;
  public final Settings settings = new Settings();

  private boolean active;

  public boolean serialize = true;
  public boolean runInMainMenu = false;
  public boolean autoSubscribe = true;

  public final Keybind keybind = Keybind.none();
  public boolean toggleOnBindRelease = false;
  public boolean chatFeedback = true;
  public boolean favorite = false;

  public Module(Category category, String name, String description, String... aliases) {
    this.mc = MeteorClient.mc;
    this.category = category;
    this.name = name;
    this.title = NameUtils.nameToTitle(name);
    this.description = description;
    this.aliases = aliases == null ? new String[0] : aliases;
    this.color = new Color(255, 255, 255, 255);

    MeteorAddon resolved = null;
    String className = getClass().getName();
    for (MeteorAddon a : AddonManager.ADDONS) {
      String pkg = a.getPackage();
      if (pkg != null && className.startsWith(pkg)) {
        resolved = a;
        break;
      }
    }
    this.addon = resolved;
  }

  public Module(Category category, String name, String description) {
    this(category, name, description, new String[0]);
  }

  public meteordevelopment.meteorclient.gui.widgets.WWidget getWidget(
      meteordevelopment.meteorclient.gui.GuiTheme theme) {
    return null;
  }

  public void onActivate() {}

  public void onDeactivate() {}

  public void toggle() {
    if (!active) {
      active = true;
      Modules.get().addActive(this);
      settings.onActivated();
      if (autoSubscribe) MeteorClient.EVENT_BUS.subscribe(this);
      onActivate();
    } else {
      if (autoSubscribe) MeteorClient.EVENT_BUS.unsubscribe(this);
      onDeactivate();
      active = false;
      Modules.get().removeActive(this);
    }
  }

  public void enable() {
    if (!isActive()) toggle();
  }

  public void disable() {
    if (isActive()) toggle();
  }

  public boolean isActive() {
    return active;
  }

  public void info(net.minecraft.class_2561 message) {}

  public void info(String message, Object... args) {}

  public void warning(String message, Object... args) {}

  public void error(String message, Object... args) {}

  @Override
  public void meteorAdditions$setKeywords(String... keywords) {}

  @Override
  public String meteorAdditions$getQueryString() {
    return name == null ? "" : name;
  }

  public net.minecraft.class_2487 toTag() {
    return new net.minecraft.class_2487();
  }

  public Module fromTag(net.minecraft.class_2487 tag) {
    return this;
  }

  @Override
  public int compareTo(Module o) {
    if (o == null || o.name == null) return 1;
    if (name == null) return -1;
    return name.compareTo(o.name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Module)) return false;
    Module module = (Module) o;
    return Objects.equals(name, module.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
