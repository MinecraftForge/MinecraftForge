/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Assumes VertexFormatElement is present in the BufferBuilder's vertex format.
 */
public class VertexBufferConsumer implements IVertexConsumer
{
    private static final float[] dummyColor = new float[]{ 1, 1, 1, 1 };

    private BufferBuilder renderer;
    private int[] quadData;
    private int v = 0;
    private BlockPos offset = BlockPos.ORIGIN;

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
        if(renderer.isColorDisabled() && format.getElement(e).getUsage() == EnumUsage.COLOR)
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
        if (quadData == null || renderer.getVertexFormat().getNextOffset() != quadData.length)
        {
            quadData = new int[renderer.getVertexFormat().getNextOffset()];
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
    public void setQuadOrientation(EnumFacing orientation) {}
    @Override
    public void setApplyDiffuseLighting(boolean diffuse) {}
    @Override
    public void setTexture(TextureAtlasSprite texture ) {}
}
