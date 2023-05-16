/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ForgeEntityTypeTagsProvider extends EntityTypeTagsProvider
{

    public ForgeEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider lookupProvider)
    {
        tag(Tags.EntityTypes.BOSSES).add(EntityType.ENDER_DRAGON, EntityType.WITHER);
    }

    @Override
    public String getName()
    {
        return "Forge EntityType Tags";
    }
}
