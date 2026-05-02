package meteordevelopment.meteorclient.gui.tabs;

import meteordevelopment.meteorclient.gui.GuiTheme;
import net.minecraft.client.gui.screens.Screen;

@SuppressWarnings("all")
public abstract class Tab {
  public final String name;

  public Tab(String name) {
    this.name = name;
  }

  public void openScreen(GuiTheme theme) {}

  public abstract TabScreen createScreen(GuiTheme theme);

  public abstract boolean isScreen(Screen screen);
}
