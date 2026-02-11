package meteordevelopment.meteorclient.utils.misc;

public class Keybind implements ISerializable<Keybind> {
    private boolean isKey;
    private int value;
    private int modifiers;

    public static Keybind none() {
        return new Keybind();
    }

    public static Keybind fromKey(int key) {
        Keybind bind = new Keybind();
        bind.isKey = true;
        bind.value = key;
        return bind;
    }

    public static Keybind fromKeys(int key, int modifiers) {
        Keybind bind = new Keybind();
        bind.isKey = true;
        bind.value = key;
        bind.modifiers = modifiers;
        return bind;
    }

    public boolean matches(boolean isKey, int value, int modifiers) {
        return this.isKey == isKey && this.value == value && this.modifiers == modifiers;
    }

    public boolean isPressed() {
        return false;
    }

    public boolean canBindTo(boolean isKey, int value, int modifiers) {
        return true;
    }

    public void set(boolean isKey, int value, int modifiers) {
        this.isKey = isKey;
        this.value = value;
        this.modifiers = modifiers;
    }

    public Keybind set(Keybind other) {
        if (other == null) return this;
        this.isKey = other.isKey;
        this.value = other.value;
        this.modifiers = other.modifiers;
        return this;
    }

    public Keybind copy() {
        Keybind bind = new Keybind();
        bind.set(this);
        return bind;
    }

    @Override
    public net.minecraft.class_2487 toTag() {
        return new net.minecraft.class_2487();
    }

    @Override
    public Keybind fromTag(net.minecraft.class_2487 tag) {
        return this;
    }

    @Override
    public String toString() {
        if (value == 0) return "None";
        return (isKey ? "Key:" : "Mouse:") + value;
    }
}
