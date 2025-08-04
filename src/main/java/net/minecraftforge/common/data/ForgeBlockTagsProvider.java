/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

// We typically don't do static imports as S2S can't remap them {as they are not qualified}, however this conflicts with vanilla and our tag class names, and our tags don't get obfed so its one line of warning.
import static net.minecraftforge.common.Tags.Blocks.*;

@ApiStatus.Internal
public final class ForgeBlockTagsProvider extends VanillaBlockTagsProvider {
    public ForgeBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider p_256380_) {
        (new ForgeBlockItemTagsProvider() {
            @Override
            protected TagAppender<Block, Block> tag(TagKey<Block> p_406922_, TagKey<Item> p_408417_) {
                return ForgeBlockTagsProvider.this.tag(p_406922_);
            }
        }).run();
        tag(CHORUS_ADDITIONALLY_GROWS_ON)
            .addTag(END_STONES);
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
        addColored(DYED, "{color}_wall_banner");
        addColored(DYED, "{color}_wool");
        addColoredTags(tag(DYED)::addTag, DYED);
        tag(ENDERMAN_PLACE_ON_BLACKLIST); // forge:enderman_place_on_blacklist
        tag(SKULLS).add(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD);
        tag(HIDDEN_FROM_RECIPE_VIEWERS);
        tag(RELOCATION_NOT_SUPPORTED);
        tag(VILLAGER_JOB_SITES).add(
                Blocks.BARREL, Blocks.BLAST_FURNACE, Blocks.BREWING_STAND, Blocks.CARTOGRAPHY_TABLE,
                Blocks.CAULDRON, Blocks.WATER_CAULDRON, Blocks.LAVA_CAULDRON, Blocks.POWDER_SNOW_CAULDRON,
                Blocks.COMPOSTER, Blocks.FLETCHING_TABLE, Blocks.GRINDSTONE, Blocks.LECTERN,
                Blocks.LOOM, Blocks.SMITHING_TABLE, Blocks.SMOKER, Blocks.STONECUTTER);
    }

    private void addColored(TagKey<Block> group, String pattern) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (var color : DyeColor.values()) {
            var key = ResourceLocation.fromNamespaceAndPath("minecraft", pattern.replace("{color}", color.getName()));
            TagKey<Block> tag = getForgeTag(prefix + color.getName());
            var block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block == Blocks.AIR)
                throw new IllegalStateException("Unknown vanilla block: " + key);
            tag(tag).add(block);
        }
    }

    private static void addColoredTags(Consumer<TagKey<Block>> consumer, TagKey<Block> group) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (var color : DyeColor.values()) {
            TagKey<Block> tag = getForgeTag(prefix + color.getName());
            consumer.accept(tag);
        }
    }

    @SuppressWarnings("unchecked")
    private static TagKey<Block> getForgeTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
        }
    }

    private static TagKey<Block> forgeTagKey(String path) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", path));
    }

    @Override
    public String getName() {
        return "Forge Block Tags";
    }
}
