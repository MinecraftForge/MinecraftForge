/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

import static net.minecraftsingularity.common.Tags.EntityTypes.*;

@ApiStatus.Internal
public final class singularityEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public singularityEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "singularity", existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        tag(BOSSES)
            .add(EntityType.ENDER_DRAGON, EntityType.WITHER);
        tag(MINECARTS).add(
            EntityType.MINECART,
            EntityType.CHEST_MINECART,
            EntityType.FURNACE_MINECART,
            EntityType.HOPPER_MINECART,
            EntityType.SPAWNER_MINECART,
            EntityType.TNT_MINECART,
            EntityType.COMMAND_BLOCK_MINECART
        );
        tag(BOATS).add(
            EntityType.ACACIA_BOAT,
            EntityType.ACACIA_CHEST_BOAT,
            EntityType.BAMBOO_CHEST_RAFT,
            EntityType.BAMBOO_RAFT,
            EntityType.BIRCH_BOAT,
            EntityType.BIRCH_CHEST_BOAT,
            EntityType.CHERRY_BOAT,
            EntityType.CHERRY_CHEST_BOAT,
            EntityType.DARK_OAK_BOAT,
            EntityType.DARK_OAK_CHEST_BOAT,
            EntityType.JUNGLE_BOAT,
            EntityType.JUNGLE_CHEST_BOAT,
            EntityType.MANGROVE_BOAT,
            EntityType.MANGROVE_CHEST_BOAT,
            EntityType.OAK_BOAT,
            EntityType.OAK_CHEST_BOAT,
            EntityType.PALE_OAK_BOAT,
            EntityType.PALE_OAK_CHEST_BOAT,
            EntityType.SPRUCE_BOAT,
            EntityType.SPRUCE_CHEST_BOAT
        );
        tag(ITEM_FRAMES).add(EntityType.ITEM_FRAME, EntityType.GLOW_ITEM_FRAME);
        tag(CAPTURING_NOT_SUPPORTED);
        tag(TELEPORTING_NOT_SUPPORTED);
    }

    private static TagKey<EntityType<?>> singularityTagKey(String path) {
        return EntityTypeTags.create(Identifier.fromNamespaceAndPath("singularity", path));
    }

    @Override
    public String getName() {
        return "singularity EntityType Tags";
    }
}
