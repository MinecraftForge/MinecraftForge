/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;

import java.util.function.Consumer;

/**
 * Vertex consumer that outputs {@link BakedQuad baked quads}.
 * <p>
 * This consumer accepts data in {@link com.mojang.blaze3d.vertex.DefaultVertexFormat#BLOCK} and is not picky about
 * ordering or missing elements, but will not automatically populate missing data (color will be black, for example).
 */
public class QuadBakingVertexConsumer implements VertexConsumer
{
    private static final int STRIDE = IQuadTransformer.STRIDE;
    private static final int POSITION = IQuadTransformer.POSITION;
    private static final int COLOR = IQuadTransformer.COLOR;
    private static final int UV0 = IQuadTransformer.UV0;
    private static final int UV2 = IQuadTransformer.UV2;
    private static final int NORMAL = IQuadTransformer.NORMAL;
    private static final int QUAD_DATA_SIZE = STRIDE * 4;

    private final Consumer<BakedQuad> quadConsumer;

    private int vertexIndex = 0;
    private int[] quadData = new int[QUAD_DATA_SIZE];

    private int tintIndex;
    private Direction direction = Direction.DOWN;
    private TextureAtlasSprite sprite = UnitTextureAtlasSprite.INSTANCE;
    private boolean shade;

    public QuadBakingVertexConsumer(Consumer<BakedQuad> quadConsumer)
    {
        this.quadConsumer = quadConsumer;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z)
    {
        int offset = vertexIndex * STRIDE + POSITION;
        quadData[offset] = Float.floatToRawIntBits((float) x);
        quadData[offset + 1] = Float.floatToRawIntBits((float) y);
        quadData[offset + 2] = Float.floatToRawIntBits((float) z);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z)
    {
        int offset = vertexIndex * STRIDE + NORMAL;
        quadData[offset] = ((int) (x * 127.0f) & 0xFF) |
                           (((int) (y * 127.0f) & 0xFF) << 8) |
                           (((int) (z * 127.0f) & 0xFF) << 16);
        return this;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a)
    {
        int offset = vertexIndex * STRIDE + COLOR;
        quadData[offset] = ((a & 0xFF) << 24) |
                           ((b & 0xFF) << 16) |
                           ((g & 0xFF) << 8) |
                           (r & 0xFF);
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v)
    {
        int offset = vertexIndex * STRIDE + UV0;
        quadData[offset] = Float.floatToRawIntBits(u);
        quadData[offset + 1] = Float.floatToRawIntBits(v);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v)
    {
        return this; // NO-OP, not part of the BLOCK format
    }

    @Override
    public VertexConsumer uv2(int u, int v)
    {
        int offset = vertexIndex * STRIDE + UV2;
        quadData[offset] = (u & 0xFFFF) | ((v & 0xFFFF) << 16);
        return this;
    }

    @Override
    public void endVertex()
    {
        if (++vertexIndex != 4)
        {
            return;
        }
        // We have a full quad, pass it to the consumer and reset
        quadConsumer.accept(new BakedQuad(quadData, tintIndex, direction, sprite, shade));
        vertexIndex = 0;
        quadData = new int[QUAD_DATA_SIZE];
    }

    @Override
    public void defaultColor(int r, int g, int b, int a)
    {
    }

    @Override
    public void unsetDefaultColor()
    {
    }

    public void setTintIndex(int tintIndex)
    {
        this.tintIndex = tintIndex;
    }

    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }

    public void setSprite(TextureAtlasSprite sprite)
    {
        this.sprite = sprite;
    }

    public void setShade(boolean shade)
    {
        this.shade = shade;
    }
}
