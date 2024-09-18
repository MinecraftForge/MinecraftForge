/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

import static net.minecraftforge.common.Tags.Fluids.*;

@ApiStatus.Internal
public final class ForgeFluidTagsProvider extends FluidTagsProvider {
    public ForgeFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        tag(WATER).add(net.minecraft.world.level.material.Fluids.WATER).add(net.minecraft.world.level.material.Fluids.FLOWING_WATER);
        tag(LAVA).add(net.minecraft.world.level.material.Fluids.LAVA).add(net.minecraft.world.level.material.Fluids.FLOWING_LAVA);
        tag(MILK).addOptional(ForgeMod.MILK.getId()).addOptional(ForgeMod.FLOWING_MILK.getId());
        tag(GASEOUS);
        tag(HONEY);
        tag(POTION);
        tag(SUSPICIOUS_STEW);
        tag(MUSHROOM_STEW);
        tag(RABBIT_STEW);
        tag(BEETROOT_SOUP);
        tag(HIDDEN_FROM_RECIPE_VIEWERS);

        // Backwards compat with pre-1.21 tags. Done after so optional tag is last for better readability.
        // TODO: Remove backwards compat tag entries in 1.22
        tagWithOptionalLegacy(MILK);
        tagWithOptionalLegacy(HONEY);
    }

    private IntrinsicHolderTagsProvider.IntrinsicTagAppender<Fluid> tagWithOptionalLegacy(TagKey<Fluid> tag) {
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Fluid> tagAppender = tag(tag);
        tagAppender.addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", tag.location().getPath()));
        return tagAppender;
    }

    @Override
    public String getName() {
        return "Forge Fluid Tags";
    }
}
