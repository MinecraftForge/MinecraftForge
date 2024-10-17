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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
        tag(GASEOUS).addOptionalTag(forgeTagKey("gaseous"));
        tag(HONEY);
        tag(EXPERIENCE);
        tag(POTION).addOptionalTag(forgeTagKey("potion"));
        tag(SUSPICIOUS_STEW).addOptionalTag(forgeTagKey("suspicious_stew"));
        tag(MUSHROOM_STEW).addOptionalTag(forgeTagKey("mushroom_stew"));
        tag(RABBIT_STEW).addOptionalTag(forgeTagKey("rabbit_stew"));
        tag(BEETROOT_SOUP).addOptionalTag(forgeTagKey("beetroot_soup"));
        tag(HIDDEN_FROM_RECIPE_VIEWERS);

        // Backwards compat definitions for pre-1.21 legacy `forge:` tags.
        // TODO: Remove backwards compat tag entries in 1.22
        tag(forgeTagKey("milk"))
                .addOptional(ForgeMod.MILK.getId())
                .addOptional(ForgeMod.FLOWING_MILK.getId());
        tag(forgeTagKey("gaseous"));
        tag(forgeTagKey("potion"));
        tag(forgeTagKey("suspicious_stew"));
        tag(forgeTagKey("mushroom_stew"));
        tag(forgeTagKey("rabbit_stew"));
        tag(forgeTagKey("beetroot_soup"));
    }

    private static TagKey<Fluid> forgeTagKey(String path) {
        return FluidTags.create(ResourceLocation.fromNamespaceAndPath("forge", path));
    }

    @Override
    public String getName() {
        return "Forge Fluid Tags";
    }
}
