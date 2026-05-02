package net.minecraft.client;

@SuppressWarnings("all")
public class Minecraft {
  public final Options options = new Options();

  public static Minecraft getInstance() {
    return new Minecraft();
  }

  public Minecraft() {}

  public void setScreen(net.minecraft.client.gui.screens.Screen screen) {}
}
