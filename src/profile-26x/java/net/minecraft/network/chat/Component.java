package net.minecraft.network.chat;

@SuppressWarnings("all")
public interface Component {
  static MutableComponent literal(String text) {
    return new MutableComponent(text);
  }

  static MutableComponent translatable(String key, Object... args) {
    return new MutableComponent(key);
  }
}
