package net.minecraft.client;

@SuppressWarnings("all")
public class OptionInstance<T> {
  private T value;

  public OptionInstance(T value) {
    this.value = value;
  }

  public T get() {
    return value;
  }

  public void set(T value) {
    this.value = value;
  }
}
