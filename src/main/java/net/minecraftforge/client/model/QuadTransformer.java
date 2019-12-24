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

package net.minecraftforge.client.model;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuadTransformer
{
    private final VertexFormat format;
    private final int positionOffset;
    private final int normalOffset;
    private final TransformationMatrix transform;

    public QuadTransformer(VertexFormat format, TransformationMatrix transform)
    {
        this.format = format;
        this.positionOffset = findPositionOffset(format);
        this.normalOffset = findNormalOffset(format);
        this.transform = transform;
    }

    private void processVertices(int[] inData, int[] outData)
    {
        int stride = format.getIntegerSize();
        int count = inData.length / stride;
        for (int i=0;i<count;i++)
        {
            int offset = positionOffset + i * stride;
            float x = Float.intBitsToFloat(inData[offset ]);
            float y = Float.intBitsToFloat(inData[offset + 1]);
            float z = Float.intBitsToFloat(inData[offset + 2]);

            Vector4f pos = new Vector4f(x, y, z, 1);
            transform.transformPosition(pos);
            pos.func_229374_e_();

            outData[offset] = Float.floatToRawIntBits(pos.getX());
            outData[offset + 1] = Float.floatToRawIntBits(pos.getY());
            outData[offset + 2] = Float.floatToRawIntBits(pos.getZ());
        }

        if (normalOffset >= 0)
        {
            for (int i=0;i<count;i++)
            {
                int offset = normalOffset + i * stride;
                int normalIn = inData[offset];
                float x = (((normalIn)>>24)&0xFF)/255.0f;
                float y = (((normalIn<<8)>>24)&0xFF)/255.0f;
                float z = (((normalIn<<16)>>24)&0xFF)/255.0f;

                Vector3f pos = new Vector3f(x, y, z);
                transform.transformNormal(pos);

                int normalOut =
                        (((int)(x/255.0)&0xFF)<<24) |
                        (((int)(y/255.0)&0xFF)<<16) |
                        (((int)(z/255.0)&0xFF)<<8) |
                        (normalIn&0xFF);

                outData[offset] = normalOut;
            }
        }
    }

    private static int findPositionOffset(VertexFormat fmt)
    {
        int index;
        VertexFormatElement element = null;
        for (index = 0; index < fmt.func_227894_c_().size(); index++)
        {
            VertexFormatElement el = fmt.func_227894_c_().get(index);
            if (el.getUsage() == VertexFormatElement.Usage.POSITION)
            {
                element = el;
                break;
            }
        }
        if (index == fmt.func_227894_c_().size() || element == null)
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
        for (index = 0; index < fmt.func_227894_c_().size(); index++)
        {
            VertexFormatElement el = fmt.func_227894_c_().get(index);
            if (el.getUsage() == VertexFormatElement.Usage.NORMAL)
            {
                element = el;
                break;
            }
        }
        if (index == fmt.func_227894_c_().size() || element == null)
            return -1;
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

        return new BakedQuad(outData, input.getTintIndex(), input.getFace(), input.getSprite(), input.shouldApplyDiffuseLighting(), format);
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

            outputs.add(new BakedQuad(outData, input.getTintIndex(), input.getFace(), input.getSprite(), input.shouldApplyDiffuseLighting(), format));
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
