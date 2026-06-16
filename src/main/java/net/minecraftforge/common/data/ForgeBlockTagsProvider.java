/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.minecraft.references.BlockIds.*;
import static net.minecraft.references.BlockItemIds.*;
import static net.minecraftforge.common.Tags.Blocks.*;

@ApiStatus.Internal
public final class ForgeBlockTagsProvider extends VanillaBlockTagsProvider {
    public ForgeBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider p_256380_)  {
        new ForgeBlockItemTagsProvider(tagId -> WrappedCombinedAppender.block(this.tag(tagId.block()))).run();
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
        tag(SKULLS)
            .add( // Has Items
                SKELETON_SKULL,
                WITHER_SKELETON_SKULL,
                PLAYER_HEAD,
                ZOMBIE_HEAD,
                CREEPER_HEAD,
                PIGLIN_HEAD,
                DRAGON_HEAD
            )
            .add( // Doesn't have items
                SKELETON_WALL_SKULL,
                WITHER_SKELETON_WALL_SKULL,
                PLAYER_WALL_HEAD,
                ZOMBIE_WALL_HEAD,
                CREEPER_WALL_HEAD,
                PIGLIN_WALL_HEAD,
                DRAGON_WALL_HEAD
            );
        tag(HIDDEN_FROM_RECIPE_VIEWERS);
        tag(RELOCATION_NOT_SUPPORTED);
        tag(VILLAGER_JOB_SITES)
            .add( // Has items
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
            )
            .add( // Doesn't have items
                WATER_CAULDRON,
                LAVA_CAULDRON,
                POWDER_SNOW_CAULDRON
            );
    }

    private void addColored(TagKey<Block> group, String pattern) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (var color : DyeColor.values()) {
            var key = Identifier.withDefaultNamespace(pattern.replace("{color}", color.getName()));
            TagKey<Block> tag = getTag(prefix + color.getName());
            var block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block == Blocks.AIR)
                throw new IllegalStateException("Unknown vanilla block: " + key);
            tag(tag)
                .add(ResourceKey.create(Registries.BLOCK, key));
        }
    }

    private static void addColoredTags(Consumer<TagKey<Block>> consumer, TagKey<Block> group) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (var color : DyeColor.values()) {
            TagKey<Block> tag = getTag(prefix + color.getName());
            consumer.accept(tag);
        }
    }

    @SuppressWarnings("unchecked")
    private static TagKey<Block> getTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey<Block>)Tags.Blocks.class.getDeclaredField(name).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
        }
    }

    @SuppressWarnings("unused")
    private static TagKey<Block> forgeTagKey(String path) {
        return BlockTags.create(Identifier.fromNamespaceAndPath("forge", path));
    }

    @Override
    public String getName() {
        return "Forge Block Tags";
    }
}
