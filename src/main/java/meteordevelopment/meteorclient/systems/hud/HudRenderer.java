package meteordevelopment.meteorclient.systems.hud;

import meteordevelopment.meteorclient.utils.render.color.Color;

public class HudRenderer {
    public double textWidth(String text, boolean shadow) {
        return text == null ? 0 : text.length() * 6.0;
    }

    public double textHeight(boolean shadow) {
        return 8.0;
    }

    public double text(String text, double x, double y, Color color, boolean shadow) {
        return textWidth(text, shadow);
    }

    public void quad(double x, double y, double w, double h, double r, Color color) {
    }
}
