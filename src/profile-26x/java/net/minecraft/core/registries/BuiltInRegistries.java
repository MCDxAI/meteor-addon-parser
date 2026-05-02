package net.minecraft.core.registries;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("all")
public final class BuiltInRegistries {
  public static final DefaultedRegistry<Block> BLOCK = new DefaultedRegistry<>();
  public static final Registry<Object> BLOCK_ENTITY_TYPE = new Registry<>();
  public static final Registry<Object> CREATIVE_MODE_TAB = new Registry<>();
  public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = new DefaultedRegistry<>();
  public static final DefaultedRegistry<Item> ITEM = new DefaultedRegistry<>();
  public static final Registry<Object> MENU = new Registry<>();
  public static final Registry<MobEffect> MOB_EFFECT = new Registry<>();
  public static final Registry<Object> PARTICLE_TYPE = new Registry<>();
  public static final Registry<Object> POTION = new Registry<>();
  public static final Registry<Object> SOUND_EVENT = new Registry<>();

  static {
    BLOCK.add(Blocks.AIR);
    BLOCK.add(Blocks.DIRT);

    ITEM.add(Items.AIR);
    ITEM.add(Items.BOW);
    ITEM.add(Items.CROSSBOW);
    ITEM.add(Items.DIRT);
    ITEM.add(Items.EGG);
    ITEM.add(Items.ENDER_PEARL);
    ITEM.add(Items.EXPERIENCE_BOTTLE);
    ITEM.add(Items.FIREWORK_ROCKET);
    ITEM.add(Items.FISHING_ROD);
    ITEM.add(Items.SNOWBALL);
    ITEM.add(Items.SPLASH_POTION);
    ITEM.add(Items.TRIDENT);
    ITEM.add(Items.WIND_CHARGE);

    ENTITY_TYPE.add(EntityType.ARROW);
    ENTITY_TYPE.add(EntityType.DRAGON_FIREBALL);
    ENTITY_TYPE.add(EntityType.EGG);
    ENTITY_TYPE.add(EntityType.ENDER_PEARL);
    ENTITY_TYPE.add(EntityType.EXPERIENCE_BOTTLE);
    ENTITY_TYPE.add(EntityType.EYE_OF_ENDER);
    ENTITY_TYPE.add(EntityType.LLAMA_SPIT);
    ENTITY_TYPE.add(EntityType.SHULKER_BULLET);
    ENTITY_TYPE.add(EntityType.SMALL_FIREBALL);
    ENTITY_TYPE.add(EntityType.SNOWBALL);
    ENTITY_TYPE.add(EntityType.SPECTRAL_ARROW);
    ENTITY_TYPE.add(EntityType.SPLASH_POTION);
    ENTITY_TYPE.add(EntityType.TRIDENT);
    ENTITY_TYPE.add(EntityType.WIND_CHARGE);
    ENTITY_TYPE.add(EntityType.WITHER_SKULL);
  }

  private BuiltInRegistries() {}
}
