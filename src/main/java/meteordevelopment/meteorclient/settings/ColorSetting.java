package meteordevelopment.meteorclient.settings;

import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class ColorSetting extends Setting<SettingColor> {
    private ColorSetting(String name, String description, SettingColor defaultValue,
                         java.util.function.Consumer<SettingColor> onChanged,
                         java.util.function.Consumer<Setting<SettingColor>> onModuleActivated,
                         IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected SettingColor parseImpl(String str) {
        return value;
    }

    @Override
    protected boolean isValueValid(SettingColor value) {
        return value != null;
    }

    @Override
    protected net.minecraft.class_2487 save(net.minecraft.class_2487 tag) {
        return tag;
    }

    @Override
    protected SettingColor load(net.minecraft.class_2487 tag) {
        return value;
    }

    public static class Builder extends Setting.SettingBuilder<Builder, SettingColor, ColorSetting> {
        public Builder() {
            super(new SettingColor(255, 255, 255, 255));
        }

        public Builder defaultValue(Color color) {
            this.defaultValue = new SettingColor(color.r, color.g, color.b, color.a);
            return this;
        }

        public Builder defaultValue(SettingColor color) {
            this.defaultValue = color;
            return this;
        }

        @Override
        public ColorSetting build() {
            return new ColorSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
