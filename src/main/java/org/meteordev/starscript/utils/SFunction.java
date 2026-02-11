package org.meteordev.starscript.utils;

@FunctionalInterface
public interface SFunction {
  org.meteordev.starscript.value.Value run(
      org.meteordev.starscript.Starscript starscript, int argCount);
}
