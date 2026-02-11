package meteordevelopment.meteorclient.systems.hud.elements;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;

public class TextHud extends HudElement {
    public final Setting<String> text = settings.getDefaultGroup().add(new StringSetting.Builder().name("text").defaultValue("").build());
    public final Setting<Integer> updateDelay = settings.getDefaultGroup().add(new IntSetting.Builder().name("update-delay").defaultValue(1).build());

    public TextHud(HudElementInfo<?> info) {
        super(info);
    }
}
