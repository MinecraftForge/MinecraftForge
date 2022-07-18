/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.Arrays;
import java.util.List;

/**
 * Transformer for {@link BakedQuad baked quads}.
 *
 * @see #applying(Transformation)
 * @see #applyingLightmap(int)
 */
public interface IQuadTransformer
{
    int STRIDE = DefaultVertexFormat.BLOCK.getIntegerSize();
    int POSITION = findOffset(DefaultVertexFormat.ELEMENT_POSITION);
    int COLOR = findOffset(DefaultVertexFormat.ELEMENT_COLOR);
    int UV0 = findOffset(DefaultVertexFormat.ELEMENT_UV0);
    int UV1 = findOffset(DefaultVertexFormat.ELEMENT_UV1);
    int UV2 = findOffset(DefaultVertexFormat.ELEMENT_UV2);
    int NORMAL = findOffset(DefaultVertexFormat.ELEMENT_NORMAL);

    void processInPlace(BakedQuad quad);

    default void processInPlace(List<BakedQuad> quads)
    {
        for (BakedQuad quad : quads)
            processInPlace(quad);
    }

    default BakedQuad process(BakedQuad quad)
    {
        var copy = copy(quad);
        processInPlace(copy);
        return copy;
    }

    default List<BakedQuad> process(List<BakedQuad> inputs)
    {
        return inputs.stream().map(IQuadTransformer::copy).peek(this::processInPlace).toList();
    }

    default IQuadTransformer andThen(IQuadTransformer other)
    {
        return quad -> {
            processInPlace(quad);
            other.processInPlace(quad);
        };
    }

    /**
     * Creates a {@link BakedQuad} transformer that does nothing.
     */
    static IQuadTransformer empty()
    {
        return quad -> {};
    }

    /**
     * Creates a {@link BakedQuad} transformer that applies the specified {@link Transformation}.
     */
    static IQuadTransformer applying(Transformation transform)
    {
        if (transform.isIdentity())
            return empty();
        return quad -> {
            var vertices = quad.getVertices();
            for (int i = 0; i < 4; i++)
            {
                int offset = i * STRIDE + POSITION;
                float x = Float.intBitsToFloat(vertices[offset]);
                float y = Float.intBitsToFloat(vertices[offset + 1]);
                float z = Float.intBitsToFloat(vertices[offset + 2]);

                Vector4f pos = new Vector4f(x, y, z, 1);
                transform.transformPosition(pos);
                pos.perspectiveDivide();

                vertices[offset] = Float.floatToRawIntBits(pos.x());
                vertices[offset + 1] = Float.floatToRawIntBits(pos.y());
                vertices[offset + 2] = Float.floatToRawIntBits(pos.z());
            }

            for (int i = 0; i < 4; i++)
            {
                int offset = i * STRIDE + NORMAL;
                int normalIn = vertices[offset];
                if ((normalIn >> 8) != 0)
                {
                    float x = ((byte) (normalIn & 0xFF)) / 127.0f;
                    float y = ((byte) ((normalIn >> 8) & 0xFF)) / 127.0f;
                    float z = ((byte) ((normalIn >> 16) & 0xFF)) / 127.0f;

                    Vector3f pos = new Vector3f(x, y, z);
                    transform.transformNormal(pos);
                    pos.normalize();

                    vertices[offset] = (((byte) (x * 127.0f)) & 0xFF) |
                                       ((((byte) (y * 127.0f)) & 0xFF) << 8) |
                                       ((((byte) (z * 127.0f)) & 0xFF) << 16) |
                                       (normalIn & 0xFF000000);
                }
            }
        };
    }

    /**
     * Creates a {@link BakedQuad} transformer that applies the specified lightmap.
     */
    static IQuadTransformer applyingLightmap(int lightmap)
    {
        return quad -> {
            var vertices = quad.getVertices();
            for (int i = 0; i < 4; i++)
                vertices[i * STRIDE + UV2] = lightmap;
        };
    }

    private static BakedQuad copy(BakedQuad quad)
    {
        var vertices = quad.getVertices();
        return new BakedQuad(Arrays.copyOf(vertices, vertices.length), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
    }

    private static int findOffset(VertexFormatElement element)
    {
        // Divide by 4 because we want the int offset
        var index = DefaultVertexFormat.BLOCK.getElements().indexOf(element);
        return index < 0 ? -1 : DefaultVertexFormat.BLOCK.getOffset(index) / 4;
    }
}
