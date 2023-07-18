/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags.Fluids;

import java.util.concurrent.CompletableFuture;

public final class ForgeFluidTagsProvider extends FluidTagsProvider
{
    public ForgeFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider lookupProvider)
    {
        tag(Fluids.MILK).addOptional(ForgeMod.MILK.getId()).addOptional(ForgeMod.FLOWING_MILK.getId());
    }

    @Override
    public String getName()
    {
        return "Forge Fluid Tags";
    }
}
