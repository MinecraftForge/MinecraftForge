/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.VertexFormat;

public abstract class QuadGatheringTransformer implements IVertexConsumer
{
    protected IVertexConsumer parent;
    protected VertexFormat format;
    protected int vertices = 0;

    protected byte[] dataLength = null;
    protected float[][][] quadData = null;

    public void setParent(IVertexConsumer parent)
    {
        this.parent = parent;
    }

    public void setVertexFormat(VertexFormat format)
    {
        this.format = format;
        dataLength = new byte[format.getElements().size()];
        quadData = new float[format.getElements().size()][4][4];
    }

    @Override
    public VertexFormat getVertexFormat()
    {
        return format;
    }

    @Override
    public void put(int element, float... data)
    {
        System.arraycopy(data, 0, quadData[element][vertices], 0, data.length);
        if (vertices == 0)
        {
            dataLength[element] = (byte)data.length;
        }
        if (element == getVertexFormat().getElements().size() - 1)
        {
            vertices++;
        }
        if (vertices == 4)
        {
            vertices = 0;
            processQuad();
        }
    }

    protected abstract void processQuad();
}
