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

package net.minecraftforge.client.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class QuadTransformer
{
    private static final int POSITION = findPositionOffset(DefaultVertexFormats.BLOCK);
    private static final int NORMAL = findNormalOffset(DefaultVertexFormats.BLOCK);
    private final TransformationMatrix transform;

    public QuadTransformer(TransformationMatrix transform)
    {
        this.transform = transform;
    }

    private void processVertices(int[] inData, int[] outData)
    {
        int stride = DefaultVertexFormats.BLOCK.getSize();
        int count = (inData.length * 4) / stride;
        for (int i=0;i<count;i++)
        {
            int offset = POSITION + i * stride;
            float x = Float.intBitsToFloat(getAtByteOffset(inData, offset ));
            float y = Float.intBitsToFloat(getAtByteOffset(inData, offset + 4));
            float z = Float.intBitsToFloat(getAtByteOffset(inData, offset + 8));

            Vector4f pos = new Vector4f(x, y, z, 1);
            transform.transformPosition(pos);
            pos.perspectiveDivide();

            putAtByteOffset(outData, offset, Float.floatToRawIntBits(pos.getX()));
            putAtByteOffset(outData,offset + 4, Float.floatToRawIntBits(pos.getY()));
            putAtByteOffset(outData,offset + 8, Float.floatToRawIntBits(pos.getZ()));
        }

        for (int i=0;i<count;i++)
        {
            int offset = NORMAL + i * stride;
            int normalIn = getAtByteOffset(inData,offset);
            if (normalIn != 0)
            {

                float x = ((byte)((normalIn) >> 24)) / 127.0f;
                float y = ((byte)((normalIn << 8) >> 24)) / 127.0f;
                float z = ((byte)((normalIn << 16) >> 24)) / 127.0f;

                Vector3f pos = new Vector3f(x, y, z);
                transform.transformNormal(pos);
                pos.normalize();

                int normalOut = ((((byte)(x / 127.0f)) & 0xFF) << 24) |
                                ((((byte)(y / 127.0f)) & 0xFF) << 16) |
                                ((((byte)(z / 127.0f)) & 0xFF) << 8) |
                                (normalIn & 0xFF);

                putAtByteOffset(outData, offset, normalOut);
            }
        }
    }

    private static int getAtByteOffset(int[] inData, int offset)
    {
        int index = offset / 4;
        int lsb = inData[index];

        int shift = (offset % 4) * 8;
        if (shift == 0)
            return inData[index];

        int msb = inData[index+1];

        return (lsb >>> shift) | (msb << (32-shift));
    }

    private static void putAtByteOffset(int[] outData, int offset, int value)
    {
        int index = offset / 4;
        int shift = (offset % 4) * 8;

        if (shift == 0)
        {
            outData[index] = value;
            return;
        }

        int lsbMask = 0xFFFFFFFF >>> (32-shift);
        int msbMask = 0xFFFFFFFF << shift;

        outData[index] = (outData[index] & lsbMask) | (value << shift);
        outData[index+1] = (outData[index+1] & msbMask) | (value >>> (32-shift));
    }

    private static int findPositionOffset(VertexFormat fmt)
    {
        int index;
        VertexFormatElement element = null;
        for (index = 0; index < fmt.getElements().size(); index++)
        {
            VertexFormatElement el = fmt.getElements().get(index);
            if (el.getUsage() == VertexFormatElement.Usage.POSITION)
            {
                element = el;
                break;
            }
        }
        if (index == fmt.getElements().size() || element == null)
            throw new RuntimeException("Expected vertex format to have a POSITION attribute");
        if (element.getType() != VertexFormatElement.Type.FLOAT)
            throw new RuntimeException("Expected POSITION attribute to have data type FLOAT");
        if (element.getSize() < 3)
            throw new RuntimeException("Expected POSITION attribute to have at least 3 dimensions");
        return fmt.getOffset(index);
    }

    private static int findNormalOffset(VertexFormat fmt)
    {
        int index;
        VertexFormatElement element = null;
        for (index = 0; index < fmt.getElements().size(); index++)
        {
            VertexFormatElement el = fmt.getElements().get(index);
            if (el.getUsage() == VertexFormatElement.Usage.NORMAL)
            {
                element = el;
                break;
            }
        }
        if (index == fmt.getElements().size() || element == null)
            throw new IllegalStateException("BLOCK format does not have normals?");
        if (element.getType() != VertexFormatElement.Type.BYTE)
            throw new RuntimeException("Expected NORMAL attribute to have data type BYTE");
        if (element.getSize() < 3)
            throw new RuntimeException("Expected NORMAL attribute to have at least 3 dimensions");
        return fmt.getOffset(index);
    }

    /**
     * Processes a single quad, producing a new quad.
     * @param input A single quad to transform.
     * @return A new BakedQuad object with the new position.
     */
    public BakedQuad processOne(BakedQuad input)
    {
        int[] inData = input.getVertexData();
        int[] outData = Arrays.copyOf(inData, inData.length);
        processVertices(inData, outData);

        return new BakedQuad(outData, input.getTintIndex(), input.getFace(), input.getSprite(), input.applyDiffuseLighting());
    }

    /**
     * Processes a single quad, modifying the input quad.
     * @param input A single quad to transform.
     * @return The input BakedQuad object with the new position applied.
     */
    public BakedQuad processOneInPlace(BakedQuad input)
    {
        int[] data = input.getVertexData();
        processVertices(data, data);
        return input;
    }

    /**
     * Processes multiple quads, producing a new array of new quads.
     * @param inputs The list of quads to transform
     * @return A new array of new BakedQuad objects.
     */
    public List<BakedQuad> processMany(List<BakedQuad> inputs)
    {
        if(inputs.size() == 0)
            return Collections.emptyList();

        List<BakedQuad> outputs = Lists.newArrayList();
        for(BakedQuad input : inputs)
        {
            int[] inData = input.getVertexData();
            int[] outData = Arrays.copyOf(inData, inData.length);
            processVertices(inData, outData);

            outputs.add(new BakedQuad(outData, input.getTintIndex(), input.getFace(), input.getSprite(), input.applyDiffuseLighting()));
        }
        return outputs;
    }

    /**
     * Processes multiple quads in place, modifying the input quads.
     * @param inputs The list of quads to transform
     */
    public void processManyInPlace(List<BakedQuad> inputs)
    {
        if(inputs.size() == 0)
            return;

        for(BakedQuad input : inputs)
        {
            int[] data = input.getVertexData();
            processVertices(data, data);
        }
    }
}
