package net.minecraft.core.registries;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("all")
public final class Registries {
  public static final ResourceKey<Object> BLOCK = key("block");
  public static final ResourceKey<Object> BLOCK_ENTITY_TYPE = key("block_entity_type");
  public static final ResourceKey<Object> CREATIVE_MODE_TAB = key("creative_mode_tab");
  public static final ResourceKey<Object> ENCHANTMENT = key("enchantment");
  public static final ResourceKey<Object> ENTITY_TYPE = key("entity_type");
  public static final ResourceKey<Object> ITEM = key("item");
  public static final ResourceKey<Object> MENU = key("menu");
  public static final ResourceKey<Object> MOB_EFFECT = key("mob_effect");
  public static final ResourceKey<Object> PLACED_FEATURE = key("placed_feature");
  public static final ResourceKey<Object> WORLD_PRESET = key("world_preset");

  private Registries() {}

  private static ResourceKey<Object> key(String path) {
    return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("minecraft", path));
  }
}
