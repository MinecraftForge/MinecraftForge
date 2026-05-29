/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraftsingularity.common.Tags;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public final class singularityStructureTagsProvider extends StructureTagsProvider {
    public singularityStructureTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "singularity", existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        tag(Tags.Structures.HIDDEN_FROM_DISPLAYERS);
        tag(Tags.Structures.HIDDEN_FROM_LOCATOR_SELECTION);
    }

    @Override
    public String getName() {
        return "singularity Structure Tags";
    }
}
