package meteordevelopment.meteorclient.systems;

import meteordevelopment.meteorclient.systems.hud.Hud;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Systems {
    private static final Map<Class<?>, System<?>> SYSTEMS = new HashMap<>();

    private Systems() {
    }

    public static <T extends System<?>> T add(T system) {
        if (system != null) {
            SYSTEMS.put(system.getClass(), system);
            system.init();
        }
        return system;
    }

    @SuppressWarnings("unchecked")
    public static <T extends System<?>> T get(Class<T> klass) {
        if (klass == Hud.class) return (T) Hud.get();
        return (T) SYSTEMS.get(klass);
    }

    public static Collection<System<?>> all() {
        return SYSTEMS.values();
    }

    public static void reset() {
        SYSTEMS.clear();
    }
}
