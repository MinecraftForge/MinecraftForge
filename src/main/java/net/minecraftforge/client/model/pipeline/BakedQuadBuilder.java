/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;

/**
 * Allows easier building of BakedQuad objects. During building, data is stored
 * unpacked as floats, but is packed into the typical int array format on build.
 */
public class BakedQuadBuilder implements IVertexConsumer
{
    private static final int SIZE = DefaultVertexFormats.BLOCK.getElements().size();
    
    private final float[][][] unpackedData = new float[4][SIZE][4];
    private int tint = -1;
    private Direction orientation;
    private TextureAtlasSprite texture;
    private boolean applyDiffuseLighting = true;

    private int vertices = 0;
    private int elements = 0;
    private boolean full = false;
    private boolean contractUVs = false;

    public BakedQuadBuilder() {}
    
    public BakedQuadBuilder(TextureAtlasSprite texture)
    {
        this.texture = texture;
    }

    public void setContractUVs(boolean value)
    {
        this.contractUVs = value;
    }

    @Override
    public VertexFormat getVertexFormat()
    {
        return DefaultVertexFormats.BLOCK;
    }

    @Override
    public void setQuadTint(int tint)
    {
        this.tint = tint;
    }

    @Override
    public void setQuadOrientation(Direction orientation)
    {
        this.orientation = orientation;
    }

    @Override
    public void setTexture(TextureAtlasSprite texture)
    {
        this.texture = texture;
    }

    @Override
    public void setApplyDiffuseLighting(boolean diffuse)
    {
        this.applyDiffuseLighting = diffuse;
    }

    @Override
    public void put(int element, float... data)
    {
        for(int i = 0; i < 4; i++)
        {
            if(i < data.length)
            {
                unpackedData[vertices][element][i] = data[i];
            }
            else
            {
                unpackedData[vertices][element][i] = 0;
            }
        }
        elements++;
        if(elements == SIZE)
        {
            vertices++;
            elements = 0;
        }
        if(vertices == 4)
        {
            full = true;
        }
    }

    private final float eps = 1f / 0x100;

    public BakedQuad build()
    {
        if(!full)
        {
            throw new IllegalStateException("not enough data");
        }
        if(texture == null)
        {
            throw new IllegalStateException("texture not set");
        }
        if(contractUVs)
        {
            float tX = texture.getWidth() / (texture.getMaxU() - texture.getMinU());
            float tY = texture.getHeight() / (texture.getMaxV() - texture.getMinV());
            float tS = tX > tY ? tX : tY;
            float ep = 1f / (tS * 0x100);
            int uve = 0;
            ImmutableList<VertexFormatElement> elements = DefaultVertexFormats.BLOCK.getElements();
            while(uve < elements.size())
            {
                VertexFormatElement e = elements.get(uve);
                if(e.getUsage() == VertexFormatElement.Usage.UV && e.getIndex() == 0)
                {
                    break;
                }
                uve++;
            }
            if(uve == elements.size())
            {
                throw new IllegalStateException("Can't contract UVs: format doesn't contain UVs");
            }
            float[] uvc = new float[4];
            for(int v = 0; v < 4; v++)
            {
                for(int i = 0; i < 4; i++)
                {
                    uvc[i] += unpackedData[v][uve][i] / 4;
                }
            }
            for(int v = 0; v < 4; v++)
            {
                for (int i = 0; i < 4; i++)
                {
                    float uo = unpackedData[v][uve][i];
                    float un = uo * (1 - eps) + uvc[i] * eps;
                    float ud = uo - un;
                    float aud = ud;
                    if(aud < 0) aud = -aud;
                    if(aud < ep) // not moving a fraction of a pixel
                    {
                        float udc = uo - uvc[i];
                        if(udc < 0) udc = -udc;
                        if(udc < 2 * ep) // center is closer than 2 fractions of a pixel, don't move too close
                        {
                            un = (uo + uvc[i]) / 2;
                        }
                        else // move at least by a fraction
                        {
                            un = uo + (ud < 0 ? ep : -ep);
                        }
                    }
                    unpackedData[v][uve][i] = un;
                }
            }
        }
        int[] packed = new int[DefaultVertexFormats.BLOCK.getIntegerSize() * 4];
        for (int v = 0; v < 4; v++)
        {
            for (int e = 0; e < SIZE; e++)
            {
                LightUtil.pack(unpackedData[v][e], packed, DefaultVertexFormats.BLOCK, v, e);
            }
        }
        return new BakedQuad(packed, tint, orientation, texture, applyDiffuseLighting);
    }
}
