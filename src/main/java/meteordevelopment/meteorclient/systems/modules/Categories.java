package meteordevelopment.meteorclient.systems.modules;

import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.MeteorAddon;

public final class Categories {
  public static final Category Combat = new Category("Combat");
  public static final Category Player = new Category("Player");
  public static final Category Movement = new Category("Movement");
  public static final Category Render = new Category("Render");
  public static final Category World = new Category("World");
  public static final Category Misc = new Category("Misc");

  public static boolean REGISTERING;

  private Categories() {}

  public static void init() {
    REGISTERING = true;

    Modules.registerCategory(Combat);
    Modules.registerCategory(Player);
    Modules.registerCategory(Movement);
    Modules.registerCategory(Render);
    Modules.registerCategory(World);
    Modules.registerCategory(Misc);

    for (MeteorAddon addon : AddonManager.ADDONS) {
      addon.onRegisterCategories();
    }

    REGISTERING = false;
  }
}
