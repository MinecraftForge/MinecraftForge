/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.references.BlockItemId;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.minecraft.references.ItemIds.*;
import static net.minecraft.references.BlockItemIds.*;
import static net.minecraftforge.common.Tags.Items.*;

@ApiStatus.Internal
public final class ForgeItemTagsProvider extends VanillaItemTagsProvider {
    public ForgeItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @SuppressWarnings({ "unchecked", "removal" })
    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        new ForgeBlockItemTagsProvider(tagId -> WrappedCombinedAppender.item(this.tag(tagId.item()))).run();
        tag(BONES).add(BONE);
        tag(Tags.Items.BRICKS)
            .addTags(
                BRICKS_NORMAL,
                BRICKS_NETHER,
                BRICKS_RESIN
            );
        tag(BRICKS_NORMAL).add(BRICK);
        tag(BRICKS_NETHER).add(NETHER_BRICK);
        tag(BRICKS_RESIN).add(RESIN_BRICK);
        tag(BUCKETS_EMPTY).add(BUCKET);
        tag(BUCKETS_WATER).add(WATER_BUCKET);
        tag(BUCKETS_LAVA).add(LAVA_BUCKET);
        tag(BUCKETS_MILK).add(MILK_BUCKET);
        tag(BUCKETS_POWDER_SNOW).add(POWDER_SNOW);
        tag(BUCKETS_ENTITY_WATER)
            .add(
                AXOLOTL_BUCKET,
                COD_BUCKET,
                PUFFERFISH_BUCKET,
                TADPOLE_BUCKET,
                TROPICAL_FISH_BUCKET,
                SALMON_BUCKET
            );
        tag(BUCKETS)
            .addTags(
                BUCKETS_EMPTY,
                BUCKETS_WATER,
                BUCKETS_LAVA,
                BUCKETS_MILK,
                BUCKETS_POWDER_SNOW,
                BUCKETS_ENTITY_WATER
            );
        tag(CLUMPS)
            .addTag(CLUMPS_RESIN);
        tag(CLUMPS_RESIN)
            .add(RESIN_CLUMP);
        tag(CONCRETE_POWDERS)
            .addAll(BlockItemIds.CONCRETE_POWDER.map(BlockItemId::item).asList());
        tag(CROPS)
            .addTags(
                CROPS_BEETROOT,
                CROPS_CACTUS,
                CROPS_CARROT,
                CROPS_COCOA_BEAN,
                CROPS_MELON,
                CROPS_NETHER_WART,
                CROPS_POTATO,
                CROPS_PUMPKIN,
                CROPS_SUGAR_CANE,
                CROPS_WHEAT
            );
        tag(CROPS_BEETROOT).add(BEETROOT);
        tag(CROPS_CACTUS).add(CACTUS);
        tag(CROPS_CARROT).add(CARROT_CROP);
        tag(CROPS_COCOA_BEAN).add(COCOA_CROP);
        tag(CROPS_MELON).add(MELON);
        tag(CROPS_NETHER_WART).add(NETHER_WART);
        tag(CROPS_POTATO).add(POTATO_CROP);
        tag(CROPS_PUMPKIN).add(PUMPKIN);
        tag(CROPS_SUGAR_CANE).add(SUGAR_CANE);
        tag(CROPS_WHEAT).add(WHEAT);
        tag(DRINK_CONTAINING_BOTTLE)
            .add(
                POTION,
                HONEY_BOTTLE,
                OMINOUS_BOTTLE
            );
        tag(DRINK_CONTAINING_BUCKET).add(MILK_BUCKET);
        tag(DRINKS)
            .addTags(
                DRINKS_HONEY,
                DRINKS_JUICE,
                DRINKS_MAGIC,
                DRINKS_MILK,
                DRINKS_OMINOUS,
                DRINKS_WATER,
                DRINKS_WATERY
            );
        tag(DRINKS_HONEY).add(HONEY_BOTTLE);
        tag(DRINKS_JUICE);
        tag(DRINKS_MAGIC)
            .add(
                OMINOUS_BOTTLE,
                POTION
            );
        tag(DRINKS_MILK).add(MILK_BUCKET);
        tag(DRINKS_OMINOUS).add(OMINOUS_BOTTLE);
        tag(DRINKS_WATER);
        tag(DRINKS_WATERY).add(POTION);
        addColored(DYED, "{color}_banner");
        addColored(DYED, "{color}_bed");
        addColored(DYED, "{color}_candle");
        addColored(DYED, "{color}_carpet");
        addColored(DYED, "{color}_concrete");
        addColored(DYED, "{color}_concrete_powder");
        addColored(DYED, "{color}_glazed_terracotta");
        addColored(DYED, "{color}_shulker_box");
        addColored(DYED, "{color}_stained_glass");
        addColored(DYED, "{color}_stained_glass_pane");
        addColored(DYED, "{color}_terracotta");
        addColored(DYED, "{color}_wool");
        addColoredTags(tag(DYED)::addTags, DYED);
        tag(DUSTS)
            .addTags(
                DUSTS_GLOWSTONE,
                DUSTS_REDSTONE
            );
        tag(DUSTS_GLOWSTONE).add(GLOWSTONE_DUST);
        tag(DUSTS_REDSTONE).add(REDSTONE_DUST);
        addColored(DYES, "{color}_dye");
        addColoredTags(tag(DYES)::addTags, DYES);
        tag(EGGS)
            .add(
                EGG,
                BLUE_EGG,
                BROWN_EGG
            );
        tag(ENCHANTING_FUELS).addTag(GEMS_LAPIS); // forge:enchanting_fuels
        tag(ENDER_PEARLS).add(ENDER_PEARL);
        tag(FEATHERS).add(FEATHER);
        tag(FERTILIZERS).add(BONE_MEAL);
        tag(FOODS_FRUIT)
            .add(
                APPLE,
                GOLDEN_APPLE,
                ENCHANTED_GOLDEN_APPLE,
                CHORUS_FRUIT,
                MELON_SLICE
            );
        tag(FOODS_VEGETABLE)
            .add(
                GOLDEN_CARROT,
                BEETROOT
            )
            .add(
                CARROT_CROP,
                POTATO_CROP
            );
        tag(FOODS_BERRY)
            .add(
                SWEET_BERRY_CROP,
                GLOW_BERRY_CROP
            );
        tag(FOODS_BREAD).add(BREAD);
        tag(FOODS_COOKIE).add(COOKIE);
        tag(FOODS_DOUGH);
        tag(FOODS_RAW_MEAT)
            .add(
                BEEF,
                PORKCHOP,
                CHICKEN,
                RABBIT,
                MUTTON
            );
        tag(FOODS_RAW_FISH)
            .add(
                COD,
                SALMON,
                TROPICAL_FISH,
                PUFFERFISH
            );
        tag(FOODS_COOKED_MEAT)
            .add(
                COOKED_BEEF,
                COOKED_PORKCHOP,
                COOKED_CHICKEN,
                COOKED_RABBIT,
                COOKED_MUTTON
            );
        tag(FOODS_COOKED_FISH)
            .add(
                COOKED_COD,
                COOKED_SALMON
            );
        tag(FOODS_SOUP)
            .add(
                BEETROOT_SOUP,
                MUSHROOM_STEW,
                RABBIT_STEW,
                SUSPICIOUS_STEW
            );
        tag(FOODS_CANDY);
        tag(FOODS_PIE).add(PUMPKIN_PIE);
        tag(FOODS_EDIBLE_WHEN_PLACED).add(CAKE);
        tag(FOODS_FOOD_POISONING)
            .add(
                POISONOUS_POTATO,
                PUFFERFISH,
                SPIDER_EYE,
                CHICKEN,
                ROTTEN_FLESH
            );
        tag(FOODS_GOLDEN)
            .add(
                GOLDEN_APPLE,
                ENCHANTED_GOLDEN_APPLE,
                GOLDEN_CARROT
            );
        tag(FOODS)
            .add(
                BAKED_POTATO,
                HONEY_BOTTLE,
                OMINOUS_BOTTLE,
                DRIED_KELP
            )
            .addTags(
                FOODS_FRUIT,
                FOODS_VEGETABLE,
                FOODS_BERRY,
                FOODS_BREAD,
                FOODS_COOKIE,
                FOODS_RAW_MEAT,
                FOODS_RAW_FISH,
                FOODS_COOKED_MEAT,
                FOODS_COOKED_FISH,
                FOODS_SOUP,
                FOODS_CANDY,
                FOODS_PIE,
                FOODS_GOLDEN,
                FOODS_EDIBLE_WHEN_PLACED,
                FOODS_FOOD_POISONING
            );
        tag(ANIMAL_FOODS)
            .addTags(
                ItemTags.ARMADILLO_FOOD,
                ItemTags.AXOLOTL_FOOD,
                ItemTags.BEE_FOOD,
                ItemTags.CAMEL_FOOD,
                ItemTags.CAT_FOOD,
                ItemTags.CHICKEN_FOOD,
                ItemTags.COW_FOOD,
                ItemTags.FOX_FOOD,
                ItemTags.FROG_FOOD,
                ItemTags.GOAT_FOOD,
                ItemTags.HOGLIN_FOOD,
                ItemTags.HORSE_FOOD,
                ItemTags.LLAMA_FOOD,
                ItemTags.OCELOT_FOOD,
                ItemTags.PANDA_FOOD,
                ItemTags.PARROT_FOOD,
                ItemTags.PIG_FOOD,
                ItemTags.PIGLIN_FOOD,
                ItemTags.RABBIT_FOOD,
                ItemTags.SHEEP_FOOD,
                ItemTags.SNIFFER_FOOD,
                ItemTags.STRIDER_FOOD,
                ItemTags.TURTLE_FOOD,
                ItemTags.WOLF_FOOD
            );
        tag(GEMS)
            .addTags(
                GEMS_AMETHYST,
                GEMS_DIAMOND,
                GEMS_EMERALD,
                GEMS_LAPIS,
                GEMS_PRISMARINE,
                GEMS_QUARTZ
            );
        tag(GEMS_AMETHYST).add(AMETHYST_SHARD);
        tag(GEMS_DIAMOND).add(DIAMOND);
        tag(GEMS_EMERALD).add(EMERALD);
        tag(GEMS_LAPIS).add(LAPIS_LAZULI);
        tag(GEMS_PRISMARINE).add(PRISMARINE_CRYSTALS);
        tag(GEMS_QUARTZ).add(QUARTZ);
        tag(GUNPOWDERS).add(GUNPOWDER);
        tag(HIDDEN_FROM_RECIPE_VIEWERS);
        tag(INGOTS)
            .addTags(
                INGOTS_COPPER,
                INGOTS_GOLD,
                INGOTS_IRON,
                INGOTS_NETHERITE
            );
        tag(INGOTS_COPPER).add(COPPER_INGOT);
        tag(INGOTS_GOLD).add(GOLD_INGOT);
        tag(INGOTS_IRON).add(IRON_INGOT);
        tag(INGOTS_NETHERITE).add(NETHERITE_INGOT);
        tag(LEATHERS).add(LEATHER);
        tag(MUSHROOMS)
            .add(
                BROWN_MUSHROOM,
                RED_MUSHROOM
            );
        tag(MUSIC_DISCS)
            .add(
                MUSIC_DISC_13,
                MUSIC_DISC_CAT,
                MUSIC_DISC_BLOCKS,
                MUSIC_DISC_CHIRP,
                MUSIC_DISC_FAR,
                MUSIC_DISC_MALL,
                MUSIC_DISC_MELLOHI,
                MUSIC_DISC_STAL,
                MUSIC_DISC_STRAD,
                MUSIC_DISC_WARD,
                MUSIC_DISC_11,
                MUSIC_DISC_WAIT,
                MUSIC_DISC_OTHERSIDE,
                MUSIC_DISC_5,
                MUSIC_DISC_PIGSTEP,
                MUSIC_DISC_RELIC,
                MUSIC_DISC_CREATOR,
                MUSIC_DISC_CREATOR_MUSIC_BOX,
                MUSIC_DISC_PRECIPICE,
                MUSIC_DISC_LAVA_CHICKEN,
                MUSIC_DISC_TEARS
            );
        tag(NETHER_STARS).add(NETHER_STAR);
        tag(NUGGETS)
            .addTags(
                NUGGETS_GOLD,
                NUGGETS_IRON,
                NUGGETS_COPPER
            );
        tag(NUGGETS_COPPER).add(COPPER_NUGGET);
        tag(NUGGETS_IRON).add(IRON_NUGGET);
        tag(NUGGETS_GOLD).add(GOLD_NUGGET);
        tag(POTIONS_BOTTLE)
            .add(
                POTION,
                SPLASH_POTION,
                LINGERING_POTION
            );
        tag(POTIONS).addTags(POTIONS_BOTTLE);
        tag(RAW_MATERIALS)
            .addTags(
                RAW_MATERIALS_COPPER,
                RAW_MATERIALS_GOLD,
                RAW_MATERIALS_IRON
            );
        tag(RAW_MATERIALS_COPPER).add(RAW_COPPER);
        tag(RAW_MATERIALS_GOLD).add(RAW_GOLD);
        tag(RAW_MATERIALS_IRON).add(RAW_IRON);
        tag(RODS)
            .addTags(
                RODS_WOODEN,
                RODS_BLAZE,
                RODS_BREEZE
            );
        tag(RODS_BLAZE).add(BLAZE_ROD);
        tag(RODS_BREEZE).add(BREEZE_ROD);
        tag(RODS_WOODEN).add(STICK);
        tag(SEEDS)
            .addTags(
                SEEDS_BEETROOT,
                SEEDS_MELON,
                SEEDS_PUMPKIN,
                SEEDS_WHEAT
            );
        tag(SEEDS_BEETROOT).add(BEETROOT_CROP);
        tag(SEEDS_MELON).add(MELON_CROP);
        tag(SEEDS_PUMPKIN).add(PUMPKIN_CROP);
        tag(SEEDS_WHEAT).add(WHEAT_CROP);
        tag(SLIME_BALLS).add(SLIME_BALL);
        tag(SHULKER_BOXES)
            .add(
                SHULKER_BOX
            )
            .addAll(BlockItemIds.DYED_SHULKER_BOX.map(BlockItemId::item).asList());
        tag(STRINGS).add(TRIPWIRE);
        tag(VILLAGER_JOB_SITES)
            .add(
                BARREL,
                BLAST_FURNACE,
                BREWING_STAND,
                CARTOGRAPHY_TABLE,
                CAULDRON,
                COMPOSTER,
                FLETCHING_TABLE,
                GRINDSTONE,
                LECTERN,
                LOOM,
                SMITHING_TABLE,
                SMOKER,
                STONECUTTER
            );

