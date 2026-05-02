package meteordevelopment.meteorclient.utils.misc;

import org.meteordev.starscript.Script;
import org.meteordev.starscript.Starscript;
import org.meteordev.starscript.value.Value;

/**
 * 26x stub for Meteor's starscript helper. Real Meteor builds compile starscript scripts and wraps
 * Minecraft values; the parser does not exercise this path, so we expose minimal no-op
 * signatures.
 */
public final class MeteorStarscript {
  public static Starscript ss = new Starscript();

  private MeteorStarscript() {}

  public static Script compile(String script) {
    return new Script();
  }

  public static String run(Script script) {
    return "";
  }

  public static Value wrap(net.minecraft.world.effect.MobEffectInstance value) {
    return Value.null_();
  }

  public static Value wrap(net.minecraft.world.item.ItemStack value) {
    return Value.null_();
  }

  public static Value wrap(HorizontalDirection value) {
    return Value.null_();
  }

  public static net.minecraft.resources.Identifier popIdentifier(Starscript starscript, String key) {
    return null;
  }
}
