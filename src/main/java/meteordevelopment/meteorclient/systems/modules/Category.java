package meteordevelopment.meteorclient.systems.modules;

public class Category {
    public final String name;
    public final net.minecraft.class_1799 icon;
    private final int nameHash;

    public Category(String name, net.minecraft.class_1799 icon) {
        this.name = name;
        this.icon = icon;
        this.nameHash = name == null ? 0 : name.hashCode();
    }

    public Category(String name) {
        this(name, null);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return this.nameHash == category.nameHash;
    }

    @Override
    public int hashCode() {
        return nameHash;
    }
}
