package com.cope.addonparser.util;

import java.util.List;
import java.util.Map;

/**
 * 26x (Mojmap) profile bindings for ValueNormalizer. Mojmap is unobfuscated, so the resolver is
 * an identity no-op. Owner hints map registry holders to their Items/Blocks/etc. registry class so
 * we can recover symbolic names like {@code Items.DIAMOND}.
 */
public final class MappingResolverFactory {
  private static final MappingResolver IDENTITY =
      new MappingResolver() {
        @Override
        public String mapClass(String className) {
          return null;
        }

        @Override
        public String mapSymbol(String ownerClass, String fieldName) {
          return null;
        }

        @Override
        public String mapField(String ownerClass, String fieldName) {
          return null;
        }
      };

  private static final Map<String, List<String>> OWNER_HINTS =
      Map.of(
          "net.minecraft.world.item.Item", List.of("net.minecraft.world.item.Items"),
          "net.minecraft.world.level.block.Block", List.of("net.minecraft.world.level.block.Blocks"),
          "net.minecraft.sounds.SoundEvent", List.of("net.minecraft.sounds.SoundEvents"),
          "net.minecraft.world.item.enchantment.Enchantment",
              List.of("net.minecraft.world.item.enchantment.Enchantments"),
          "net.minecraft.world.effect.MobEffect",
              List.of("net.minecraft.world.effect.MobEffects"),
          "net.minecraft.core.Holder",
              List.of("net.minecraft.world.effect.MobEffects", "net.minecraft.world.item.Items"));

  private MappingResolverFactory() {}

  public static MappingResolver get() {
    return IDENTITY;
  }

  public static Map<String, List<String>> ownerHints() {
    return OWNER_HINTS;
  }

  public static String fallbackOwnerForSimpleName(String simpleName) {
    return switch (simpleName) {
      case "Item" -> "net.minecraft.world.item.Items";
      case "Block" -> "net.minecraft.world.level.block.Blocks";
      case "MobEffect" -> "net.minecraft.world.effect.MobEffects";
      default -> null;
    };
  }
}
