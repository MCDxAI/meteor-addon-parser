package meteordevelopment.meteorclient.systems.hud;

public class HudElement {
  public final HudElementInfo<?> info;
  public final meteordevelopment.meteorclient.settings.Settings settings =
      new meteordevelopment.meteorclient.settings.Settings();

  private boolean active = true;
  private int x;
  private int y;
  private int width;
  private int height;

  public HudElement(HudElementInfo<?> info) {
    this.info = info;
  }

  public void render(HudRenderer renderer) {}

  public boolean isActive() {
    return active;
  }

  public void toggle() {
    active = !active;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
