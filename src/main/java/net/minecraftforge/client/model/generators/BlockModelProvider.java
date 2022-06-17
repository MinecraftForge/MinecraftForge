/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Stub class to extend for block model data providers, eliminates some
 * boilerplate constructor parameters.
 */
public abstract class BlockModelProvider extends ModelProvider<BlockModelBuilder> {

    public BlockModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, BLOCK_FOLDER, BlockModelBuilder::new, existingFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return "Block Models: " + modid;
    }
}
