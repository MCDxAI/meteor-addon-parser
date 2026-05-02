package net.minecraft.network.chat;

@SuppressWarnings("all")
public class MutableComponent implements Component {
  private final String text;

  public MutableComponent() {
    this("");
  }

  public MutableComponent(String text) {
    this.text = text == null ? "" : text;
  }

  public MutableComponent append(Component component) {
    return this;
  }

  public MutableComponent append(String text) {
    return this;
  }

  @Override
  public String toString() {
    return text;
  }
}
