/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.core.Direction;

/**
 * Assumes that the data length is not less than e.getElements().size().
 * Also assumes that element index passed will increment from 0 to format.getElements().size() - 1.
 * Normal, Color and UV are assumed to be in 0-1 range.
 */
public interface IVertexConsumer
{
    /**
     * @return the format that should be used for passed data.
     */
    VertexFormat getVertexFormat();

    void setQuadTint(int tint);
    void setQuadOrientation(Direction orientation);
    void setApplyDiffuseLighting(boolean diffuse);
    void setTexture(TextureAtlasSprite texture);
    void put(int element, float... data);
}
