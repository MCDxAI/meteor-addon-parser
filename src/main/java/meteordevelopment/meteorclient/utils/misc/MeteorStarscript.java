package meteordevelopment.meteorclient.utils.misc;

public final class MeteorStarscript {
    public static meteordevelopment.starscript.Starscript ss = new meteordevelopment.starscript.Starscript();

    private MeteorStarscript() {
    }

    public static meteordevelopment.starscript.Script compile(String script) {
        return new meteordevelopment.starscript.Script();
    }

    public static String run(meteordevelopment.starscript.Script script) {
        return "";
    }

    public static meteordevelopment.starscript.value.Value wrap(net.minecraft.class_1293 value) {
        return meteordevelopment.starscript.value.Value.null_();
    }

    public static meteordevelopment.starscript.value.Value wrap(net.minecraft.class_1799 value) {
        return meteordevelopment.starscript.value.Value.null_();
    }

    public static meteordevelopment.starscript.value.Value wrap(meteordevelopment.meteorclient.utils.misc.HorizontalDirection value) {
        return meteordevelopment.starscript.value.Value.null_();
    }

    public static net.minecraft.class_2960 popIdentifier(meteordevelopment.starscript.Starscript starscript, String key) {
        return null;
    }
}
