package meteordevelopment.meteorclient.systems.config;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.Settings;

public final class Config {
  private static final Config INSTANCE = new Config();

  public final Settings settings = new Settings();

  // Common Meteor config toggles referenced by addon code paths.
  public final Setting<Boolean> chatFeedback =
      settings
          .getDefaultGroup()
          .add(new BoolSetting.Builder().name("chat-feedback").defaultValue(false).build());

  public final Setting<Boolean> moduleAliases =
      settings
          .getDefaultGroup()
          .add(new BoolSetting.Builder().name("module-aliases").defaultValue(true).build());

  private Config() {}

  public static Config get() {
    return INSTANCE;
  }
}
