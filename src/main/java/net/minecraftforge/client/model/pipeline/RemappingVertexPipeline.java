/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

/**
 * Vertex pipeline element that remaps incoming data to another format.
 */
public class RemappingVertexPipeline implements VertexConsumer
{
    private final VertexConsumer parent;
    private final VertexFormat targetFormat;

    private final Vector3d position = new Vector3d(0, 0, 0);
    private final Vector3f normal = new Vector3f(0, 0, 0);
    private final int[] color = new int[] { 255, 255, 255, 255 };
    private final float[] uv0 = new float[] { 0, 0 };
    private final int[] uv1 = new int[] { 0, 0 };
    private final int[] uv2 = new int[] { 0, 0 };

    public RemappingVertexPipeline(VertexConsumer parent, VertexFormat targetFormat)
    {
        this.parent = parent;
        this.targetFormat = targetFormat;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z)
    {
        position.set(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z)
    {
        normal.set(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a)
    {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v)
    {
        uv0[0] = u;
        uv0[1] = v;
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v)
    {
        uv1[0] = u;
        uv1[1] = v;
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v)
    {
        uv2[0] = u;
        uv2[1] = v;
        return this;
    }

    @Override
    public void endVertex()
    {
        for (var element : targetFormat.getElements())
        {
            switch (element.getUsage())
            {
                case POSITION -> parent.vertex(position.x, position.y, position.z);
                case NORMAL -> parent.normal(normal.x(), normal.y(), normal.z());
                case COLOR -> parent.color(color[0], color[1], color[2], color[3]);
                case UV ->
                {
                    switch (element.getIndex())
                    {
                        case 0 -> parent.uv(uv0[0], uv0[1]);
                        case 1 -> parent.overlayCoords(uv1[0], uv1[1]);
                        case 2 -> parent.uv2(uv2[0], uv2[1]);
                    }
                }
            }
        }
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
