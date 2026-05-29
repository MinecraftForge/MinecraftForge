/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.model;

import com.mojang.math.Transformation;

import net.minecraft.client.resources.model.geometry.BakedQuad;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

/**
 * A collection of {@link IQuadTransformer} implementations.
 *
 * @see IQuadTransformer
 */
public final class QuadTransformers {
    private static final IQuadTransformer EMPTY = quad -> quad;

    /**
     * {@return a {@link BakedQuad} transformer that does nothing}
     */
    public static IQuadTransformer empty() {
        return EMPTY;
    }

    /**
     * {@return a new {@link BakedQuad} transformer that applies the specified {@link Transformation}}
     */
    public static IQuadTransformer applying(Transformation transform) {
        if (transform.isIdentity())
            return empty();

        return quad -> {
            var positions = new Vector3fc[4];
            for (int i = 0; i < 4; i++) {
                var pos = new Vector4f(quad.position(i), 1);
                transform.transformPosition(pos);
                pos.div(pos.w);
                positions[i] = new Vector3f(pos.x(), pos.y(), pos.z());
            }

            return new BakedQuad(positions[0], positions[1], positions[2], positions[3],
                    quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                    quad.direction(), quad.materialInfo());
        };
    }
    /**
     * Converts an ARGB color to an ABGR color, as the commonly used color format is not the format colors end up packed into.
     * This function doubles as its own inverse.
     * @param argb ARGB color
     * @return ABGR color
     */
    public static int toABGR(int argb) {
        return (argb & 0xFF00FF00) // alpha and green same spot
             | ((argb >> 16) & 0x000000FF) // red moves to blue
             | ((argb << 16) & 0x00FF0000); // blue moves to red
    }

    private QuadTransformers() { }
}
