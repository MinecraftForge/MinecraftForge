/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

import static net.minecraftforge.common.Tags.EntityTypes.*;

@ApiStatus.Internal
public final class ForgeEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ForgeEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        tag(BOSSES).add(EntityType.ENDER_DRAGON, EntityType.WITHER);
        tag(MINECARTS).add(EntityType.MINECART, EntityType.CHEST_MINECART, EntityType.FURNACE_MINECART, EntityType.HOPPER_MINECART, EntityType.SPAWNER_MINECART, EntityType.TNT_MINECART, EntityType.COMMAND_BLOCK_MINECART);
        tag(BOATS).add(EntityType.BOAT, EntityType.CHEST_BOAT);
        tag(CAPTURING_NOT_SUPPORTED);
        tag(TELEPORTING_NOT_SUPPORTED);

        // Backwards compat with pre-1.21 tags. Done after so optional tag is last for better readability.
        // TODO: Remove backwards compat tag entries in 1.22
        tag(BOSSES).addOptionalTag(forgeRl("bosses"));
        tag(MINECARTS).addOptionalTag(forgeRl("minecarts"));
        tag(BOATS).addOptionalTag(forgeRl("boats"));
    }

    private ResourceLocation forgeRl(String path) {
        return ResourceLocation.fromNamespaceAndPath("forge", path);
    }

    @Override
    public String getName() {
        return "Forge EntityType Tags";
    }
}
