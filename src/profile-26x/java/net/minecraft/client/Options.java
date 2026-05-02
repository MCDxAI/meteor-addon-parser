package net.minecraft.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.HumanoidArm;

@SuppressWarnings("all")
public class Options {
  public final KeyMapping keyPlayerList = new KeyMapping();
  public final KeyMapping keyUse = new KeyMapping();
  private final OptionInstance<HumanoidArm> mainHand = new OptionInstance<>(HumanoidArm.RIGHT);

  public OptionInstance<HumanoidArm> mainHand() {
    return mainHand;
  }

  public net.minecraft.client.multiplayer.ClientInformation buildPlayerInformation() {
    return new net.minecraft.client.multiplayer.ClientInformation();
  }
}
