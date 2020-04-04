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

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

/**
 * Assumes VertexFormatElement is present in the BufferBuilder's vertex format.
 */
public class VertexBufferConsumer implements IVertexConsumer
{
    private IVertexBuilder renderer;

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
    
    private int expandTextureCoord(final float c, final int max)
    {
        return (int) (MathHelper.clamp(c, 0, 1) * max);
    }

    @Override
    public void put(int e, float... data)
    {
        VertexFormat format = getVertexFormat();
        VertexFormatElement element = format.getElements().get(e);
        final float d0 = data.length <= 0 ? 0 : data[0];
        final float d1 = data.length <= 1 ? 0 : data[1];
        final float d2 = data.length <= 2 ? 0 : data[2];
        final float d3 = data.length <= 3 ? 0 : data[3];

        switch (element.getUsage())
        {
        case POSITION:
            renderer.pos(d0, d1, d2);
            break;
        case NORMAL:
            renderer.normal(d0, d1, d2);
            break;
        case COLOR:
            renderer.color(d0, d1, d2, d3);
            break;
        case UV:
            switch (element.getIndex())
            {
            case 0:
                renderer.tex(d0, d1);
                break;
            case 1:
                renderer.overlay(expandTextureCoord(d0, 0xF), expandTextureCoord(d1, 0xF));
                break;
            case 2:
                renderer.lightmap(expandTextureCoord(d0, 0xF0), expandTextureCoord(d1, 0xF0));
                break;
            }
            break;
        default:
        case PADDING:
        case GENERIC:
            break;
        }
        if(e == format.getElements().size() - 1)
        {
            renderer.endVertex();
        }
    }

    public void setBuffer(IVertexBuilder buffer)
    {
        this.renderer = buffer;
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
