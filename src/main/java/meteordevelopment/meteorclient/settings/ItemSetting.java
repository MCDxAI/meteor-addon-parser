package meteordevelopment.meteorclient.settings;

public class ItemSetting extends Setting<net.minecraft.class_1792> {
    private java.util.function.Predicate<net.minecraft.class_1792> filter;

    private ItemSetting(String name, String description, net.minecraft.class_1792 defaultValue,
                        java.util.function.Consumer<net.minecraft.class_1792> onChanged,
                        java.util.function.Consumer<Setting<net.minecraft.class_1792>> onModuleActivated,
                        IVisible visible,
                        java.util.function.Predicate<net.minecraft.class_1792> filter) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
        this.filter = filter;
    }

    @Override
    protected net.minecraft.class_1792 parseImpl(String str) {
        return value;
    }

    @Override
    protected boolean isValueValid(net.minecraft.class_1792 value) {
        return value != null && (filter == null || filter.test(value));
    }

    @Override
    protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
        return tag;
    }

    @Override
    protected net.minecraft.class_1792 load(net.minecraft.class_2487 tag) {
        return value;
    }

    public static class Builder extends Setting.SettingBuilder<Builder, net.minecraft.class_1792, ItemSetting> {
        private java.util.function.Predicate<net.minecraft.class_1792> filter;

        public Builder() {
            super(new net.minecraft.class_1792());
        }

        public Builder filter(java.util.function.Predicate<net.minecraft.class_1792> filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public ItemSetting build() {
            return new ItemSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
        }
    }
}
