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

package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.block.model.BakedQuad;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraftforge.client.model.pipeline.LightUtil;

import org.lwjgl.system.MemoryStack;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.core.Vec3i;

public interface IForgeVertexConsumer
{
    // Copy of putBulkData, but enables tinting and per-vertex alpha
    default void putBulkData(PoseStack.Pose matrixStack, BakedQuad bakedQuad, float red, float green, float blue, int lightmapCoord, int overlayColor, boolean readExistingColor) {
        putBulkData(matrixStack, bakedQuad, red, green, blue, 1.0f, lightmapCoord, overlayColor, readExistingColor);
    }

    // Copy of putBulkData with alpha support
    default void putBulkData(PoseStack.Pose matrixEntry, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int lightmapCoord, int overlayColor) {
        putBulkData(matrixEntry, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, alpha, new int[]{lightmapCoord, lightmapCoord, lightmapCoord, lightmapCoord}, overlayColor, false);
    }

    // Copy of putBulkData with alpha support
    default void putBulkData(PoseStack.Pose matrixEntry, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int lightmapCoord, int overlayColor, boolean readExistingColor) {
        putBulkData(matrixEntry, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, alpha, new int[]{lightmapCoord, lightmapCoord, lightmapCoord, lightmapCoord}, overlayColor, readExistingColor);
    }

    // Copy of putBulkData with alpha support
    default void putBulkData(PoseStack.Pose matrixEntry, BakedQuad bakedQuad, float[] baseBrightness, float red, float green, float blue, float alpha, int[] lightmapCoords, int overlayCoords, boolean readExistingColor) {
        int[] aint = bakedQuad.getVertices();
        Vec3i faceNormal = bakedQuad.getDirection().getNormal();
        Vector3f normal = new Vector3f((float)faceNormal.getX(), (float)faceNormal.getY(), (float)faceNormal.getZ());
        Matrix4f matrix4f = matrixEntry.pose();
        normal.transform(matrixEntry.normal());
        int intSize = DefaultVertexFormat.BLOCK.getIntegerSize();
        int vertexCount = aint.length / intSize;

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intbuffer = bytebuffer.asIntBuffer();

            for(int v = 0; v < vertexCount; ++v) {
                ((Buffer)intbuffer).clear();
                intbuffer.put(aint, v * 8, 8);
                float f = bytebuffer.getFloat(0);
                float f1 = bytebuffer.getFloat(4);
                float f2 = bytebuffer.getFloat(8);
                float cr;
                float cg;
                float cb;
                float ca;
                if (readExistingColor) {
                    float r = (float)(bytebuffer.get(12) & 255) / 255.0F;
                    float g = (float)(bytebuffer.get(13) & 255) / 255.0F;
                    float b = (float)(bytebuffer.get(14) & 255) / 255.0F;
                    float a = (float)(bytebuffer.get(15) & 255) / 255.0F;
                    cr = r * baseBrightness[v] * red;
                    cg = g * baseBrightness[v] * green;
                    cb = b * baseBrightness[v] * blue;
                    ca = a * alpha;
                } else {
                    cr = baseBrightness[v] * red;
                    cg = baseBrightness[v] * green;
                    cb = baseBrightness[v] * blue;
                    ca = alpha;
                }

                int lightmapCoord = applyBakedLighting(lightmapCoords[v], bytebuffer);
                float f9 = bytebuffer.getFloat(16);
                float f10 = bytebuffer.getFloat(20);
                Vector4f pos = new Vector4f(f, f1, f2, 1.0F);
                pos.transform(matrix4f);
                applyBakedNormals(normal, bytebuffer, matrixEntry.normal());
                ((VertexConsumer)this).vertex(pos.x(), pos.y(), pos.z(), cr, cg, cb, ca, f9, f10, overlayCoords, lightmapCoord, normal.x(), normal.y(), normal.z());
            }
        }
    }

    default int applyBakedLighting(int lightmapCoord, ByteBuffer data) {
        int bl = lightmapCoord&0xFFFF;
        int sl = (lightmapCoord>>16)&0xFFFF;
        int offset = LightUtil.getLightOffset(0) * 4; // int offset for vertex 0 * 4 bytes per int
        int blBaked = Short.toUnsignedInt(data.getShort(offset));
        int slBaked = Short.toUnsignedInt(data.getShort(offset + 2));
        bl = Math.max(bl, blBaked);
        sl = Math.max(sl, slBaked);
        return bl | (sl<<16);
    }

    default void applyBakedNormals(Vector3f generated, ByteBuffer data, Matrix3f normalTransform) {
        byte nx = data.get(28);
        byte ny = data.get(29);
        byte nz = data.get(30);
        if (nx != 0 || ny != 0 || nz != 0) {
            generated.set(nx / 127f, ny / 127f, nz / 127f);
            generated.transform(normalTransform);
        }
    }
}