        // Tools and Armors
        tag(TOOLS_SHIELD).add(SHIELD);
        tag(TOOLS_BOW).add(BOW);
        tag(TOOLS_BRUSH).add(BRUSH);
        tag(TOOLS_CROSSBOW).add(CROSSBOW);
        tag(TOOLS_FISHING_ROD).add(FISHING_ROD);
        tag(TOOLS_SHEAR).add(SHEARS);
        tag(TOOLS_TRIDENT).add(TRIDENT);
        tag(TOOLS_MACE).add(MACE);
        tag(TOOLS_IGNITER).add(FLINT_AND_STEEL);
        tag(MINING_TOOL_TOOLS)
            .add(
                WOODEN_PICKAXE,
                STONE_PICKAXE,
                COPPER_PICKAXE,
                IRON_PICKAXE,
                GOLDEN_PICKAXE,
                DIAMOND_PICKAXE,
                NETHERITE_PICKAXE
            );
        tag(MELEE_WEAPON_TOOLS)
            .add(
                MACE,
                TRIDENT,
                WOODEN_SWORD,
                STONE_SWORD,
                COPPER_SWORD,
                GOLDEN_SWORD,
                IRON_SWORD,
                DIAMOND_SWORD,
                NETHERITE_SWORD,
                WOODEN_AXE,
                STONE_AXE,
                COPPER_AXE,
                GOLDEN_AXE,
                IRON_AXE,
                DIAMOND_AXE,
                NETHERITE_AXE,
                WOODEN_SPEAR,
                STONE_SPEAR,
                COPPER_SPEAR,
                IRON_SPEAR,
                GOLDEN_SPEAR,
                DIAMOND_SPEAR,
                NETHERITE_SPEAR
            );
        tag(RANGED_WEAPON_TOOLS)
            .add(
                BOW,
                CROSSBOW,
                TRIDENT
            );
        tag(TOOLS_WRENCH);
        tag(TOOLS)
            .addTags(
                ItemTags.AXES,
                ItemTags.HOES,
                ItemTags.PICKAXES,
                ItemTags.SHOVELS,
                ItemTags.SWORDS
            )
            .addTags(
                TOOLS_BOW,
                TOOLS_BRUSH,
                TOOLS_CROSSBOW,
                TOOLS_FISHING_ROD,
                TOOLS_SHEAR,
                TOOLS_IGNITER,
                TOOLS_SHIELD,
                TOOLS_TRIDENT,
                TOOLS_MACE,
                MINING_TOOL_TOOLS,
                MELEE_WEAPON_TOOLS,
                RANGED_WEAPON_TOOLS,
                TOOLS_WRENCH
            );
        tag(ARMORS_HORSE)
            .add(
                COPPER_HORSE_ARMOR,
                DIAMOND_HORSE_ARMOR,
                GOLDEN_HORSE_ARMOR,
                IRON_HORSE_ARMOR,
                LEATHER_HORSE_ARMOR,
                NETHERITE_HORSE_ARMOR
            );
        tag(ARMORS_NAUTILUS)
            .add(
                COPPER_NAUTILUS_ARMOR,
                DIAMOND_NAUTILUS_ARMOR,
                GOLDEN_NAUTILUS_ARMOR,
                IRON_NAUTILUS_ARMOR,
                NETHERITE_NAUTILUS_ARMOR
            );
        tag(ARMORS_HUMANOID)
            .add(
                CHAINMAIL_BOOTS,
                CHAINMAIL_CHESTPLATE,
                CHAINMAIL_HELMET,
                CHAINMAIL_LEGGINGS,
                COPPER_BOOTS,
                COPPER_CHESTPLATE,
                COPPER_HELMET,
                COPPER_LEGGINGS,
                DIAMOND_BOOTS,
                DIAMOND_CHESTPLATE,
                DIAMOND_HELMET,
                DIAMOND_LEGGINGS,
                GOLDEN_BOOTS,
                GOLDEN_CHESTPLATE,
                GOLDEN_HELMET,
                GOLDEN_LEGGINGS,
                IRON_BOOTS,
                IRON_CHESTPLATE,
                IRON_HELMET,
                IRON_LEGGINGS,
                LEATHER_BOOTS,
                LEATHER_CHESTPLATE,
                LEATHER_HELMET,
                LEATHER_LEGGINGS,
                NETHERITE_BOOTS,
                NETHERITE_CHESTPLATE,
                NETHERITE_HELMET,
                NETHERITE_LEGGINGS,
                TURTLE_HELMET
            );
        tag(ARMORS_WOLF).add(WOLF_ARMOR);
        tag(ARMORS)
            .addTags(
                ItemTags.HEAD_ARMOR,
                ItemTags.CHEST_ARMOR,
                ItemTags.LEG_ARMOR,
                ItemTags.FOOT_ARMOR,
                ARMORS_HORSE,
                ARMORS_NAUTILUS,
                ARMORS_WOLF,
                ARMORS_HUMANOID
            );
        tag(ENCHANTABLES)
            .addTags(
                ItemTags.ARMOR_ENCHANTABLE,
                ItemTags.EQUIPPABLE_ENCHANTABLE,
                ItemTags.WEAPON_ENCHANTABLE,
                ItemTags.SHARP_WEAPON_ENCHANTABLE,
                ItemTags.MINING_ENCHANTABLE,
                ItemTags.MINING_LOOT_ENCHANTABLE,
                ItemTags.FISHING_ENCHANTABLE,
                ItemTags.TRIDENT_ENCHANTABLE,
                ItemTags.BOW_ENCHANTABLE,
                ItemTags.CROSSBOW_ENCHANTABLE,
                ItemTags.MACE_ENCHANTABLE,
                ItemTags.FIRE_ASPECT_ENCHANTABLE,
                ItemTags.DURABILITY_ENCHANTABLE,
                ItemTags.VANISHING_ENCHANTABLE
            );
        tag(SEEDS)
            .addTags(
                SEEDS_BEETROOT,
                SEEDS_MELON,
                SEEDS_PUMPKIN,
                SEEDS_WHEAT,
                SEEDS_PITCHER_PLANT,
                SEEDS_TORCHFLOWER
            );
        tag(SEEDS_BEETROOT).add(BEETROOT_CROP);
        tag(SEEDS_MELON).add(MELON_CROP);
        tag(SEEDS_PUMPKIN).add(PUMPKIN_CROP);
        tag(SEEDS_WHEAT).add(WHEAT_CROP);
        tag(SEEDS_PITCHER_PLANT).add(PITCHER_CROP);
        tag(SEEDS_TORCHFLOWER).add(TORCHFLOWER_CROP);
        tag(BONES).add(BONE);
        // Backwards compat definitions for pre-1.21 legacy `forge:` tags.
        // TODO: Remove backwards compat tag entries in 1.22
        addColored(tag(forgeItemTagKey("dyes"))::addTags, forgeItemTagKey("dyes"), "{color}_dye");
    }

    private void addColored(TagKey<Item> group, String pattern) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color : DyeColor.values()) {
            Identifier key = Identifier.withDefaultNamespace(pattern.replace("{color}", color.getName()));
            TagKey<Item> tag = getForgeItemTag(prefix + color.getName());
            Item item = BuiltInRegistries.ITEM.getValue(key);
            if (item == null || item == Items.AIR)
                throw new IllegalStateException("Unknown vanilla item: " + key);
            tag(tag)
                .add(ResourceKey.create(Registries.ITEM, key));
        }
    }

    private void addColored(Consumer<TagKey<Item>> consumer, TagKey<Item> group, String pattern) {
        String prefix = group.location().getPath() + '/';
        for (DyeColor color  : DyeColor.values()) {
            Identifier key = Identifier.withDefaultNamespace(pattern.replace("{color}",  color.getName()));
            TagKey<Item> tag = forgeItemTagKey(prefix + color.getName());
            Item item = ForgeRegistries.ITEMS.getValue(key);
            if (item == null || item  == Items.AIR)
                throw new IllegalStateException("Unknown vanilla item: " + key);
            tag(tag)
                .add(ResourceKey.create(Registries.ITEM, key));
            consumer.accept(tag);
        }
    }

    private static void addColoredTags(Consumer<TagKey<Item>> consumer, TagKey<Item> group) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color : DyeColor.values()) {
            TagKey<Item> tag = getForgeItemTag(prefix + color.getName());
            consumer.accept(tag);
        }
    }

    @SuppressWarnings("unchecked")
    private static TagKey<Item> getForgeItemTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey<Item>) Tags.Items.class.getDeclaredField(name).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(Tags.Items.class.getName() + " is missing tag name: " + name);
        }
    }

    private static Identifier forgeRl(String path) {
        return Identifier.fromNamespaceAndPath("forge", path);
    }

    private static TagKey<Item> forgeItemTagKey(String path) {
        return ItemTags.create(forgeRl(path));
    }

    @Override
    public String getName() {
        return "Forge Item Tags";
    }
}
