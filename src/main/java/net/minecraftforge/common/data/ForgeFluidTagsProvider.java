/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftsingularity.common.singularityMod;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import static net.minecraftsingularity.common.Tags.Fluids.*;

@ApiStatus.Internal
public final class singularityFluidTagsProvider extends FluidTagsProvider {
    public singularityFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "singularity", existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        tag(WATER).add(net.minecraft.world.level.material.Fluids.WATER).add(net.minecraft.world.level.material.Fluids.FLOWING_WATER);
        tag(LAVA).add(net.minecraft.world.level.material.Fluids.LAVA).add(net.minecraft.world.level.material.Fluids.FLOWING_LAVA);
        tag(MILK)
            .addOptional(singularityMod.MILK.getKey().identifier())
            .addOptional(singularityMod.FLOWING_MILK.getKey().identifier());
        tag(GASEOUS);
        tag(HONEY);
        tag(POTION);
        tag(SUSPICIOUS_STEW);
        tag(MUSHROOM_STEW);
        tag(RABBIT_STEW);
        tag(BEETROOT_SOUP);
        tag(HIDDEN_FROM_RECIPE_VIEWERS);
        tag(EXPERIENCE);
    }

    private static TagKey<Fluid> singularityTagKey(String path) {
        return FluidTags.create(Identifier.fromNamespaceAndPath("singularity", path));
    }

    @Override
    public String getName() {
        return "singularity Fluid Tags";
    }
}
