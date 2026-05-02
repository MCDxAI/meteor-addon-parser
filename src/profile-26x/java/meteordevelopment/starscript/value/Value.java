package meteordevelopment.starscript.value;

import meteordevelopment.starscript.utils.SFunction;

@SuppressWarnings("all")
public class Value extends org.meteordev.starscript.value.Value {
  public static Value bool(boolean value) {
    return new Value();
  }

  public static Value function(SFunction function) {
    return new Value();
  }

  public static Value null_() {
    return new Value();
  }

  public static Value number(double value) {
    return new Value();
  }

  public static Value string(String value) {
    return new Value();
  }
}
