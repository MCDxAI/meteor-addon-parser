package meteordevelopment.meteorclient;

import java.io.File;
import meteordevelopment.orbit.IEventBus;
import meteordevelopment.orbit.SimpleEventBus;

public final class MeteorClient {
  public static final IEventBus EVENT_BUS = new SimpleEventBus();
  public static volatile File FOLDER = resolveInitialFolder();

  public static net.minecraft.client.Minecraft mc = new net.minecraft.client.Minecraft();

  private MeteorClient() {}

  public static void setFolder(File folder) {
    FOLDER = folder == null ? new File(".") : folder;
  }

  private static File resolveInitialFolder() {
    String configured = System.getProperty("addonparser.meteorFolder");
    if (configured != null && !configured.isBlank()) return new File(configured);
    return new File(".");
  }
}
