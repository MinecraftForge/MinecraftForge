/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Wrapper for {@link VertexConsumer} which delegates all operations to its parent.
 * <p>
 * Useful for defining custom pipeline elements that only process certain data.
 */
public abstract class VertexConsumerWrapper implements VertexConsumer
{
    private final VertexConsumer parent;

    public VertexConsumerWrapper(VertexConsumer parent)
    {
        this.parent = parent;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z)
    {
        return parent.vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a)
    {
        return parent.color(r, g, b, a);
    }

    @Override
    public VertexConsumer uv(float u, float v)
    {
        return parent.uv(u, v);
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v)
    {
        return parent.overlayCoords(u, v);
    }

    @Override
    public VertexConsumer uv2(int u, int v)
    {
        return parent.uv2(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z)
    {
        return parent.normal(x, y, z);
    }

    @Override
    public void endVertex()
    {
        parent.endVertex();
    }

    @Override
    public void defaultColor(int r, int g, int b, int a)
    {
        parent.defaultColor(r, g, b, a);
    }

    @Override
    public void unsetDefaultColor()
    {
        parent.unsetDefaultColor();
    }
}
