/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.client.model.pipeline.LightUtil;

import org.lwjgl.system.MemoryStack;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public interface IForgeVertexBuilder
{
    default IVertexBuilder getVertexBuilder() { return (IVertexBuilder)this; }

    // Copy of addQuad, but enables tinting and per-vertex alpha
    default void addVertexData(MatrixStack.Entry matrixStack, BakedQuad bakedQuad, float red, float green, float blue, int lightmapCoord, int overlayColor, boolean readExistingColor) {
        addVertexData(matrixStack, bakedQuad, red, green, blue, 1.0f, lightmapCoord, overlayColor, readExistingColor);
    }

    // Copy of addQuad with alpha support
    default void addVertexData(MatrixStack.Entry matrixEntry, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int lightmapCoord, int overlayColor) {
        addVertexData(matrixEntry, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, alpha, new int[]{lightmapCoord, lightmapCoord, lightmapCoord, lightmapCoord}, overlayColor, false);
    }

    // Copy of addQuad with alpha support
    default void addVertexData(MatrixStack.Entry matrixEntry, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int lightmapCoord, int overlayColor, boolean readExistingColor) {
        addVertexData(matrixEntry, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, alpha, new int[]{lightmapCoord, lightmapCoord, lightmapCoord, lightmapCoord}, overlayColor, readExistingColor);
    }

    // Copy of addQuad with alpha support
    default void addVertexData(MatrixStack.Entry matrixEntry, BakedQuad bakedQuad, float[] baseBrightness, float red, float green, float blue, float alpha, int[] lightmapCoords, int overlayCoords, boolean readExistingColor) {
        int[] aint = bakedQuad.getVertexData();
        Vector3i faceNormal = bakedQuad.getFace().getDirectionVec();
        Vector3f normal = new Vector3f((float)faceNormal.getX(), (float)faceNormal.getY(), (float)faceNormal.getZ());
        Matrix4f matrix4f = matrixEntry.getMatrix();
        normal.transform(matrixEntry.getNormal());
        int intSize = DefaultVertexFormats.BLOCK.getIntegerSize();
        int vertexCount = aint.length / intSize;

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormats.BLOCK.getSize());
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
                applyBakedNormals(normal, bytebuffer, matrixEntry.getNormal());
                ((IVertexBuilder)this).addVertex(pos.getX(), pos.getY(), pos.getZ(), cr, cg, cb, ca, f9, f10, overlayCoords, lightmapCoord, normal.getX(), normal.getY(), normal.getZ());
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
