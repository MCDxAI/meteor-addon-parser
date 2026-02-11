package meteordevelopment.orbit;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SimpleEventBus implements IEventBus {
    private final Set<Object> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void subscribe(Object listener) {
        if (listener != null) listeners.add(listener);
    }

    @Override
    public void unsubscribe(Object listener) {
        if (listener != null) listeners.remove(listener);
    }

    @Override
    public <T> T post(T event) {
        return event;
    }
}
