package meteordevelopment.meteorclient.utils.render.color;

public class SettingColor extends Color {
    public boolean rainbow;

    public SettingColor(int r, int g, int b) {
        super(r, g, b);
    }

    public SettingColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public SettingColor(int r, int g, int b, int a, boolean rainbow) {
        super(r, g, b, a);
        this.rainbow = rainbow;
    }

    public SettingColor(int r, int g, int b, boolean rainbow) {
        super(r, g, b);
        this.rainbow = rainbow;
    }

    public SettingColor(SettingColor other) {
        super(other);
        this.rainbow = other.rainbow;
    }

    public SettingColor set(Color other) {
        if (other == null) return this;
        this.r = other.r;
        this.g = other.g;
        this.b = other.b;
        this.a = other.a;
        return this;
    }

    public void validate() {
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        a = Math.max(0, Math.min(255, a));
    }

    public void update() {
    }

    public net.minecraft.class_2487 toTag() {
        return new net.minecraft.class_2487();
    }

    public SettingColor fromTag(net.minecraft.class_2487 tag) {
        return this;
    }
}
