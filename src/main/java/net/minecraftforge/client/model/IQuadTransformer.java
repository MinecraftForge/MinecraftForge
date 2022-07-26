/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.Arrays;
import java.util.List;

/**
 * Transformer for {@link BakedQuad baked quads}.
 *
 * @see QuadTransformers
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
     *
     * @deprecated Use {@link QuadTransformers#empty()}
     */
    @Deprecated(forRemoval = true, since = "1.19")
    static IQuadTransformer empty()
    {
        return QuadTransformers.empty();
    }

    /**
     * Creates a {@link BakedQuad} transformer that applies the specified {@link Transformation}.
     *
     * @deprecated Use {@link QuadTransformers#applying(Transformation)}
     */
    @Deprecated(forRemoval = true, since = "1.19")
    static IQuadTransformer applying(Transformation transform)
    {
        return QuadTransformers.applying(transform);
    }

    /**
     * Creates a {@link BakedQuad} transformer that applies the specified lightmap.
     *
     * @deprecated Use {@link QuadTransformers#applyingLightmap(int)}
     */
    @Deprecated(forRemoval = true, since = "1.19")
    static IQuadTransformer applyingLightmap(int lightmap)
    {
        return QuadTransformers.applyingLightmap(lightmap);
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
