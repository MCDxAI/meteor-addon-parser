package meteordevelopment.starscript.utils;

@SuppressWarnings("all")
@FunctionalInterface
public interface SFunction {
  int run(Object starscript, int argCount);
}
