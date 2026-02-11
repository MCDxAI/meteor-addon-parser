package meteordevelopment.meteorclient.systems;

public class System<T extends System<?>> {
    public final String name;

    public final meteordevelopment.meteorclient.settings.Settings settings = new meteordevelopment.meteorclient.settings.Settings();

    public System(String name) {
        this.name = name;
    }

    public void init() {
    }

    public void load(java.io.File folder) {
    }

    public void save() {
    }

    public void save(java.io.File folder) {
    }

    public net.minecraft.class_2487 toTag() {
        return new net.minecraft.class_2487();
    }

    @SuppressWarnings("unchecked")
    public T fromTag(net.minecraft.class_2487 tag) {
        return (T) this;
    }
}
