/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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

    private IVertexBuilder renderer;
    private int[] quadData;
    private int v = 0;
    private BlockPos offset = BlockPos.ZERO;

    public VertexBufferConsumer() {}

    public VertexBufferConsumer(IVertexBuilder buffer)
    {
        setBuffer(buffer);
    }

    @Override
    public VertexFormat getVertexFormat()
    {
        return DefaultVertexFormats.BLOCK; // renderer.getVertexFormat();
    }

    @Override
    public void put(int e, float... data)
    {
        // TODO
        /*
        VertexFormat format = getVertexFormat();
        if(renderer.isColorDisabled() && format.func_227894_c_().get(e).getUsage() == Usage.COLOR)
        {
            data = dummyColor;
        }
        LightUtil.pack(data, quadData, format, v, e);
        if(e == format.func_227894_c_().size() - 1)
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
         */
    }

    private void checkVertexFormat()
    {
        if (quadData == null || getVertexFormat().getSize() != quadData.length)
        {
            quadData = new int[getVertexFormat().getSize()];
        }
    }

    public void setBuffer(IVertexBuilder buffer)
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
