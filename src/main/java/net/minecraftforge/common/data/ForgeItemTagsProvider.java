/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@ApiStatus.Internal
public final class ForgeItemTagsProvider extends VanillaItemTagsProvider {
    public ForgeItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @SuppressWarnings({ "unchecked", "removal" })
    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        (new ForgeBlockItemTagsProvider() {
            @Override
            protected TagAppender<Block, Block> tag(TagKey<Block> p_409856_, TagKey<Item> p_406371_) {
                return new VanillaItemTagsProvider.BlockToItemConverter(ForgeItemTagsProvider.this.tag(p_406371_));
            }
        }).run();
        tag(Tags.Items.BONES).add(Items.BONE);
        tag(Tags.Items.BRICKS).addTags(Tags.Items.BRICKS_NORMAL, Tags.Items.BRICKS_NETHER, Tags.Items.BRICKS_RESIN);
        tag(Tags.Items.BRICKS_NORMAL).add(Items.BRICK);
        tag(Tags.Items.BRICKS_NETHER).add(Items.NETHER_BRICK);
        tag(Tags.Items.BRICKS_RESIN).add(Items.RESIN_BRICK);
        tag(Tags.Items.BUCKETS_EMPTY).add(Items.BUCKET);
        tag(Tags.Items.BUCKETS_WATER).add(Items.WATER_BUCKET);
        tag(Tags.Items.BUCKETS_LAVA).add(Items.LAVA_BUCKET);
        tag(Tags.Items.BUCKETS_MILK).add(Items.MILK_BUCKET);
        tag(Tags.Items.BUCKETS_POWDER_SNOW).add(Items.POWDER_SNOW_BUCKET);
        tag(Tags.Items.BUCKETS_ENTITY_WATER).add(Items.AXOLOTL_BUCKET, Items.COD_BUCKET, Items.PUFFERFISH_BUCKET, Items.TADPOLE_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.SALMON_BUCKET);
        tag(Tags.Items.BUCKETS).addTags(Tags.Items.BUCKETS_EMPTY, Tags.Items.BUCKETS_WATER, Tags.Items.BUCKETS_LAVA, Tags.Items.BUCKETS_MILK, Tags.Items.BUCKETS_POWDER_SNOW, Tags.Items.BUCKETS_ENTITY_WATER);
        tag(Tags.Items.CLUMPS)
                .addTag(Tags.Items.CLUMPS_RESIN);
        tag(Tags.Items.CLUMPS_RESIN)
                .add(Items.RESIN_CLUMP);
        tag(Tags.Items.CONCRETE_POWDERS)
                .add(Items.WHITE_CONCRETE_POWDER, Items.ORANGE_CONCRETE_POWDER, Items.MAGENTA_CONCRETE_POWDER,
                        Items.LIGHT_BLUE_CONCRETE_POWDER, Items.YELLOW_CONCRETE_POWDER, Items.LIME_CONCRETE_POWDER,
                        Items.PINK_CONCRETE_POWDER, Items.GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_CONCRETE_POWDER,
                        Items.CYAN_CONCRETE_POWDER, Items.PURPLE_CONCRETE_POWDER, Items.BLUE_CONCRETE_POWDER,
                        Items.BROWN_CONCRETE_POWDER, Items.GREEN_CONCRETE_POWDER, Items.RED_CONCRETE_POWDER,
                        Items.BLACK_CONCRETE_POWDER);
        tag(Tags.Items.CROPS).addTags(
                Tags.Items.CROPS_BEETROOT, Tags.Items.CROPS_CACTUS, Tags.Items.CROPS_CARROT,
                Tags.Items.CROPS_COCOA_BEAN, Tags.Items.CROPS_MELON, Tags.Items.CROPS_NETHER_WART,
                Tags.Items.CROPS_POTATO, Tags.Items.CROPS_PUMPKIN, Tags.Items.CROPS_SUGAR_CANE,
                Tags.Items.CROPS_WHEAT
        );
        tag(Tags.Items.CROPS_BEETROOT)
                .add(Items.BEETROOT);
        tag(Tags.Items.CROPS_CACTUS).add(Items.CACTUS);
        tag(Tags.Items.CROPS_CARROT)
                .add(Items.CARROT);
        tag(Tags.Items.CROPS_COCOA_BEAN).add(Items.COCOA_BEANS);
        tag(Tags.Items.CROPS_MELON).add(Items.MELON);
        tag(Tags.Items.CROPS_NETHER_WART)
                .add(Items.NETHER_WART);
        tag(Tags.Items.CROPS_POTATO)
                .add(Items.POTATO);
        tag(Tags.Items.CROPS_PUMPKIN).add(Items.PUMPKIN);
        tag(Tags.Items.CROPS_SUGAR_CANE).add(Items.SUGAR_CANE);
        tag(Tags.Items.CROPS_WHEAT)
                .add(Items.WHEAT);
        tag(Tags.Items.DRINK_CONTAINING_BOTTLE)
                .add(Items.POTION, Items.HONEY_BOTTLE, Items.OMINOUS_BOTTLE);
        tag(Tags.Items.DRINK_CONTAINING_BUCKET)
                .add(Items.MILK_BUCKET);
        tag(Tags.Items.DRINKS)
                .addTags(Tags.Items.DRINKS_HONEY, Tags.Items.DRINKS_JUICE, Tags.Items.DRINKS_MAGIC, Tags.Items.DRINKS_MILK,
                        Tags.Items.DRINKS_OMINOUS, Tags.Items.DRINKS_WATER, Tags.Items.DRINKS_WATERY);
        tag(Tags.Items.DRINKS_HONEY)
                .add(Items.HONEY_BOTTLE);
        tag(Tags.Items.DRINKS_JUICE);
        tag(Tags.Items.DRINKS_MAGIC)
                .add(Items.OMINOUS_BOTTLE, Items.POTION);
        tag(Tags.Items.DRINKS_MILK)
                .add(Items.MILK_BUCKET);
        tag(Tags.Items.DRINKS_OMINOUS)
                .add(Items.OMINOUS_BOTTLE);
        tag(Tags.Items.DRINKS_WATER);
        tag(Tags.Items.DRINKS_WATERY)
                .add(Items.POTION);
        addColored(Tags.Items.DYED, "{color}_banner");
        addColored(Tags.Items.DYED, "{color}_bed");
        addColored(Tags.Items.DYED, "{color}_candle");
        addColored(Tags.Items.DYED, "{color}_carpet");
        addColored(Tags.Items.DYED, "{color}_concrete");
        addColored(Tags.Items.DYED, "{color}_concrete_powder");
        addColored(Tags.Items.DYED, "{color}_glazed_terracotta");
        addColored(Tags.Items.DYED, "{color}_shulker_box");
        addColored(Tags.Items.DYED, "{color}_stained_glass");
        addColored(Tags.Items.DYED, "{color}_stained_glass_pane");
        addColored(Tags.Items.DYED, "{color}_terracotta");
        addColored(Tags.Items.DYED, "{color}_wool");
        addColoredTags(tag(Tags.Items.DYED)::addTags, Tags.Items.DYED);
        tag(Tags.Items.DUSTS).addTags(Tags.Items.DUSTS_GLOWSTONE, Tags.Items.DUSTS_REDSTONE);
        tag(Tags.Items.DUSTS_GLOWSTONE)
                .add(Items.GLOWSTONE_DUST);
        tag(Tags.Items.DUSTS_REDSTONE)
                .add(Items.REDSTONE);
        addColored(Tags.Items.DYES, "{color}_dye");
        addColoredTags(tag(Tags.Items.DYES)::addTags, Tags.Items.DYES);
        tag(Tags.Items.EGGS).add(Items.EGG, Items.BLUE_EGG, Items.BROWN_EGG);
        tag(Tags.Items.ENCHANTING_FUELS).addTag(Tags.Items.GEMS_LAPIS); // forge:enchanting_fuels
        tag(Tags.Items.ENDER_PEARLS)
                .add(Items.ENDER_PEARL);
        tag(Tags.Items.FEATHERS)
                .add(Items.FEATHER);
        tag(Tags.Items.FERTILIZERS).add(Items.BONE_MEAL);
        tag(Tags.Items.FOODS_FRUIT).add(Items.APPLE, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.CHORUS_FRUIT, Items.MELON_SLICE);
        tag(Tags.Items.FOODS_VEGETABLE).add(Items.CARROT, Items.GOLDEN_CARROT, Items.POTATO, Items.BEETROOT);
        tag(Tags.Items.FOODS_BERRY).add(Items.SWEET_BERRIES, Items.GLOW_BERRIES);
        tag(Tags.Items.FOODS_BREAD).add(Items.BREAD);
        tag(Tags.Items.FOODS_COOKIE).add(Items.COOKIE);
        tag(Tags.Items.FOODS_RAW_MEAT).add(Items.BEEF, Items.PORKCHOP, Items.CHICKEN, Items.RABBIT, Items.MUTTON);
        tag(Tags.Items.FOODS_RAW_FISH).add(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH);
        tag(Tags.Items.FOODS_COOKED_MEAT).add(Items.COOKED_BEEF, Items.COOKED_PORKCHOP, Items.COOKED_CHICKEN, Items.COOKED_RABBIT, Items.COOKED_MUTTON);
        tag(Tags.Items.FOODS_COOKED_FISH).add(Items.COOKED_COD, Items.COOKED_SALMON);
        tag(Tags.Items.FOODS_SOUP).add(Items.BEETROOT_SOUP, Items.MUSHROOM_STEW, Items.RABBIT_STEW, Items.SUSPICIOUS_STEW);
        tag(Tags.Items.FOODS_CANDY);
        tag(Tags.Items.FOODS_PIE).add(Items.PUMPKIN_PIE);
        tag(Tags.Items.FOODS_EDIBLE_WHEN_PLACED).add(Items.CAKE);
        tag(Tags.Items.FOODS_FOOD_POISONING).add(Items.POISONOUS_POTATO, Items.PUFFERFISH, Items.SPIDER_EYE, Items.CHICKEN, Items.ROTTEN_FLESH);
        tag(Tags.Items.FOODS_GOLDEN).add(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_CARROT);
        tag(Tags.Items.FOODS)
                .add(Items.BAKED_POTATO, Items.HONEY_BOTTLE, Items.OMINOUS_BOTTLE, Items.DRIED_KELP)
                .addTags(Tags.Items.FOODS_FRUIT, Tags.Items.FOODS_VEGETABLE, Tags.Items.FOODS_BERRY, Tags.Items.FOODS_BREAD, Tags.Items.FOODS_COOKIE,
                        Tags.Items.FOODS_RAW_MEAT, Tags.Items.FOODS_RAW_FISH, Tags.Items.FOODS_COOKED_MEAT, Tags.Items.FOODS_COOKED_FISH,
                        Tags.Items.FOODS_SOUP, Tags.Items.FOODS_CANDY, Tags.Items.FOODS_PIE, Tags.Items.FOODS_GOLDEN,
                        Tags.Items.FOODS_EDIBLE_WHEN_PLACED, Tags.Items.FOODS_FOOD_POISONING);
        tag(Tags.Items.ANIMAL_FOODS)
                .addTags(ItemTags.ARMADILLO_FOOD, ItemTags.AXOLOTL_FOOD, ItemTags.BEE_FOOD, ItemTags.CAMEL_FOOD,
                        ItemTags.CAT_FOOD, ItemTags.CHICKEN_FOOD, ItemTags.COW_FOOD, ItemTags.FOX_FOOD, ItemTags.FROG_FOOD,
                        ItemTags.GOAT_FOOD, ItemTags.HOGLIN_FOOD, ItemTags.HORSE_FOOD, ItemTags.LLAMA_FOOD, ItemTags.OCELOT_FOOD,
                        ItemTags.PANDA_FOOD, ItemTags.PARROT_FOOD, ItemTags.PIG_FOOD, ItemTags.PIGLIN_FOOD, ItemTags.RABBIT_FOOD,
                        ItemTags.SHEEP_FOOD, ItemTags.SNIFFER_FOOD, ItemTags.STRIDER_FOOD, ItemTags.TURTLE_FOOD, ItemTags.WOLF_FOOD);
        tag(Tags.Items.GEMS)
                .addTags(Tags.Items.GEMS_AMETHYST, Tags.Items.GEMS_DIAMOND, Tags.Items.GEMS_EMERALD, Tags.Items.GEMS_LAPIS, Tags.Items.GEMS_PRISMARINE, Tags.Items.GEMS_QUARTZ);
        tag(Tags.Items.GEMS_AMETHYST)
                .add(Items.AMETHYST_SHARD);
        tag(Tags.Items.GEMS_DIAMOND)
                .add(Items.DIAMOND);
        tag(Tags.Items.GEMS_EMERALD)
                .add(Items.EMERALD);
        tag(Tags.Items.GEMS_LAPIS)
                .add(Items.LAPIS_LAZULI);
        tag(Tags.Items.GEMS_PRISMARINE)
                .add(Items.PRISMARINE_CRYSTALS);
        tag(Tags.Items.GEMS_QUARTZ)
                .add(Items.QUARTZ);
        tag(Tags.Items.GUNPOWDERS).add(Items.GUNPOWDER);
        tag(Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS);
        tag(Tags.Items.INGOTS)
                .addTags(Tags.Items.INGOTS_COPPER, Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_NETHERITE);
        tag(Tags.Items.INGOTS_COPPER)
                .add(Items.COPPER_INGOT);
        tag(Tags.Items.INGOTS_GOLD)
                .add(Items.GOLD_INGOT);
        tag(Tags.Items.INGOTS_IRON)
                .add(Items.IRON_INGOT);
        tag(Tags.Items.INGOTS_NETHERITE)
                .add(Items.NETHERITE_INGOT);
        tag(Tags.Items.LEATHERS)
                .add(Items.LEATHER);
        tag(Tags.Items.MUSHROOMS)
                .add(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM);
        tag(Tags.Items.MUSIC_DISCS).add(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP,
                Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD,
                Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT, Items.MUSIC_DISC_OTHERSIDE, Items.MUSIC_DISC_5,
                Items.MUSIC_DISC_PIGSTEP, Items.MUSIC_DISC_RELIC, Items.MUSIC_DISC_CREATOR, Items.MUSIC_DISC_CREATOR_MUSIC_BOX,
                Items.MUSIC_DISC_PRECIPICE, Items.MUSIC_DISC_LAVA_CHICKEN, Items.MUSIC_DISC_TEARS);
        tag(Tags.Items.NETHER_STARS)
                .add(Items.NETHER_STAR);
        tag(Tags.Items.NUGGETS)
                .addTags(Tags.Items.NUGGETS_GOLD, Tags.Items.NUGGETS_IRON, Tags.Items.NUGGETS_COPPER);
        tag(Tags.Items.NUGGETS_COPPER)
                .add(Items.COPPER_NUGGET);
        tag(Tags.Items.NUGGETS_IRON)
                .add(Items.IRON_NUGGET);
        tag(Tags.Items.NUGGETS_GOLD)
                .add(Items.GOLD_NUGGET);
        tag(Tags.Items.POTIONS_BOTTLE).add(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        tag(Tags.Items.POTIONS).addTags(Tags.Items.POTIONS_BOTTLE);
        tag(Tags.Items.RAW_MATERIALS)
                .addTags(Tags.Items.RAW_MATERIALS_COPPER, Tags.Items.RAW_MATERIALS_GOLD, Tags.Items.RAW_MATERIALS_IRON);
        tag(Tags.Items.RAW_MATERIALS_COPPER)
                .add(Items.RAW_COPPER);
        tag(Tags.Items.RAW_MATERIALS_GOLD)
                .add(Items.RAW_GOLD);
        tag(Tags.Items.RAW_MATERIALS_IRON)
                .add(Items.RAW_IRON);
        tag(Tags.Items.RODS)
                .addTags(Tags.Items.RODS_WOODEN, Tags.Items.RODS_BLAZE, Tags.Items.RODS_BREEZE);
        tag(Tags.Items.RODS_BLAZE)
                .add(Items.BLAZE_ROD);
        tag(Tags.Items.RODS_BREEZE).add(Items.BREEZE_ROD);
        tag(Tags.Items.RODS_WOODEN)
                .add(Items.STICK);
        tag(Tags.Items.SEEDS).addTags(Tags.Items.SEEDS_BEETROOT, Tags.Items.SEEDS_MELON, Tags.Items.SEEDS_PUMPKIN, Tags.Items.SEEDS_WHEAT);
        tag(Tags.Items.SEEDS_BEETROOT).add(Items.BEETROOT_SEEDS);
        tag(Tags.Items.SEEDS_MELON).add(Items.MELON_SEEDS);
        tag(Tags.Items.SEEDS_PUMPKIN).add(Items.PUMPKIN_SEEDS);
        tag(Tags.Items.SEEDS_WHEAT).add(Items.WHEAT_SEEDS);
        tag(Tags.Items.SLIME_BALLS)
                .add(Items.SLIME_BALL);
        tag(Tags.Items.SHULKER_BOXES)
                .add(Items.SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX,
                        Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX,
                        Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
                        Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
                        Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
                        Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX);
        tag(Tags.Items.STRINGS)
                .add(Items.STRING);
        tag(Tags.Items.VILLAGER_JOB_SITES).add(
                Items.BARREL, Items.BLAST_FURNACE, Items.BREWING_STAND, Items.CARTOGRAPHY_TABLE,
                Items.CAULDRON, Items.COMPOSTER, Items.FLETCHING_TABLE, Items.GRINDSTONE,
                Items.LECTERN, Items.LOOM, Items.SMITHING_TABLE, Items.SMOKER, Items.STONECUTTER);

        // Tools and Armors
        tag(Tags.Items.TOOLS_SHIELD)
                .add(Items.SHIELD);
        tag(Tags.Items.TOOLS_BOW)
                .add(Items.BOW);
        tag(Tags.Items.TOOLS_BRUSH).add(Items.BRUSH);
        tag(Tags.Items.TOOLS_CROSSBOW)
                .add(Items.CROSSBOW);
        tag(Tags.Items.TOOLS_FISHING_ROD)
                .add(Items.FISHING_ROD);
        tag(Tags.Items.TOOLS_SHEAR)
                .add(Items.SHEARS);
        tag(Tags.Items.TOOLS_TRIDENT).add(Items.TRIDENT);
        tag(Tags.Items.TOOLS_MACE).add(Items.MACE);
        tag(Tags.Items.TOOLS_IGNITER).add(Items.FLINT_AND_STEEL);
        tag(Tags.Items.MINING_TOOL_TOOLS).add(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.COPPER_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
        tag(Tags.Items.MELEE_WEAPON_TOOLS).add(
                Items.MACE, Items.TRIDENT,
                Items.WOODEN_SWORD, Items.STONE_SWORD, Items.COPPER_SWORD, Items.GOLDEN_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD,
                Items.WOODEN_AXE, Items.STONE_AXE, Items.COPPER_AXE, Items.GOLDEN_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE,
                Items.WOODEN_SPEAR, Items.STONE_SPEAR, Items.COPPER_SPEAR, Items.IRON_SPEAR, Items.GOLDEN_SPEAR, Items.DIAMOND_SPEAR, Items.NETHERITE_SPEAR
        );
        tag(Tags.Items.RANGED_WEAPON_TOOLS).add(Items.BOW, Items.CROSSBOW, Items.TRIDENT);
        tag(Tags.Items.TOOLS_WRENCH);
        tag(Tags.Items.TOOLS)
                .addTags(ItemTags.AXES, ItemTags.HOES, ItemTags.PICKAXES, ItemTags.SHOVELS, ItemTags.SWORDS)
                .addTags(Tags.Items.TOOLS_BOW, Tags.Items.TOOLS_BRUSH, Tags.Items.TOOLS_CROSSBOW, Tags.Items.TOOLS_FISHING_ROD, Tags.Items.TOOLS_SHEAR, Tags.Items.TOOLS_IGNITER, Tags.Items.TOOLS_SHIELD, Tags.Items.TOOLS_TRIDENT, Tags.Items.TOOLS_MACE, Tags.Items.MINING_TOOL_TOOLS, Tags.Items.MELEE_WEAPON_TOOLS, Tags.Items.RANGED_WEAPON_TOOLS, Tags.Items.TOOLS_WRENCH);
        tag(Tags.Items.ARMORS_HORSE)
                .add(
                        Items.COPPER_HORSE_ARMOR,
                        Items.DIAMOND_HORSE_ARMOR,
                        Items.GOLDEN_HORSE_ARMOR,
                        Items.IRON_HORSE_ARMOR,
                        Items.LEATHER_HORSE_ARMOR,
                        Items.NETHERITE_HORSE_ARMOR
                );
        tag(Tags.Items.ARMORS_NAUTILUS)
                .add(
                        Items.COPPER_NAUTILUS_ARMOR,
                        Items.DIAMOND_NAUTILUS_ARMOR,
                        Items.GOLDEN_NAUTILUS_ARMOR,
                        Items.IRON_NAUTILUS_ARMOR,
                        Items.NETHERITE_NAUTILUS_ARMOR
                );
        tag(Tags.Items.ARMORS_HUMANOID)
                .add(Items.CHAINMAIL_BOOTS, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_LEGGINGS,
                        Items.COPPER_BOOTS, Items.COPPER_CHESTPLATE, Items.COPPER_HELMET, Items.COPPER_LEGGINGS,
                        Items.DIAMOND_BOOTS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET, Items.DIAMOND_LEGGINGS,
                        Items.GOLDEN_BOOTS, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_HELMET, Items.GOLDEN_LEGGINGS,
                        Items.IRON_BOOTS, Items.IRON_CHESTPLATE, Items.IRON_HELMET, Items.IRON_LEGGINGS,
                        Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS,
                        Items.NETHERITE_BOOTS, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_HELMET, Items.NETHERITE_LEGGINGS,
                        Items.TURTLE_HELMET);
        tag(Tags.Items.ARMORS_WOLF)
                .add(Items.WOLF_ARMOR);
        tag(Tags.Items.ARMORS)
                .addTags(ItemTags.HEAD_ARMOR, ItemTags.CHEST_ARMOR, ItemTags.LEG_ARMOR, ItemTags.FOOT_ARMOR,
                        Tags.Items.ARMORS_HORSE, Tags.Items.ARMORS_NAUTILUS, Tags.Items.ARMORS_WOLF, Tags.Items.ARMORS_HUMANOID);
        tag(Tags.Items.ENCHANTABLES)
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
        tag(Tags.Items.SEEDS)
                .addTags(
                        Tags.Items.SEEDS_BEETROOT, Tags.Items.SEEDS_MELON, Tags.Items.SEEDS_PUMPKIN,
                        Tags.Items.SEEDS_WHEAT, Tags.Items.SEEDS_PITCHER_PLANT, Tags.Items.SEEDS_TORCHFLOWER);
        tag(Tags.Items.SEEDS_BEETROOT).add(Items.BEETROOT_SEEDS);
        tag(Tags.Items.SEEDS_MELON).add(Items.MELON_SEEDS);
        tag(Tags.Items.SEEDS_PUMPKIN).add(Items.PUMPKIN_SEEDS);
        tag(Tags.Items.SEEDS_WHEAT).add(Items.WHEAT_SEEDS);
        tag(Tags.Items.SEEDS_PITCHER_PLANT).add(Items.PITCHER_POD);
        tag(Tags.Items.SEEDS_TORCHFLOWER).add(Items.TORCHFLOWER_SEEDS);

        tag(Tags.Items.BONES).add(Items.BONE);
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
            tag(tag).add(item);
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
            tag(tag).add(item);
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
