package org.meteordev.starscript.value;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import org.meteordev.starscript.utils.SFunction;

public class ValueMap {
  public Supplier<?> get(String key) {
    return () -> Value.null_();
  }

  public Set<String> keys() {
    return Collections.emptySet();
  }

  public ValueMap set(String key, Supplier<?> value) {
    return this;
  }

  public ValueMap set(String key, SFunction value) {
    return this;
  }

  public ValueMap set(String key, ValueMap value) {
    return this;
  }

  public ValueMap set(String key, Value value) {
    return this;
  }
}
