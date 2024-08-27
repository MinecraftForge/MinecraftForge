/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@ApiStatus.Internal
public final class ForgeItemTagsProvider extends ItemTagsProvider {
    public ForgeItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, "forge", existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        copy(Tags.Blocks.BARRELS, Tags.Items.BARRELS);
        copy(Tags.Blocks.BARRELS_WOODEN, Tags.Items.BARRELS_WOODEN);
        tag(Tags.Items.BONES).add(Items.BONE);
        copy(Tags.Blocks.BOOKSHELVES, Tags.Items.BOOKSHELVES);
        tag(Tags.Items.BRICKS).addTags(Tags.Items.BRICKS_NORMAL, Tags.Items.BRICKS_NETHER);
        tag(Tags.Items.BRICKS_NORMAL).add(Items.BRICK);
        tag(Tags.Items.BRICKS_NETHER).add(Items.NETHER_BRICK);
        tag(Tags.Items.BUCKETS_EMPTY).add(Items.BUCKET);
        tag(Tags.Items.BUCKETS_WATER).add(Items.WATER_BUCKET);
        tag(Tags.Items.BUCKETS_LAVA).add(Items.LAVA_BUCKET);
        tag(Tags.Items.BUCKETS_MILK).add(Items.MILK_BUCKET);
        tag(Tags.Items.BUCKETS_POWDER_SNOW).add(Items.POWDER_SNOW_BUCKET);
        tag(Tags.Items.BUCKETS_ENTITY_WATER).add(Items.AXOLOTL_BUCKET, Items.COD_BUCKET, Items.PUFFERFISH_BUCKET, Items.TADPOLE_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.SALMON_BUCKET);
        tag(Tags.Items.BUCKETS).addTags(Tags.Items.BUCKETS_EMPTY, Tags.Items.BUCKETS_WATER, Tags.Items.BUCKETS_LAVA, Tags.Items.BUCKETS_MILK, Tags.Items.BUCKETS_POWDER_SNOW, Tags.Items.BUCKETS_ENTITY_WATER);
        copy(Tags.Blocks.BUDDING_BLOCKS, Tags.Items.BUDDING_BLOCKS);
        copy(Tags.Blocks.BUDS, Tags.Items.BUDS);
        copy(Tags.Blocks.CHAINS, Tags.Items.CHAINS);
        copy(Tags.Blocks.CHESTS, Tags.Items.CHESTS);
        copy(Tags.Blocks.CHESTS_ENDER, Tags.Items.CHESTS_ENDER);
        copy(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED);
        copy(Tags.Blocks.CHESTS_WOODEN, Tags.Items.CHESTS_WOODEN);
        copy(Tags.Blocks.CLUSTERS, Tags.Items.CLUSTERS);
        copy(Tags.Blocks.COBBLESTONES, Tags.Items.COBBLESTONES);
        copy(Tags.Blocks.COBBLESTONES_NORMAL, Tags.Items.COBBLESTONES_NORMAL);
        copy(Tags.Blocks.COBBLESTONES_INFESTED, Tags.Items.COBBLESTONES_INFESTED);
        copy(Tags.Blocks.COBBLESTONES_MOSSY, Tags.Items.COBBLESTONES_MOSSY);
        copy(Tags.Blocks.COBBLESTONES_DEEPSLATE, Tags.Items.COBBLESTONES_DEEPSLATE);
        copy(Tags.Blocks.CONCRETES, Tags.Items.CONCRETES);
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
        tag(Tags.Items.CROPS_BEETROOT).add(Items.BEETROOT);
        tag(Tags.Items.CROPS_CACTUS).add(Items.CACTUS);
        tag(Tags.Items.CROPS_CARROT).add(Items.CARROT);
        tag(Tags.Items.CROPS_COCOA_BEAN).add(Items.COCOA_BEANS);
        tag(Tags.Items.CROPS_MELON).add(Items.MELON);
        tag(Tags.Items.CROPS_NETHER_WART).add(Items.NETHER_WART);
        tag(Tags.Items.CROPS_POTATO).add(Items.POTATO);
        tag(Tags.Items.CROPS_PUMPKIN).add(Items.PUMPKIN);
        tag(Tags.Items.CROPS_SUGAR_CANE).add(Items.SUGAR_CANE);
        tag(Tags.Items.CROPS_WHEAT).add(Items.WHEAT);
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
        tag(Tags.Items.DUSTS_GLOWSTONE).add(Items.GLOWSTONE_DUST);
        tag(Tags.Items.DUSTS_REDSTONE).add(Items.REDSTONE);
        addColored(Tags.Items.DYES, "{color}_dye");
        addColoredTags(tag(Tags.Items.DYES)::addTags, Tags.Items.DYES);
        tag(Tags.Items.EGGS).add(Items.EGG);
        tag(Tags.Items.ENCHANTING_FUELS).addTag(Tags.Items.GEMS_LAPIS);
        copy(Tags.Blocks.END_STONES, Tags.Items.END_STONES);
        tag(Tags.Items.ENDER_PEARLS).add(Items.ENDER_PEARL);
        tag(Tags.Items.FEATHERS).add(Items.FEATHER);
        copy(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES);
        copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
        copy(Tags.Blocks.FENCES, Tags.Items.FENCES);
        copy(Tags.Blocks.FENCES_NETHER_BRICK, Tags.Items.FENCES_NETHER_BRICK);
        copy(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
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
        tag(Tags.Items.FOODS_EDIBLE_WHEN_PLACED).add(Items.CAKE);
        tag(Tags.Items.FOODS_FOOD_POISONING).add(Items.POISONOUS_POTATO, Items.PUFFERFISH, Items.SPIDER_EYE, Items.CHICKEN, Items.ROTTEN_FLESH);
        tag(Tags.Items.FOODS_GOLDEN).add(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_CARROT);
        tag(Tags.Items.FOODS)
                .add(Items.BAKED_POTATO, Items.PUMPKIN_PIE, Items.HONEY_BOTTLE, Items.OMINOUS_BOTTLE, Items.DRIED_KELP)
                .addTags(Tags.Items.FOODS_FRUIT, Tags.Items.FOODS_VEGETABLE, Tags.Items.FOODS_BERRY, Tags.Items.FOODS_BREAD, Tags.Items.FOODS_COOKIE,
                        Tags.Items.FOODS_RAW_MEAT, Tags.Items.FOODS_RAW_FISH, Tags.Items.FOODS_COOKED_MEAT, Tags.Items.FOODS_COOKED_FISH,
                        Tags.Items.FOODS_SOUP, Tags.Items.FOODS_CANDY, Tags.Items.FOODS_GOLDEN,
                        Tags.Items.FOODS_EDIBLE_WHEN_PLACED, Tags.Items.FOODS_FOOD_POISONING);
        tag(Tags.Items.GEMS).addTags(Tags.Items.GEMS_AMETHYST, Tags.Items.GEMS_DIAMOND, Tags.Items.GEMS_EMERALD, Tags.Items.GEMS_LAPIS, Tags.Items.GEMS_PRISMARINE, Tags.Items.GEMS_QUARTZ);
        tag(Tags.Items.GEMS_AMETHYST).add(Items.AMETHYST_SHARD);
        tag(Tags.Items.GEMS_DIAMOND).add(Items.DIAMOND);
        tag(Tags.Items.GEMS_EMERALD).add(Items.EMERALD);
        tag(Tags.Items.GEMS_LAPIS).add(Items.LAPIS_LAZULI);
        tag(Tags.Items.GEMS_PRISMARINE).add(Items.PRISMARINE_CRYSTALS);
        tag(Tags.Items.GEMS_QUARTZ).add(Items.QUARTZ);
        copy(Tags.Blocks.GLASS_BLOCKS, Tags.Items.GLASS_BLOCKS);
        copy(Tags.Blocks.GLASS_BLOCKS_COLORLESS, Tags.Items.GLASS_BLOCKS_COLORLESS);
        copy(Tags.Blocks.GLASS_BLOCKS_TINTED, Tags.Items.GLASS_BLOCKS_TINTED);
        copy(Tags.Blocks.GLASS_BLOCKS_CHEAP, Tags.Items.GLASS_BLOCKS_CHEAP);
        copy(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
        copy(Tags.Blocks.GLASS_PANES_COLORLESS, Tags.Items.GLASS_PANES_COLORLESS);
        copy(Tags.Blocks.GLAZED_TERRACOTTAS, Tags.Items.GLAZED_TERRACOTTAS);
        copy(Tags.Blocks.GRAVELS, Tags.Items.GRAVELS);
        tag(Tags.Items.GUNPOWDERS).add(Items.GUNPOWDER);
        tag(Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS);
        tag(Tags.Items.INGOTS).addTags(Tags.Items.INGOTS_COPPER, Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_NETHERITE);
        tag(Tags.Items.INGOTS_COPPER).add(Items.COPPER_INGOT);
        tag(Tags.Items.INGOTS_GOLD).add(Items.GOLD_INGOT);
        tag(Tags.Items.INGOTS_IRON).add(Items.IRON_INGOT);
        tag(Tags.Items.INGOTS_NETHERITE).add(Items.NETHERITE_INGOT);
        tag(Tags.Items.LEATHERS).add(Items.LEATHER);
        tag(Tags.Items.MUSHROOMS).add(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM);
        tag(Tags.Items.MUSIC_DISCS).add(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP,
                Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD,
                Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT, Items.MUSIC_DISC_OTHERSIDE, Items.MUSIC_DISC_5,
                Items.MUSIC_DISC_PIGSTEP, Items.MUSIC_DISC_RELIC, Items.MUSIC_DISC_CREATOR, Items.MUSIC_DISC_CREATOR_MUSIC_BOX,
                Items.MUSIC_DISC_PRECIPICE);
        tag(Tags.Items.NETHER_STARS).add(Items.NETHER_STAR);
        copy(Tags.Blocks.NETHERRACKS, Tags.Items.NETHERRACKS);
        tag(Tags.Items.NUGGETS).addTags(Tags.Items.NUGGETS_GOLD, Tags.Items.NUGGETS_IRON);
        tag(Tags.Items.NUGGETS_IRON).add(Items.IRON_NUGGET);
        tag(Tags.Items.NUGGETS_GOLD).add(Items.GOLD_NUGGET);
        copy(Tags.Blocks.OBSIDIANS, Tags.Items.OBSIDIANS);
        copy(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE, Tags.Items.ORE_BEARING_GROUND_DEEPSLATE);
        copy(Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK, Tags.Items.ORE_BEARING_GROUND_NETHERRACK);
        copy(Tags.Blocks.ORE_BEARING_GROUND_STONE, Tags.Items.ORE_BEARING_GROUND_STONE);
        copy(Tags.Blocks.ORE_RATES_DENSE, Tags.Items.ORE_RATES_DENSE);
        copy(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR);
        copy(Tags.Blocks.ORE_RATES_SPARSE, Tags.Items.ORE_RATES_SPARSE);
        copy(Tags.Blocks.ORES, Tags.Items.ORES);
        copy(Tags.Blocks.ORES_COAL, Tags.Items.ORES_COAL);
        copy(Tags.Blocks.ORES_COPPER, Tags.Items.ORES_COPPER);
        copy(Tags.Blocks.ORES_DIAMOND, Tags.Items.ORES_DIAMOND);
        copy(Tags.Blocks.ORES_EMERALD, Tags.Items.ORES_EMERALD);
        copy(Tags.Blocks.ORES_GOLD, Tags.Items.ORES_GOLD);
        copy(Tags.Blocks.ORES_IRON, Tags.Items.ORES_IRON);
        copy(Tags.Blocks.ORES_LAPIS, Tags.Items.ORES_LAPIS);
        copy(Tags.Blocks.ORES_QUARTZ, Tags.Items.ORES_QUARTZ);
        copy(Tags.Blocks.ORES_REDSTONE, Tags.Items.ORES_REDSTONE);
        copy(Tags.Blocks.ORES_NETHERITE_SCRAP, Tags.Items.ORES_NETHERITE_SCRAP);
        copy(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE);
        copy(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
        copy(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE);
        copy(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES, Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES);
        copy(Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES, Tags.Items.PLAYER_WORKSTATIONS_FURNACES);
        tag(Tags.Items.RAW_MATERIALS).addTags(Tags.Items.RAW_MATERIALS_COPPER, Tags.Items.RAW_MATERIALS_GOLD, Tags.Items.RAW_MATERIALS_IRON);
        tag(Tags.Items.RAW_MATERIALS_COPPER).add(Items.RAW_COPPER);
        tag(Tags.Items.RAW_MATERIALS_GOLD).add(Items.RAW_GOLD);
        tag(Tags.Items.RAW_MATERIALS_IRON).add(Items.RAW_IRON);
        tag(Tags.Items.RODS).addTags(Tags.Items.RODS_WOODEN, Tags.Items.RODS_BLAZE, Tags.Items.RODS_BREEZE);
        tag(Tags.Items.RODS_BLAZE).add(Items.BLAZE_ROD);
        tag(Tags.Items.RODS_BREEZE).add(Items.BREEZE_ROD);
        tag(Tags.Items.RODS_WOODEN).add(Items.STICK);
        copy(Tags.Blocks.ROPES, Tags.Items.ROPES);
        copy(Tags.Blocks.SANDS, Tags.Items.SANDS);
        copy(Tags.Blocks.SANDS_COLORLESS, Tags.Items.SANDS_COLORLESS);
        copy(Tags.Blocks.SANDS_RED, Tags.Items.SANDS_RED);
        copy(Tags.Blocks.SANDSTONE_BLOCKS, Tags.Items.SANDSTONE_BLOCKS);
        copy(Tags.Blocks.SANDSTONE_SLABS, Tags.Items.SANDSTONE_SLABS);
        copy(Tags.Blocks.SANDSTONE_STAIRS, Tags.Items.SANDSTONE_STAIRS);
        copy(Tags.Blocks.SANDSTONE_RED_BLOCKS, Tags.Items.SANDSTONE_RED_BLOCKS);
        copy(Tags.Blocks.SANDSTONE_RED_SLABS, Tags.Items.SANDSTONE_RED_SLABS);
        copy(Tags.Blocks.SANDSTONE_RED_STAIRS, Tags.Items.SANDSTONE_RED_STAIRS);
        copy(Tags.Blocks.SANDSTONE_UNCOLORED_BLOCKS, Tags.Items.SANDSTONE_UNCOLORED_BLOCKS);
        copy(Tags.Blocks.SANDSTONE_UNCOLORED_SLABS, Tags.Items.SANDSTONE_UNCOLORED_SLABS);
        copy(Tags.Blocks.SANDSTONE_UNCOLORED_STAIRS, Tags.Items.SANDSTONE_UNCOLORED_STAIRS);
        tag(Tags.Items.SEEDS).addTags(Tags.Items.SEEDS_BEETROOT, Tags.Items.SEEDS_MELON, Tags.Items.SEEDS_PUMPKIN, Tags.Items.SEEDS_WHEAT);
        tag(Tags.Items.SEEDS_BEETROOT).add(Items.BEETROOT_SEEDS);
        tag(Tags.Items.SEEDS_MELON).add(Items.MELON_SEEDS);
        tag(Tags.Items.SEEDS_PUMPKIN).add(Items.PUMPKIN_SEEDS);
        tag(Tags.Items.SEEDS_WHEAT).add(Items.WHEAT_SEEDS);
        tag(Tags.Items.SLIME_BALLS).add(Items.SLIME_BALL);
        tag(Tags.Items.SHULKER_BOXES)
                .add(Items.SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX,
                        Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX,
                        Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
                        Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
                        Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
                        Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX);
        copy(Tags.Blocks.STONES, Tags.Items.STONES);
        copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        copy(Tags.Blocks.STORAGE_BLOCKS_BONE_MEAL, Tags.Items.STORAGE_BLOCKS_BONE_MEAL);
        copy(Tags.Blocks.STORAGE_BLOCKS_COAL, Tags.Items.STORAGE_BLOCKS_COAL);
        copy(Tags.Blocks.STORAGE_BLOCKS_COPPER, Tags.Items.STORAGE_BLOCKS_COPPER);
        copy(Tags.Blocks.STORAGE_BLOCKS_DIAMOND, Tags.Items.STORAGE_BLOCKS_DIAMOND);
        copy(Tags.Blocks.STORAGE_BLOCKS_DRIED_KELP, Tags.Items.STORAGE_BLOCKS_DRIED_KELP);
        copy(Tags.Blocks.STORAGE_BLOCKS_EMERALD, Tags.Items.STORAGE_BLOCKS_EMERALD);
        copy(Tags.Blocks.STORAGE_BLOCKS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD);
        copy(Tags.Blocks.STORAGE_BLOCKS_IRON, Tags.Items.STORAGE_BLOCKS_IRON);
        copy(Tags.Blocks.STORAGE_BLOCKS_LAPIS, Tags.Items.STORAGE_BLOCKS_LAPIS);
        copy(Tags.Blocks.STORAGE_BLOCKS_NETHERITE, Tags.Items.STORAGE_BLOCKS_NETHERITE);
        copy(Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER, Tags.Items.STORAGE_BLOCKS_RAW_COPPER);
        copy(Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD, Tags.Items.STORAGE_BLOCKS_RAW_GOLD);
        copy(Tags.Blocks.STORAGE_BLOCKS_RAW_IRON, Tags.Items.STORAGE_BLOCKS_RAW_IRON);
        copy(Tags.Blocks.STORAGE_BLOCKS_REDSTONE, Tags.Items.STORAGE_BLOCKS_REDSTONE);
        copy(Tags.Blocks.STORAGE_BLOCKS_SLIME, Tags.Items.STORAGE_BLOCKS_SLIME);
        copy(Tags.Blocks.STORAGE_BLOCKS_WHEAT, Tags.Items.STORAGE_BLOCKS_WHEAT);
        tag(Tags.Items.STRINGS).add(Items.STRING);
        tag(Tags.Items.VILLAGER_JOB_SITES).add(
                Items.BARREL, Items.BLAST_FURNACE, Items.BREWING_STAND, Items.CARTOGRAPHY_TABLE,
                Items.CAULDRON, Items.COMPOSTER, Items.FLETCHING_TABLE, Items.GRINDSTONE,
                Items.LECTERN, Items.LOOM, Items.SMITHING_TABLE, Items.SMOKER, Items.STONECUTTER);

        // Tools and Armors
        tag(Tags.Items.TOOLS_SHIELD).add(Items.SHIELD);
        tag(Tags.Items.TOOLS_BOW).add(Items.BOW);
        tag(Tags.Items.TOOLS_BRUSH).add(Items.BRUSH);
        tag(Tags.Items.TOOLS_CROSSBOW).add(Items.CROSSBOW);
        tag(Tags.Items.TOOLS_FISHING_ROD).add(Items.FISHING_ROD);
        tag(Tags.Items.TOOLS_SHEAR).add(Items.SHEARS);
        tag(Tags.Items.TOOLS_SPEAR).add(Items.TRIDENT);
        tag(Tags.Items.TOOLS_MACE).add(Items.MACE);
        tag(Tags.Items.TOOLS_IGNITER).add(Items.FLINT_AND_STEEL);
        tag(Tags.Items.MINING_TOOL_TOOLS).add(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
        tag(Tags.Items.MELEE_WEAPON_TOOLS).add(
                Items.MACE, Items.TRIDENT,
                Items.WOODEN_SWORD, Items.STONE_SWORD, Items.GOLDEN_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD,
                Items.WOODEN_AXE, Items.STONE_AXE, Items.GOLDEN_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE
        );
        tag(Tags.Items.TOOLS)
                .addTags(ItemTags.AXES, ItemTags.HOES, ItemTags.PICKAXES, ItemTags.SHOVELS, ItemTags.SWORDS)
                .addTags(Tags.Items.TOOLS_BOW, Tags.Items.TOOLS_BRUSH, Tags.Items.TOOLS_CROSSBOW, Tags.Items.TOOLS_FISHING_ROD, Tags.Items.TOOLS_SHEAR, Tags.Items.TOOLS_IGNITER, Tags.Items.TOOLS_SHIELD, Tags.Items.TOOLS_SPEAR, Tags.Items.TOOLS_MACE, Tags.Items.MINING_TOOL_TOOLS, Tags.Items.MELEE_WEAPON_TOOLS, Tags.Items.RANGED_WEAPON_TOOLS);
        tag(Tags.Items.ARMORS).addTags(ItemTags.HEAD_ARMOR, ItemTags.CHEST_ARMOR, ItemTags.LEG_ARMOR, ItemTags.FOOT_ARMOR);
        tag(Tags.Items.ENCHANTABLES).addTags(ItemTags.ARMOR_ENCHANTABLE, ItemTags.EQUIPPABLE_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.SWORD_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE, ItemTags.FISHING_ENCHANTABLE, ItemTags.TRIDENT_ENCHANTABLE, ItemTags.BOW_ENCHANTABLE, ItemTags.CROSSBOW_ENCHANTABLE, ItemTags.MACE_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.VANISHING_ENCHANTABLE);

        // Backwards compat with pre-1.21 tags. Done after so optional tag is last for better readability.
        // TODO: Remove backwards compat tag entries in 1.22
        tagWithOptionalLegacy(Tags.Items.BONES);
        tag(Tags.Items.BRICKS_NORMAL).addOptionalTag(forgeRl("ingots/brick"));
        tag(Tags.Items.BRICKS_NETHER).addOptionalTag(forgeRl("ingots/nether_brick"));
        tagWithOptionalLegacy(Tags.Items.CROPS);
        tagWithOptionalLegacy(Tags.Items.CROPS_BEETROOT);
        tagWithOptionalLegacy(Tags.Items.CROPS_CARROT);
        tagWithOptionalLegacy(Tags.Items.CROPS_NETHER_WART);
        tagWithOptionalLegacy(Tags.Items.CROPS_POTATO);
        tagWithOptionalLegacy(Tags.Items.CROPS_WHEAT);
        tagWithOptionalLegacy(Tags.Items.DUSTS);
        tagWithOptionalLegacy(Tags.Items.DUSTS_GLOWSTONE);
        tagWithOptionalLegacy(Tags.Items.DUSTS_REDSTONE);
        tagColoredWithOptionalLegacy(Tags.Items.DYES);
        tag(Tags.Items.DYED_BLACK)
                .addOptionalTag(forgeRl("glass/black"))
                .addOptionalTag(forgeRl("stained_glass/black"));
        tag(Tags.Items.DYED_BLUE)
                .addOptionalTag(forgeRl("glass/blue"))
                .addOptionalTag(forgeRl("stained_glass/blue"));
        tag(Tags.Items.DYED_BROWN)
                .addOptionalTag(forgeRl("glass/brown"))
                .addOptionalTag(forgeRl("stained_glass/brown"));
        tag(Tags.Items.DYED_CYAN)
                .addOptionalTag(forgeRl("glass/cyan"))
                .addOptionalTag(forgeRl("stained_glass/cyan"));
        tag(Tags.Items.DYED_GRAY)
                .addOptionalTag(forgeRl("glass/gray"))
                .addOptionalTag(forgeRl("stained_glass/gray"));
        tag(Tags.Items.DYED_GREEN)
                .addOptionalTag(forgeRl("glass/green"))
                .addOptionalTag(forgeRl("stained_glass/green"));
        tag(Tags.Items.DYED_LIGHT_BLUE)
                .addOptionalTag(forgeRl("glass/light_blue"))
                .addOptionalTag(forgeRl("stained_glass/light_blue"));
        tag(Tags.Items.DYED_LIGHT_GRAY)
                .addOptionalTag(forgeRl("glass/light_gray"))
                .addOptionalTag(forgeRl("stained_glass/light_gray"));
        tag(Tags.Items.DYED_LIME)
                .addOptionalTag(forgeRl("glass/lime"))
                .addOptionalTag(forgeRl("stained_glass/lime"));
        tag(Tags.Items.DYED_MAGENTA)
                .addOptionalTag(forgeRl("glass/magenta"))
                .addOptionalTag(forgeRl("stained_glass/magenta"));
        tag(Tags.Items.DYED_MAGENTA)
                .addOptionalTag(forgeRl("glass/magenta"))
                .addOptionalTag(forgeRl("stained_glass/magenta"));
        tag(Tags.Items.DYED_ORANGE)
                .addOptionalTag(forgeRl("glass/orange"))
                .addOptionalTag(forgeRl("stained_glass/orange"));
        tag(Tags.Items.DYED_PINK)
                .addOptionalTag(forgeRl("glass/pink"))
                .addOptionalTag(forgeRl("stained_glass/pink"));
        tag(Tags.Items.DYED_PURPLE)
                .addOptionalTag(forgeRl("glass/purple"))
                .addOptionalTag(forgeRl("stained_glass/purple"));
        tag(Tags.Items.DYED_RED)
                .addOptionalTag(forgeRl("glass/red"))
                .addOptionalTag(forgeRl("stained_glass/red"));
        tag(Tags.Items.DYED_WHITE)
                .addOptionalTag(forgeRl("glass/white"))
                .addOptionalTag(forgeRl("stained_glass/white"));
        tag(Tags.Items.DYED_YELLOW)
                .addOptionalTag(forgeRl("glass/yellow"))
                .addOptionalTag(forgeRl("stained_glass/yellow"));
        tagWithOptionalLegacy(Tags.Items.ENDER_PEARLS);
        tagWithOptionalLegacy(Tags.Items.FEATHERS);
        tagWithOptionalLegacy(Tags.Items.GEMS);
        tagWithOptionalLegacy(Tags.Items.GEMS_AMETHYST);
        tagWithOptionalLegacy(Tags.Items.GEMS_DIAMOND);
        tagWithOptionalLegacy(Tags.Items.GEMS_EMERALD);
        tagWithOptionalLegacy(Tags.Items.GEMS_LAPIS);
        tagWithOptionalLegacy(Tags.Items.GEMS_PRISMARINE);
        tagWithOptionalLegacy(Tags.Items.GEMS_QUARTZ);
        tag(Tags.Items.GUNPOWDERS).addOptionalTag(forgeRl("gunpowder"));
        tagWithOptionalLegacy(Tags.Items.INGOTS);
        tagWithOptionalLegacy(Tags.Items.INGOTS_COPPER);
        tagWithOptionalLegacy(Tags.Items.INGOTS_GOLD);
        tagWithOptionalLegacy(Tags.Items.INGOTS_IRON);
        tagWithOptionalLegacy(Tags.Items.INGOTS_NETHERITE);
        tag(Tags.Items.LEATHERS).addOptionalTag(forgeRl("leather"));
        tagWithOptionalLegacy(Tags.Items.MUSHROOMS);
        tagWithOptionalLegacy(Tags.Items.NETHER_STARS);
        tagWithOptionalLegacy(Tags.Items.NUGGETS);
        tagWithOptionalLegacy(Tags.Items.NUGGETS_IRON);
        tagWithOptionalLegacy(Tags.Items.NUGGETS_GOLD);
        tagWithOptionalLegacy(Tags.Items.RAW_MATERIALS);
        tagWithOptionalLegacy(Tags.Items.RAW_MATERIALS_COPPER);
        tagWithOptionalLegacy(Tags.Items.RAW_MATERIALS_GOLD);
        tagWithOptionalLegacy(Tags.Items.RAW_MATERIALS_IRON);
        tagWithOptionalLegacy(Tags.Items.RODS);
        tagWithOptionalLegacy(Tags.Items.RODS_BLAZE);
        tagWithOptionalLegacy(Tags.Items.RODS_WOODEN);
        tagWithOptionalLegacy(Tags.Items.SEEDS);
        tagWithOptionalLegacy(Tags.Items.SEEDS_BEETROOT);
        tagWithOptionalLegacy(Tags.Items.SEEDS_MELON);
        tagWithOptionalLegacy(Tags.Items.SEEDS_PUMPKIN);
        tagWithOptionalLegacy(Tags.Items.SEEDS_WHEAT);
        tag(Tags.Items.SLIME_BALLS).addOptionalTag(forgeRl("slimeballs"));
        tagWithOptionalLegacy(Tags.Items.STRINGS);
        tagWithOptionalLegacy(Tags.Items.TOOLS);
        tagWithOptionalLegacy(Tags.Items.ARMORS);
        tag(Tags.Items.TOOLS_SHIELD).addOptionalTag(forgeRl("tools/shields"));
        tag(Tags.Items.TOOLS_BOW).addOptionalTag(forgeRl("tools/bows"));
        tag(Tags.Items.TOOLS_BRUSH).addOptionalTag(forgeRl("tools/brushes"));
        tag(Tags.Items.TOOLS_CROSSBOW).addOptionalTag(forgeRl("tools/crossbows"));
        tag(Tags.Items.TOOLS_FISHING_ROD).addOptionalTag(forgeRl("tools/fishing_rods"));
        tag(Tags.Items.TOOLS_SHEAR).addOptionalTag(forgeRl("shears")); // yes, it's forge:shears not forge:tools/shears
        tag(Tags.Items.TOOLS_SPEAR).addOptionalTag(forgeRl("tools/tridents"));
    }

    /**
     * Shorthand for {@code tag(tag).addOptionalTag(forgeRl(tag.location().getPath()))}
     */
    private IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tagWithOptionalLegacy(TagKey<Item> tag) {
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tagAppender = tag(tag);
        tagAppender.addOptionalTag(forgeRl(tag.location().getPath()));
        return tagAppender;
    }

    private void tagColoredWithOptionalLegacy(TagKey<Item> group) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color : DyeColor.values()) {
            TagKey<Item> tag = getForgeItemTag(prefix + color.getName());
            tagWithOptionalLegacy(tag);
        }
    }

    private void addColored(TagKey<Item> group, String pattern) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color : DyeColor.values()) {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath("minecraft", pattern.replace("{color}", color.getName()));
            TagKey<Item> tag = getForgeItemTag(prefix + color.getName());
            Item item = BuiltInRegistries.ITEM.get(key);
            if (item == null || item == Items.AIR)
                throw new IllegalStateException("Unknown vanilla item: " + key);
            tag(tag).add(item);
        }
    }

    private void addColoredTags(Consumer<TagKey<Item>> consumer, TagKey<Item> group) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color : DyeColor.values()) {
            TagKey<Item> tag = getForgeItemTag(prefix + color.getName());
            consumer.accept(tag);
        }
    }

    @SuppressWarnings("unchecked")
    private TagKey<Block> getForgeBlockTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
        }
    }

    @SuppressWarnings("unchecked")
    private TagKey<Item> getForgeItemTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey<Item>) Tags.Items.class.getDeclaredField(name).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(Tags.Items.class.getName() + " is missing tag name: " + name);
        }
    }

    private static ResourceLocation forgeRl(String path) {
        return ResourceLocation.fromNamespaceAndPath("forge", path);
    }

    @Override
    public String getName() {
        return "Forge Item Tags";
    }
}
