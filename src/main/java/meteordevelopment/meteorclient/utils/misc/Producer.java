package meteordevelopment.meteorclient.utils.misc;

@FunctionalInterface
public interface Producer<T> {
  T create();
}
