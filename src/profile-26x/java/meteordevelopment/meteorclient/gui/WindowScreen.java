package meteordevelopment.meteorclient.gui;

import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import net.minecraft.client.gui.screens.Screen;

@SuppressWarnings("all")
public class WindowScreen extends Screen {
  public WindowScreen() {}

  public WindowScreen(GuiTheme theme, String title) {}

  public Cell add(WWidget widget) {
    return new Cell();
  }

  public void init() {}

  public void onClose() {}

  public void tick() {}
}
