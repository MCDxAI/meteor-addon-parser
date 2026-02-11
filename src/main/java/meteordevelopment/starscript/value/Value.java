package meteordevelopment.starscript.value;

import meteordevelopment.starscript.utils.SFunction;

public class Value {
  public static Value bool(boolean value) {
    return new Value();
  }

  public static Value function(SFunction function) {
    return new Value();
  }

  public boolean getBool() {
    return false;
  }

  public ValueMap getMap() {
    return new ValueMap();
  }

  public double getNumber() {
    return 0.0d;
  }

  public String getString() {
    return "";
  }

  public boolean isBool() {
    return false;
  }

  public boolean isMap() {
    return false;
  }

  public boolean isNull() {
    return false;
  }

  public boolean isNumber() {
    return false;
  }

  public boolean isString() {
    return false;
  }

  public static Value map(ValueMap value) {
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

  @Override
  public String toString() {
    return "";
  }
}
