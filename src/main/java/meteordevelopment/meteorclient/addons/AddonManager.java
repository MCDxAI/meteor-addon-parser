package meteordevelopment.meteorclient.addons;

import java.util.ArrayList;
import java.util.List;

public final class AddonManager {
  public static final List<MeteorAddon> ADDONS = new ArrayList<>();

  private AddonManager() {}

  public static void reset() {
    ADDONS.clear();
  }
}
