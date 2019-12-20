/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public interface IForgeVertexBuilder
{
    default IVertexBuilder getVertexBuilder() { return (IVertexBuilder)this; }

    // Copy of func_227889_a_, but enables tinting
    default void addVertexData(MatrixStack.Entry matrixStack, BakedQuad bakedQuad, float red, float green, float blue, int lightmapCoord, int overlayColor, boolean readExistingColor) {
        getVertexBuilder().func_227890_a_(matrixStack, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, new int[]{lightmapCoord, lightmapCoord, lightmapCoord, lightmapCoord}, overlayColor, readExistingColor);
    }

    // Copy of func_227889_a_ with alpha support
    default void addVertexData(MatrixStack.Entry matrixEntry, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int lightmapCoord, int overlayColor) {
        addVertexData(matrixEntry, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, alpha, new int[]{lightmapCoord, lightmapCoord, lightmapCoord, lightmapCoord}, overlayColor, false);
    }

    // Copy of func_227889_a_ with alpha support
    default void addVertexData(MatrixStack.Entry matrixEntry, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int lightmapCoord, int overlayColor, boolean readExistingColor) {
        addVertexData(matrixEntry, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, alpha, new int[]{lightmapCoord, lightmapCoord, lightmapCoord, lightmapCoord}, overlayColor, readExistingColor);
    }

    // Copy of func_227890_a_ with alpha support
    default void addVertexData(MatrixStack.Entry matrixEntry, BakedQuad bakedQuad, float[] baseBrightness, float red, float green, float blue, float alpha, int[] lightmapCoords, int overlayCoords, boolean readExistingColor) {
        int[] aint = bakedQuad.getVertexData();
        Vec3i faceNormal = bakedQuad.getFace().getDirectionVec();
        Vector3f normal = new Vector3f((float)faceNormal.getX(), (float)faceNormal.getY(), (float)faceNormal.getZ());
        Matrix4f matrix4f = matrixEntry.func_227870_a_();
        normal.func_229188_a_(matrixEntry.func_227872_b_());
        int intSize = DefaultVertexFormats.BLOCK.getIntegerSize();
        int vertexCount = aint.length / intSize;

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormats.BLOCK.getSize());
            IntBuffer intbuffer = bytebuffer.asIntBuffer();

            for(int v = 0; v < vertexCount; ++v) {
                intbuffer.clear();
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

                int lightmapCoord = lightmapCoords[v];
                float f9 = bytebuffer.getFloat(16);
                float f10 = bytebuffer.getFloat(20);
                Vector4f pos = new Vector4f(f, f1, f2, 1.0F);
                pos.func_229372_a_(matrix4f);
                ((IVertexBuilder)this).func_225588_a_(pos.getX(), pos.getY(), pos.getZ(), cr, cg, cb, ca, f9, f10, overlayCoords, lightmapCoord, normal.getX(), normal.getY(), normal.getZ());
            }
        }

    }
}
