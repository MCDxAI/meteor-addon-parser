package meteordevelopment.meteorclient.gui.utils;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;

@FunctionalInterface
public interface IScreenFactory {
    WidgetScreen createScreen(GuiTheme theme);
}
