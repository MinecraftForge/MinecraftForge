/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Builder for block models, does not currently provide any additional
 * functionality over {@link ModelBuilder}, purely a stub class with a concrete
 * generic.
 *
 * @see ModelProvider
 * @see ModelBuilder
 *
 * In 1.21.4 Mojang exposed their data generators for their models. So it should be feasible to just use theirs.
 * If you find something lacking feel free to open a PR so that we can extend it.
 * @deprecated Use Vanilla's providers {@link net.minecraft.client.data.models.ModelProvider}
 */
@Deprecated(since = "1.21.4", forRemoval = true)
public class BlockModelBuilder extends ModelBuilder<BlockModelBuilder>
{
    public BlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper)
    {
        super(outputLocation, existingFileHelper);
    }

    @Override
    public JsonObject toJson()
    {
        return super.toJson();
    }
}
