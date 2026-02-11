package meteordevelopment.orbit;

public interface IEventBus {
  void subscribe(Object listener);

  default void subscribe(Class<?> listenerClass) {
    subscribe((Object) listenerClass);
  }

  void unsubscribe(Object listener);

  default void unsubscribe(Class<?> listenerClass) {
    unsubscribe((Object) listenerClass);
  }

  <T> T post(T event);
}
