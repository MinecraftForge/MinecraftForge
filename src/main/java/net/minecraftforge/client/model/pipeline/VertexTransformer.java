/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;

public class VertexTransformer implements IVertexConsumer
{
    protected final IVertexConsumer parent;

    public VertexTransformer(IVertexConsumer parent)
    {
        this.parent = parent;
    }

    @Override
    public VertexFormat getVertexFormat()
    {
        return parent.getVertexFormat();
    }

    @Override
    public void setQuadTint(int tint)
    {
        parent.setQuadTint(tint);
    }

    @Override
    public void setTexture(TextureAtlasSprite texture)
    {
        parent.setTexture(texture);
    }

    @Override
    public void setQuadOrientation(Direction orientation)
    {
        parent.setQuadOrientation(orientation);
    }

    @Override
    public void setApplyDiffuseLighting(boolean diffuse)
    {
        parent.setApplyDiffuseLighting(diffuse);
    }

    @Override
    public void put(int element, float... data)
    {
        parent.put(element, data);
    }
}
