/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.IQuadTransformer;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

/**
 * Extension interface for {@link VertexConsumer}.
 */
public interface IForgeVertexConsumer
{
    private VertexConsumer self()
    {
        return (VertexConsumer) this;
    }

    /**
     * Consumes an unknown {@link VertexFormatElement} as a raw int data array.
     * <p>
     * If the consumer needs to store the data for later use, it must copy it. There are no guarantees on immutability.
     */
    default VertexConsumer misc(VertexFormatElement element, int... rawData)
    {
        return self();
    }

    /**
     * Variant with no per-vertex shading.
     */
    default void putBulkData(PoseStack.Pose pose, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int packedLight, int packedOverlay, boolean readExistingColor)
    {
        self().putBulkData(pose, bakedQuad, new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, red, green, blue, alpha, new int[] { packedLight, packedLight, packedLight, packedLight }, packedOverlay, readExistingColor);
    }

    default int applyBakedLighting(int packedLight, ByteBuffer data)
    {
        int bl = packedLight & 0xFFFF;
        int sl = (packedLight >> 16) & 0xFFFF;
        int offset = IQuadTransformer.UV2 * 4; // int offset for vertex 0 * 4 bytes per int
        int blBaked = Short.toUnsignedInt(data.getShort(offset));
        int slBaked = Short.toUnsignedInt(data.getShort(offset + 2));
        bl = Math.max(bl, blBaked);
        sl = Math.max(sl, slBaked);
        return bl | (sl << 16);
    }

    default void applyBakedNormals(Vector3f generated, ByteBuffer data, Matrix3f normalTransform)
    {
        byte nx = data.get(28);
        byte ny = data.get(29);
        byte nz = data.get(30);
        if (nx != 0 || ny != 0 || nz != 0)
        {
            generated.set(nx / 127f, ny / 127f, nz / 127f);
            generated.mul(normalTransform);
        }
    }
}
