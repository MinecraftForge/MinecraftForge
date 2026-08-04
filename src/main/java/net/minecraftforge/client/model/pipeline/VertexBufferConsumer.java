/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement.Usage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;

/**
 * Assumes VertexFormatElement is present in the BufferBuilder's vertex format.
 */
public class VertexBufferConsumer implements IVertexConsumer
{
    private static final float[] dummyColor = new float[]{ 1, 1, 1, 1 };

    private BufferBuilder renderer;
    private int[] quadData;
    private int v = 0;
    private BlockPos offset = BlockPos.ZERO;

    public VertexBufferConsumer() {}

    public VertexBufferConsumer(BufferBuilder buffer)
    {
        setBuffer(buffer);
    }

    @Override
    public VertexFormat getVertexFormat()
    {
        return renderer.getVertexFormat();
    }

    @Override
    public void put(int e, float... data)
    {
        VertexFormat format = getVertexFormat();
        if(renderer.isColorDisabled() && format.getElement(e).getUsage() == Usage.COLOR)
        {
            data = dummyColor;
        }
        LightUtil.pack(data, quadData, format, v, e);
        if(e == format.getElementCount() - 1)
        {
            v++;
            if(v == 4)
            {
                renderer.addVertexData(quadData);
                renderer.putPosition(offset.getX(), offset.getY(), offset.getZ());
                //Arrays.fill(quadData, 0);
                v = 0;
            }
        }
    }

    private void checkVertexFormat()
    {
        if (quadData == null || renderer.getVertexFormat().getSize() != quadData.length)
        {
            quadData = new int[renderer.getVertexFormat().getSize()];
        }
    }

    public void setBuffer(BufferBuilder buffer)
    {
        this.renderer = buffer;
        checkVertexFormat();
    }

    public void setOffset(BlockPos offset)
    {
        this.offset = new BlockPos(offset);
    }

    @Override
    public void setQuadTint(int tint) {}
    @Override
    public void setQuadOrientation(Direction orientation) {}
    @Override
    public void setApplyDiffuseLighting(boolean diffuse) {}
    @Override
    public void setTexture(TextureAtlasSprite texture ) {}
}
