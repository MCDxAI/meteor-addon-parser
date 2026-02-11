package meteordevelopment.starscript;

import meteordevelopment.starscript.value.Value;
import meteordevelopment.starscript.value.ValueMap;

public class Starscript {
    public Value pop() {
        return Value.null_();
    }

    public ValueMap set(String key, Value value) {
        return new ValueMap();
    }

    public ValueMap set(String key, ValueMap value) {
        return new ValueMap();
    }

    public void error(String format, Object[] args) {
    }

    public double popNumber(String key) {
        return 0.0d;
    }

    public String popString(String key) {
        return "";
    }
}
