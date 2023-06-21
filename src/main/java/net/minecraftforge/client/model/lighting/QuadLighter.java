/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.lighting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import java.util.Objects;

import static net.minecraftforge.client.model.IQuadTransformer.*;

/**
 * Base class for all quad lighting providers.
 * <p>
 * Contains all the shared elements needed for {@link BakedQuad} processing and defers lighting logic to inheritors.
 *
 * @see FlatQuadLighter
 * @see SmoothQuadLighter
 */
public abstract class QuadLighter
{
    private static final float[] WHITE = new float[] { 1.0f, 1.0f, 1.0f };

    private final BlockColors colors;

    private int currentHash = 0;
    private BlockAndTintGetter level;
    private BlockPos pos;
    private BlockState state;
    private int cachedTintIndex = -1;
    private final float[] cachedTintColor = new float[3];

    // Arrays used for quad processing, initialized once and then used repeatedly to avoid GC pressure
    private final float[] brightness = new float[4];
    private final int[] lightmap = new int[4];
    private final float[][] positions = new float[4][3];
    private final byte[][] normals = new byte[4][3];
    private final int[] packedLightmaps = new int[4];

    protected QuadLighter(BlockColors colors)
    {
        this.colors = colors;
    }

    protected abstract void computeLightingAt(BlockAndTintGetter level, BlockPos pos, BlockState state);

    protected abstract float calculateBrightness(float[] position);

    protected abstract int calculateLightmap(float[] position, byte[] normal);

    public final void setup(BlockAndTintGetter level, BlockPos pos, BlockState state)
    {
        var hash = Objects.hash(level, pos, state);
        if (this.level != null && this.currentHash == hash)
        {
            return; // If we are drawing a block at the same position as before, don't re-compute anything
        }
        this.currentHash = hash;
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.cachedTintIndex = -1;
        computeLightingAt(level, pos, state);
    }

    public final void reset()
    {
        this.level = null; // Invalidates part of the state to force a re-computation
    }

    public final void process(VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad, int overlay)
    {
        var vertices = quad.getVertices();
        for (int i = 0; i < 4; i++)
        {
            int offset = i * STRIDE;
            positions[i][0] = Float.intBitsToFloat(vertices[offset + POSITION]);
            positions[i][1] = Float.intBitsToFloat(vertices[offset + POSITION + 1]);
            positions[i][2] = Float.intBitsToFloat(vertices[offset + POSITION + 2]);
            int packedNormal = vertices[offset + NORMAL];
            normals[i][0] = (byte) (packedNormal & 0xFF);
            normals[i][1] = (byte) ((packedNormal >> 8) & 0xFF);
            normals[i][2] = (byte) ((packedNormal >> 16) & 0xFF);
            packedLightmaps[i] = vertices[offset + UV2];
        }
        if (normals[0][0] == 0 && normals[0][1] == 0 && normals[0][2] == 0)
        {
            Vector3f a = new Vector3f(positions[0]);
            Vector3f ab = new Vector3f(positions[1]);
            Vector3f ac = new Vector3f(positions[2]);
            ac.sub(a);
            ab.sub(a);
            ab.cross(ac);
            ab.normalize();
            for (int v = 0; v < 4; v++)
            {
                normals[v][0] = (byte) (ab.x() * 127);
                normals[v][1] = (byte) (ab.y() * 127);
                normals[v][2] = (byte) (ab.z() * 127);
            }
        }

        for (int i = 0; i < 4; i++)
        {
            var position = positions[i];
            var normal = normals[i];
            int packedLightmap = packedLightmaps[i];

            var adjustedPosition = new float[] {
                    position[0] - 0.5f + ((normal[0] / 127f) * 0.5f),
                    position[1] - 0.5f + ((normal[1] / 127f) * 0.5f),
                    position[2] - 0.5f + ((normal[2] / 127f) * 0.5f)
            };

            var shade = level.getShade(normals[i][0] / 127f, normals[i][1] / 127f, normals[i][2] / 127f, quad.isShade());
            brightness[i] = calculateBrightness(adjustedPosition) * shade;
            int newLightmap = calculateLightmap(adjustedPosition, normal);
            lightmap[i] = Math.max(packedLightmap & 0xFFFF, newLightmap & 0xFFFF) |
                          (Math.max((packedLightmap >> 16) & 0xFFFF, (newLightmap >> 16) & 0xFFFF) << 16);
        }

        var color = quad.isTinted() ? getColorFast(quad.getTintIndex()) : WHITE;
        consumer.putBulkData(pose, quad, brightness, color[0], color[1], color[2], lightmap, overlay, true);
    }

    private float[] getColorFast(int tintIndex)
    {
        if (tintIndex != cachedTintIndex)
        {
            var packedColor = colors.getColor(state, level, pos, tintIndex);
            cachedTintIndex = tintIndex;
            cachedTintColor[0] = ((packedColor >> 16) & 0xFF) / 255F;
            cachedTintColor[1] = ((packedColor >> 8) & 0xFF) / 255F;
            cachedTintColor[2] = (packedColor & 0xFF) / 255F;
        }
        return cachedTintColor;
    }

    public static float calculateShade(float normalX, float normalY, float normalZ, boolean constantAmbientLight)
    {
        float yFactor = constantAmbientLight ? 0.9F : ((3.0F + normalY) / 4.0F);
        return Math.min(normalX * normalX * 0.6F + normalY * normalY * yFactor + normalZ * normalZ * 0.8F, 1.0F);
    }

    /**
     * Note: This method is subtly different than {@link net.minecraft.client.renderer.LevelRenderer#getLightColor(BlockAndTintGetter, BlockState, BlockPos)}
     * as it only uses the state for querying if the state has emissive rendering but instead looks up the state at the given position for checking the
     * light emission.
     */
    protected static int getLightColor(BlockAndTintGetter level, BlockPos pos, BlockState state)
    {
        if (state.emissiveRendering(level, pos))
        {
            return LightTexture.FULL_BRIGHT;
        }
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        int blockLight = Math.max(level.getBrightness(LightLayer.BLOCK, pos), level.getBlockState(pos).getLightEmission(level, pos));
        return skyLight << 20 | blockLight << 4;
    }
}
