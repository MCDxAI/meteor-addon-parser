package meteordevelopment.meteorclient.systems.hud;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import meteordevelopment.meteorclient.systems.System;

public final class Hud extends System<Hud> implements Iterable<HudElementInfo<?>> {
  private static final Hud INSTANCE = new Hud();
  public static final HudGroup GROUP = new HudGroup("Meteor");

  public final Map<String, HudElementInfo<?>> infos = new LinkedHashMap<>();

  private Hud() {
    super("hud");
  }

  public static Hud get() {
    return INSTANCE;
  }

  public void register(HudElementInfo<?> info) {
    if (info != null) infos.put(info.name, info);
  }

  @Override
  public Iterator<HudElementInfo<?>> iterator() {
    return infos.values().iterator();
  }

  public void reset() {
    infos.clear();
  }
}
