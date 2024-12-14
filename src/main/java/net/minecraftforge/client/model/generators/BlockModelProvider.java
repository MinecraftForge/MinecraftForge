/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Stub class to extend for block model data providers, eliminates some
 * boilerplate constructor parameters.
 *
 * In 1.21.4 Mojang exposed their data generators for their models. So it should be feasible to just use theirs.
 * If you find something lacking feel free to open a PR so that we can extend it.
 * @deprecated Use Vanilla's providers {@link net.minecraft.client.data.models.ModelProvider}
 */
@Deprecated(since = "1.21.4", forRemoval = true)
public abstract class BlockModelProvider extends ModelProvider<BlockModelBuilder> {

    public BlockModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, BLOCK_FOLDER, BlockModelBuilder::new, existingFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return "Block Models: " + modid;
    }
}
