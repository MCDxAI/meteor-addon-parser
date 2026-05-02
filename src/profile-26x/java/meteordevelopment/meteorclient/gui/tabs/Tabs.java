package meteordevelopment.meteorclient.gui.tabs;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public final class Tabs {
  private static final List<Tab> TABS = new ArrayList<>();

  private Tabs() {}

  public static void add(Tab tab) {
    if (tab != null) TABS.add(tab);
  }

  public static List<Tab> get() {
    return TABS;
  }

  public static Tab get(Class<? extends Tab> klass) {
    for (Tab tab : TABS) {
      if (klass.isInstance(tab)) return tab;
    }
    return null;
  }
}
