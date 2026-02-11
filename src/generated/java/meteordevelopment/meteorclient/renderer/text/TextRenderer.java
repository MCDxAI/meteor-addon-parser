// AUTO-GENERATED FILE. DO NOT EDIT.
package meteordevelopment.meteorclient.renderer.text;

@SuppressWarnings({"all", "unchecked"})
public interface TextRenderer {
    default void begin(double p0) {
    }

    default void beginBig() {
    }

    default void end() {
    }

    static meteordevelopment.meteorclient.renderer.text.TextRenderer get() {
        return (meteordevelopment.meteorclient.renderer.text.TextRenderer) com.cope.addonparser.stubs.StubSupport.defaultValue(meteordevelopment.meteorclient.renderer.text.TextRenderer.class);
    }

    default double getHeight() {
        return 0.0d;
    }

    default double getHeight(boolean p0) {
        return 0.0d;
    }

    default double getWidth(java.lang.String p0) {
        return 0.0d;
    }

    default double getWidth(java.lang.String p0, boolean p1) {
        return 0.0d;
    }

    default double render(java.lang.String p0, double p1, double p2, meteordevelopment.meteorclient.utils.render.color.Color p3) {
        return 0.0d;
    }

    default double render(java.lang.String p0, double p1, double p2, meteordevelopment.meteorclient.utils.render.color.Color p3, boolean p4) {
        return 0.0d;
    }

}
