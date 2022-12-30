/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.common.base.Preconditions;
import com.mojang.math.Transformation;
import net.minecraft.Util;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Arrays;

/**
 * A collection of {@link IQuadTransformer} implementations.
 *
 * @see IQuadTransformer
 */
public final class QuadTransformers {

    private static final IQuadTransformer EMPTY = quad -> {};
    private static final IQuadTransformer[] EMISSIVE_TRANSFORMERS = Util.make(new IQuadTransformer[16], array -> {
        Arrays.setAll(array, i -> applyingLightmap(LightTexture.pack(i, i)));
    });

    /**
     * {@return a {@link BakedQuad} transformer that does nothing}
     */
    public static IQuadTransformer empty()
    {
        return EMPTY;
    }

    /**
     * {@return a new {@link BakedQuad} transformer that applies the specified {@link Transformation}}
     */
    public static IQuadTransformer applying(Transformation transform)
    {
        if (transform.isIdentity())
            return empty();
        return quad -> {
            var vertices = quad.getVertices();
            for (int i = 0; i < 4; i++)
            {
                int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
                float x = Float.intBitsToFloat(vertices[offset]);
                float y = Float.intBitsToFloat(vertices[offset + 1]);
                float z = Float.intBitsToFloat(vertices[offset + 2]);

                Vector4f pos = new Vector4f(x, y, z, 1);
                transform.transformPosition(pos);
                pos.div(pos.w);

                vertices[offset] = Float.floatToRawIntBits(pos.x());
                vertices[offset + 1] = Float.floatToRawIntBits(pos.y());
                vertices[offset + 2] = Float.floatToRawIntBits(pos.z());
            }

            for (int i = 0; i < 4; i++)
            {
                int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.NORMAL;
                int normalIn = vertices[offset];
                if ((normalIn & 0x00FFFFFF) != 0) // The ignored byte is padding and may be filled with user data
                {
                    float x = ((byte) (normalIn & 0xFF)) / 127.0f;
                    float y = ((byte) ((normalIn >> 8) & 0xFF)) / 127.0f;
                    float z = ((byte) ((normalIn >> 16) & 0xFF)) / 127.0f;

                    Vector3f pos = new Vector3f(x, y, z);
                    transform.transformNormal(pos);

                    vertices[offset] = (((byte) (pos.x() * 127.0f)) & 0xFF) |
                            ((((byte) (pos.y() * 127.0f)) & 0xFF) << 8) |
                            ((((byte) (pos.z() * 127.0f)) & 0xFF) << 16) |
                            (normalIn & 0xFF000000); // Restore padding, just in case
                }
            }
        };
    }

    /**
     * @return A new {@link BakedQuad} transformer that applies the specified packed light value.
     */
    public static IQuadTransformer applyingLightmap(int packedLight)
    {
        return quad -> {
            var vertices = quad.getVertices();
            for (int i = 0; i < 4; i++)
                vertices[i * IQuadTransformer.STRIDE + IQuadTransformer.UV2] = packedLight;
        };
    }

    /**
     * @return A new {@link BakedQuad} transformer that applies the specified block and sky light values.
     */
    public static IQuadTransformer applyingLightmap(int blockLight, int skyLight)
    {
        return applyingLightmap(LightTexture.pack(blockLight, skyLight));
    }

    /**
     * @return A {@link BakedQuad} transformer that sets the lightmap to the given emissivity (0-15)
     */
    public static IQuadTransformer settingEmissivity(int emissivity)
    {
        Preconditions.checkArgument(emissivity >= 0 && emissivity < 16, "Emissivity must be between 0 and 15.");
        return EMISSIVE_TRANSFORMERS[emissivity];
    }

    /**
     * @return A {@link BakedQuad} transformer that sets the lightmap to its max value
     */
    public static IQuadTransformer settingMaxEmissivity()
    {
        return EMISSIVE_TRANSFORMERS[15];
    }

    /**
     * @param color The color in ARGB format.
     * @return A {@link BakedQuad} transformer that sets the color to the specified value.
     */
    public static IQuadTransformer applyingColor(int color)
    {
        final int fixedColor = toABGR(color);
        return quad -> {
            var vertices = quad.getVertices();
            for (int i = 0; i < 4; i++)
                vertices[i * IQuadTransformer.STRIDE + IQuadTransformer.COLOR] = fixedColor;
        };
    }

    /**
     * This method supplies a default alpha value of 255 (no transparency)
     * @param red The red value (0-255)
     * @param green The green value (0-255)
     * @param blue The blue value (0-255)
     * @return A {@link BakedQuad} transformer that sets the color to the specified value.
     */
    public static IQuadTransformer applyingColor(int red, int green, int blue)
    {
        return applyingColor(255, red, green, blue);
    }

    /**
     * @param alpha The alpha value (0-255)
     * @param red The red value (0-255)
     * @param green The green value (0-255)
     * @param blue The blue value (0-255)
     * @return A {@link BakedQuad} transformer that sets the color to the specified value.
     */
    public static IQuadTransformer applyingColor(int alpha, int red, int green, int blue)
    {
        return applyingColor(alpha << 24 | red << 16 | green << 8 | blue);
    }

    /**
     * Converts an ARGB color to an ABGR color, as the commonly used color format is not the format colors end up packed into.
     * This function doubles as its own inverse.
     * @param color ARGB color
     * @return ABGR color
     */
    public static int toABGR(int argb)
    {
        return (argb & 0xFF00FF00) // alpha and green same spot
             | ((argb >> 16) & 0x000000FF) // red moves to blue
             | ((argb << 16) & 0x00FF0000); // blue moves to red
    }

    private QuadTransformers()
    {
    }
}
