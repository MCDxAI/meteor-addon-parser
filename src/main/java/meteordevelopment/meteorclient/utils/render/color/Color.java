package meteordevelopment.meteorclient.utils.render.color;

public class Color {
    public static final Color WHITE = new Color(255, 255, 255, 255);
    public static final Color LIGHT_GRAY = new Color(200, 200, 200, 255);
    public static final Color GRAY = new Color(128, 128, 128, 255);
    public static final Color RED = new Color(255, 0, 0, 255);
    public static final Color GREEN = new Color(0, 255, 0, 255);
    public static final Color CYAN = new Color(0, 255, 255, 255);
    public static final Color MAGENTA = new Color(255, 0, 255, 255);

    public int r;
    public int g;
    public int b;
    public int a;

    public Color() {
        this(255, 255, 255, 255);
    }

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(Color color) {
        this(color.r, color.g, color.b, color.a);
    }

    public int getPacked() {
        return ((a & 0xff) << 24)
            | ((r & 0xff) << 16)
            | ((g & 0xff) << 8)
            | (b & 0xff);
    }
}
