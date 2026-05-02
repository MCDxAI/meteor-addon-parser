package meteordevelopment.meteorclient.gui.tabs;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;

@SuppressWarnings("all")
public abstract class TabScreen extends WidgetScreen {
  public final Tab tab;

  public TabScreen(GuiTheme theme, Tab tab) {
    super(theme, tab == null ? "" : tab.name);
    this.tab = tab;
  }

  public Cell addDirect(WWidget widget) {
    return add(widget);
  }
}
