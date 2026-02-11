package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class StatusEffectListSetting extends Setting<List<net.minecraft.class_1291>> {
    private StatusEffectListSetting(
        String name,
        String description,
        List<net.minecraft.class_1291> defaultValue,
        Consumer<List<net.minecraft.class_1291>> onChanged,
        Consumer<Setting<List<net.minecraft.class_1291>>> onModuleActivated,
        IVisible visible
    ) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected List<net.minecraft.class_1291> parseImpl(String str) {
        return value;
    }

    @Override
    protected boolean isValueValid(List<net.minecraft.class_1291> value) {
        return value != null;
    }

    @Override
    protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
        return tag;
    }

    @Override
    protected List<net.minecraft.class_1291> load(net.minecraft.class_2487 tag) {
        return value;
    }

    public static class Builder extends SettingBuilder<Builder, List<net.minecraft.class_1291>, StatusEffectListSetting> {
        public Builder() {
            super(new ArrayList<>());
        }

        public Builder defaultValue(net.minecraft.class_1291... effects) {
            this.defaultValue = new ArrayList<>(Arrays.asList(effects));
            return this;
        }

        @Override
        public StatusEffectListSetting build() {
            return new StatusEffectListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
