package net.fabricmc.loader.api;

@SuppressWarnings("all")
public class EntrypointContainer<T> {
  public T getEntrypoint() {
    return null;
  }

  public ModContainer getProvider() {
    return new ModContainer();
  }
}
