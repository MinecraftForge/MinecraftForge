/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypeIds;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

import static net.minecraftforge.common.Tags.EntityTypes.*;

@ApiStatus.Internal
public final class ForgeEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ForgeEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        tag(BOSSES)
            .add(EntityTypeIds.ENDER_DRAGON, EntityTypeIds.WITHER);
        tag(MINECARTS).add(
            EntityTypeIds.MINECART,
            EntityTypeIds.CHEST_MINECART,
            EntityTypeIds.FURNACE_MINECART,
            EntityTypeIds.HOPPER_MINECART,
            EntityTypeIds.SPAWNER_MINECART,
            EntityTypeIds.TNT_MINECART,
            EntityTypeIds.COMMAND_BLOCK_MINECART
        );
        tag(BOATS).add(
            EntityTypeIds.ACACIA_BOAT,
            EntityTypeIds.ACACIA_CHEST_BOAT,
            EntityTypeIds.BAMBOO_CHEST_RAFT,
            EntityTypeIds.BAMBOO_RAFT,
            EntityTypeIds.BIRCH_BOAT,
            EntityTypeIds.BIRCH_CHEST_BOAT,
            EntityTypeIds.CHERRY_BOAT,
            EntityTypeIds.CHERRY_CHEST_BOAT,
            EntityTypeIds.DARK_OAK_BOAT,
            EntityTypeIds.DARK_OAK_CHEST_BOAT,
            EntityTypeIds.JUNGLE_BOAT,
            EntityTypeIds.JUNGLE_CHEST_BOAT,
            EntityTypeIds.MANGROVE_BOAT,
            EntityTypeIds.MANGROVE_CHEST_BOAT,
            EntityTypeIds.OAK_BOAT,
            EntityTypeIds.OAK_CHEST_BOAT,
            EntityTypeIds.PALE_OAK_BOAT,
            EntityTypeIds.PALE_OAK_CHEST_BOAT,
            EntityTypeIds.SPRUCE_BOAT,
            EntityTypeIds.SPRUCE_CHEST_BOAT
        );
        tag(ITEM_FRAMES).add(EntityTypeIds.ITEM_FRAME, EntityTypeIds.GLOW_ITEM_FRAME);
        tag(CAPTURING_NOT_SUPPORTED);
        tag(TELEPORTING_NOT_SUPPORTED);
    }

    @SuppressWarnings("unused")
    private static TagKey<EntityType<?>> forgeTagKey(String path) {
        return EntityTypeTags.create(Identifier.fromNamespaceAndPath("forge", path));
    }

    @Override
    public String getName() {
        return "Forge EntityType Tags";
    }
}
