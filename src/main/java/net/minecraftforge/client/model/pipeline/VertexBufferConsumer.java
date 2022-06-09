/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Assumes VertexFormatElement is present in the BufferBuilder's vertex format.
 */
public class VertexBufferConsumer implements IVertexConsumer
{
    private VertexFormat format;
    private List<VertexFormatElement> elements;
    private VertexConsumer renderer;
    private boolean overrideOverlayCoords;
    private int overlayCoordU;
    private int overlayCoordV;

    public VertexBufferConsumer()
    {
        setVertexFormat(DefaultVertexFormat.BLOCK);
    }

    public VertexBufferConsumer(VertexConsumer buffer)
    {
        setBuffer(buffer);
    }

    @Override
    public final VertexFormat getVertexFormat()
    {
        return format;
    }

    @Override
    public void put(int e, float... data)
    {
        final float d0 = data.length <= 0 ? 0 : data[0];
        final float d1 = data.length <= 1 ? 0 : data[1];
        final float d2 = data.length <= 2 ? 0 : data[2];
        final float d3 = data.length <= 3 ? 0 : data[3];

        var element = elements.get(e);

        switch (element.getUsage())
        {
        case POSITION: // POSITION_3F
            if (element.getIndex() == 0)
                renderer.vertex(d0, d1, d2);
            break;
        case NORMAL: // NORMAL_3B
            if (element.getIndex() == 0)
                renderer.normal(d0, d1, d2);
            break;
        case COLOR: // COLOR_4UB
            if (element.getIndex() == 0)
                renderer.color(d0, d1, d2, d3);
            break;
        case UV: // TEX_2F
            switch(element.getIndex())
            {
                case 0 -> renderer.uv(d0, d1);
                case 1 -> {
                    if (overrideOverlayCoords)
                        renderer.overlayCoords(overlayCoordU, overlayCoordV);
                    else
                        renderer.overlayCoords((int) (d0 * 32767f), (int) (d1 * 32767f));
                }
                case 2 -> renderer.uv2((int) (d0 * 0xF0), (int) (d1 * 0xF0));
            }
            break;
        case PADDING:
            break;
        default:
            throw new IllegalArgumentException("Vertex element out of bounds: " + e);
        }
        if(e == 5)
        {
            renderer.endVertex();
        }
    }

    public void setBuffer(VertexConsumer buffer)
    {
        this.renderer = buffer;
        setVertexFormat(buffer.getVertexFormat());
    }

    public void setVertexFormat(@Nullable VertexFormat format)
    {
        this.format = format != null ? format : DefaultVertexFormat.BLOCK;
        this.elements = this.format.getElements();
    }

    @Override
    public void setQuadTint(int tint) {}
    @Override
    public void setQuadOrientation(Direction orientation) {}
    @Override
    public void setApplyDiffuseLighting(boolean diffuse) {}
    @Override
    public void setTexture(TextureAtlasSprite texture ) {}

    public void setPackedOverlay(int packedOverlay)
    {
        this.overrideOverlayCoords = true;
        this.overlayCoordV = (packedOverlay >> 16) & 0xFFFF;
        this.overlayCoordU = (packedOverlay) & 0xFFFF;
    }
}
