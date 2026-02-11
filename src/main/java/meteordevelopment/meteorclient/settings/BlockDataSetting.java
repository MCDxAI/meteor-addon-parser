package meteordevelopment.meteorclient.settings;

import meteordevelopment.meteorclient.utils.misc.IGetter;

import java.util.HashMap;
import java.util.Map;

public class BlockDataSetting<T> extends Setting<Map<net.minecraft.class_2248, T>> {
    private final IGetter<T> defaultData;

    private BlockDataSetting(String name, String description, Map<net.minecraft.class_2248, T> defaultValue,
                             java.util.function.Consumer<Map<net.minecraft.class_2248, T>> onChanged,
                             java.util.function.Consumer<Setting<Map<net.minecraft.class_2248, T>>> onModuleActivated,
                             IVisible visible,
                             IGetter<T> defaultData) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
        this.defaultData = defaultData;
    }

    @Override
    protected Map<net.minecraft.class_2248, T> parseImpl(String str) {
        return value;
    }

    @Override
    protected boolean isValueValid(Map<net.minecraft.class_2248, T> value) {
        return value != null;
    }

    @Override
    protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
        return tag;
    }

    @Override
    protected Map<net.minecraft.class_2248, T> load(net.minecraft.class_2487 tag) {
        return value;
    }

    public static class Builder<T> extends Setting.SettingBuilder<Builder<T>, Map<net.minecraft.class_2248, T>, BlockDataSetting<T>> {
        private IGetter<T> defaultData;

        public Builder() {
            super(new HashMap<>());
        }

        public Builder<T> defaultData(IGetter<T> getter) {
            this.defaultData = getter;
            return this;
        }

        @Override
        public BlockDataSetting<T> build() {
            return new BlockDataSetting<>(name, description, defaultValue, onChanged, onModuleActivated, visible, defaultData);
        }
    }
}
