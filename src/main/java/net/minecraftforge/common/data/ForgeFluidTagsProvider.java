/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.world.level.material.FluidIds;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import static net.minecraftforge.common.Tags.Fluids.*;

@ApiStatus.Internal
public final class ForgeFluidTagsProvider extends FluidTagsProvider {
    public ForgeFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        tag(WATER)
            .add(
                FluidIds.WATER,
                FluidIds.FLOWING_WATER
            );
        tag(LAVA)
            .add(
                FluidIds.LAVA,
                FluidIds.FLOWING_LAVA
            );
        tag(MILK)
            .addOptional(ForgeMod.MILK.getKey().identifier())
            .addOptional(ForgeMod.FLOWING_MILK.getKey().identifier());
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

    @Override
    public String getName() {
        return "Forge Fluid Tags";
    }
}
