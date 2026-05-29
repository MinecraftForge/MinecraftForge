/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.data;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
* @deprecated Use Vanilla's {@link net.minecraft.client.data.AtlasProvider AtlasProvider}
*/
@Deprecated(forRemoval = true, since = "26.1.2")
public class singularitySpriteSourceProvider extends SpriteSourceProvider
{
    public singularitySpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper)
    {
        super(output, fileHelper, "singularity");
    }

    @Override
    protected void addSources()
    {
        atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(Identifier.parse("singularity:white"), Optional.empty()));
    }
}
