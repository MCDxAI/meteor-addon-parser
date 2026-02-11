package meteordevelopment.starscript.utils;

@FunctionalInterface
public interface SFunction {
    meteordevelopment.starscript.value.Value run(meteordevelopment.starscript.Starscript starscript, int argCount);
}
