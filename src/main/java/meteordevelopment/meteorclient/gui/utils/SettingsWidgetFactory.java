package meteordevelopment.meteorclient.gui.utils;

import java.util.function.Function;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;

@SuppressWarnings("all")
public final class SettingsWidgetFactory {
  private SettingsWidgetFactory() {}

  public static <T> void registerCustomFactory(Class<T> klass, Function<Setting<T>, WWidget> factory) {}

  @FunctionalInterface
  public interface Factory<T> {
    WWidget create(GuiTheme theme, Setting<T> setting);
  }
}
