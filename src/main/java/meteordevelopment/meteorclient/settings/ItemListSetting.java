package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemListSetting extends Setting<List<net.minecraft.class_1792>> {
    private java.util.function.Predicate<net.minecraft.class_1792> filter;

    private ItemListSetting(String name, String description, List<net.minecraft.class_1792> defaultValue,
                            java.util.function.Consumer<List<net.minecraft.class_1792>> onChanged,
                            java.util.function.Consumer<Setting<List<net.minecraft.class_1792>>> onModuleActivated,
                            IVisible visible,
                            java.util.function.Predicate<net.minecraft.class_1792> filter) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
        this.filter = filter;
    }

    @Override
    protected List<net.minecraft.class_1792> parseImpl(String str) {
        return value;
    }

    @Override
    protected boolean isValueValid(List<net.minecraft.class_1792> value) {
        return value != null;
    }

    @Override
    protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
        return tag;
    }

    @Override
    protected List<net.minecraft.class_1792> load(net.minecraft.class_2487 tag) {
        return value;
    }

    public static class Builder extends Setting.SettingBuilder<Builder, List<net.minecraft.class_1792>, ItemListSetting> {
        private java.util.function.Predicate<net.minecraft.class_1792> filter;

        public Builder() {
            super(new ArrayList<>());
        }

        public Builder defaultValue(net.minecraft.class_1792... items) {
            this.defaultValue = new ArrayList<>(Arrays.asList(items));
            return this;
        }

        public Builder filter(java.util.function.Predicate<net.minecraft.class_1792> filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public ItemListSetting build() {
            return new ItemListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
        }
    }
}
