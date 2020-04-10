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

package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LightUtil
{
    public static float diffuseLight(float x, float y, float z)
    {
        return Math.min(x * x * 0.6f + y * y * ((3f + y) / 4f) + z * z * 0.8f, 1f);
    }

    public static float diffuseLight(EnumFacing side)
    {
        switch(side)
        {
            case DOWN:
                return .5f;
            case UP:
                return 1f;
            case NORTH:
            case SOUTH:
                return .8f;
            default:
                return .6f;
        }
    }

    public static EnumFacing toSide(float x, float y, float z)
    {
        if(Math.abs(x) > Math.abs(y))
        {
            if(Math.abs(x) > Math.abs(z))
            {
                if(x < 0) return EnumFacing.WEST;
                return EnumFacing.EAST;
            }
            else
            {
                if(z < 0) return EnumFacing.NORTH;
                return EnumFacing.SOUTH;
            }
        }
        else
        {
            if(Math.abs(y) > Math.abs(z))
            {
                if(y < 0) return EnumFacing.DOWN;
                return EnumFacing.UP;
            }
            else
            {
                if(z < 0) return EnumFacing.NORTH;
                return EnumFacing.SOUTH;
            }
        }
    }

    private static final ConcurrentMap<Pair<VertexFormat, VertexFormat>, int[]> formatMaps = new ConcurrentHashMap<>();

    public static void putBakedQuad(IVertexConsumer consumer, BakedQuad quad)
    {
        consumer.setTexture(quad.getSprite());
        consumer.setQuadOrientation(quad.getFace());
        if(quad.hasTintIndex())
        {
            consumer.setQuadTint(quad.getTintIndex());
        }
        consumer.setApplyDiffuseLighting(quad.shouldApplyDiffuseLighting());
        float[] data = new float[4];
        VertexFormat formatFrom = consumer.getVertexFormat();
        VertexFormat formatTo = quad.getFormat();
        int countFrom = formatFrom.getElementCount();
        int countTo = formatTo.getElementCount();
        int[] eMap = mapFormats(formatFrom, formatTo);
        for(int v = 0; v < 4; v++)
        {
            for(int e = 0; e < countFrom; e++)
            {
                if(eMap[e] != countTo)
                {
                    unpack(quad.getVertexData(), data, formatTo, v, eMap[e]);
                    consumer.put(e, data);
                }
                else
                {
                    consumer.put(e);
                }
            }
        }
    }

    private static final VertexFormat DEFAULT_FROM = VertexLighterFlat.withNormal(DefaultVertexFormats.BLOCK);
    private static final VertexFormat DEFAULT_TO = DefaultVertexFormats.ITEM;
    private static final int[] DEFAULT_MAPPING = generateMapping(DEFAULT_FROM, DEFAULT_TO);
    public static int[] mapFormats(VertexFormat from, VertexFormat to)
    {
        //Speedup: in 99.99% this is the mapping, no need to go make a pair, and go through the slower hash map
        if (from.equals(DEFAULT_FROM) && to.equals(DEFAULT_TO))
            return DEFAULT_MAPPING;
        return formatMaps.computeIfAbsent(Pair.of(from, to), pair -> generateMapping(pair.getLeft(), pair.getRight()));
    }

    private static int[] generateMapping(VertexFormat from, VertexFormat to)
    {
        int fromCount = from.getElementCount();
        int toCount = to.getElementCount();
        int[] eMap = new int[fromCount];

        for(int e = 0; e < fromCount; e++)
        {
            VertexFormatElement expected = from.getElement(e);
            int e2;
            for(e2 = 0; e2 < toCount; e2++)
            {
                VertexFormatElement current = to.getElement(e2);
                if(expected.getUsage() == current.getUsage() && expected.getIndex() == current.getIndex())
                {
                    break;
                }
            }
            eMap[e] = e2;
        }
        return eMap;
    }

    public static void unpack(int[] from, float[] to, VertexFormat formatFrom, int v, int e)
    {
        int length = 4 < to.length ? 4 : to.length;
        VertexFormatElement element = formatFrom.getElement(e);
        int vertexStart = v * formatFrom.getNextOffset() + formatFrom.getOffset(e);
        int count = element.getElementCount();
        VertexFormatElement.EnumType type = element.getType();
        int size = type.getSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for(int i = 0; i < length; i++)
        {
            if(i < count)
            {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                int bits = from[index];
                bits = bits >>> (offset * 8);
                if((pos + size - 1) / 4 != index)
                {
                    bits |= from[index + 1] << ((4 - offset) * 8);
                }
                bits &= mask;
                if(type == VertexFormatElement.EnumType.FLOAT)
                {
                    to[i] = Float.intBitsToFloat(bits);
                }
                else if(type == VertexFormatElement.EnumType.UBYTE || type == VertexFormatElement.EnumType.USHORT)
                {
                    to[i] = (float)bits / mask;
                }
                else if(type == VertexFormatElement.EnumType.UINT)
                {
                    to[i] = (float)((double)(bits & 0xFFFFFFFFL) / 0xFFFFFFFFL);
                }
                else if(type == VertexFormatElement.EnumType.BYTE)
                {
                    to[i] = ((float)(byte)bits) / (mask >> 1);
                }
                else if(type == VertexFormatElement.EnumType.SHORT)
                {
                    to[i] = ((float)(short)bits) / (mask >> 1);
                }
                else if(type == VertexFormatElement.EnumType.INT)
                {
                    to[i] = (float)((double)(bits & 0xFFFFFFFFL) / (0xFFFFFFFFL >> 1));
                }
            }
            else
            {
                to[i] = 0;
            }
        }
    }

    public static void pack(float[] from, int[] to, VertexFormat formatTo, int v, int e)
    {
        VertexFormatElement element = formatTo.getElement(e);
        int vertexStart = v * formatTo.getNextOffset() + formatTo.getOffset(e);
        int count = element.getElementCount();
        VertexFormatElement.EnumType type = element.getType();
        int size = type.getSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for(int i = 0; i < 4; i++)
        {
            if(i < count)
            {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                int bits = 0;
                float f = i < from.length ? from[i] : 0;
                if(type == VertexFormatElement.EnumType.FLOAT)
                {
                    bits = Float.floatToRawIntBits(f);
                }
                else if(
                    type == VertexFormatElement.EnumType.UBYTE ||
                    type == VertexFormatElement.EnumType.USHORT ||
                    type == VertexFormatElement.EnumType.UINT
                )
                {
                    bits = Math.round(f * mask);
                }
                else
                {
                    bits = Math.round(f * (mask >> 1));
                }
                to[index] &= ~(mask << (offset * 8));
                to[index] |= (((bits & mask) << (offset * 8)));
                // TODO handle overflow into to[index + 1]
            }
        }
    }

    private static IVertexConsumer tessellator = null;
    @Deprecated // TODO: remove
    public static IVertexConsumer getTessellator()
    {
        if(tessellator == null)
        {
            Tessellator tes = Tessellator.getInstance();
            BufferBuilder wr = tes.getBuffer();
            tessellator = new VertexBufferConsumer(wr);
        }
        return tessellator;
    }

    private static ItemConsumer itemConsumer = null;
    @Deprecated // TODO: remove
    public static ItemConsumer getItemConsumer()
    {
        if(itemConsumer == null)
        {
            itemConsumer = new ItemConsumer(getTessellator());
        }
        return itemConsumer;
    }

    private static final class ItemPipeline
    {
        final VertexBufferConsumer bufferConsumer;
        final ItemConsumer itemConsumer;

        ItemPipeline()
        {
            this.bufferConsumer = new VertexBufferConsumer();
            this.itemConsumer = new ItemConsumer(bufferConsumer);
        }
    }

    private static final ThreadLocal<ItemPipeline> itemPipeline = ThreadLocal.withInitial(ItemPipeline::new);

    // renders quad in any Vertex Format, but is slower
    public static void renderQuadColorSlow(BufferBuilder buffer, BakedQuad quad, int auxColor)
    {
        ItemPipeline pipeline = itemPipeline.get();
        pipeline.bufferConsumer.setBuffer(buffer);
        ItemConsumer cons = pipeline.itemConsumer;

        float b = (float)( auxColor         & 0xFF) / 0xFF;
        float g = (float)((auxColor >>>  8) & 0xFF) / 0xFF;
        float r = (float)((auxColor >>> 16) & 0xFF) / 0xFF;
        float a = (float)((auxColor >>> 24) & 0xFF) / 0xFF;

        cons.setAuxColor(r, g, b, a);
        quad.pipe(cons);
    }

    public static void renderQuadColor(BufferBuilder buffer, BakedQuad quad, int auxColor)
    {
        if (quad.getFormat().equals(buffer.getVertexFormat()))
        {
            buffer.addVertexData(quad.getVertexData());
            if (buffer.getVertexFormat().hasColor())
            {
                ForgeHooksClient.putQuadColor(buffer, quad, auxColor);
            }
        }
        else
        {
            renderQuadColorSlow(buffer, quad, auxColor);
        }
    }

    public static class ItemConsumer extends VertexTransformer
    {
        private int vertices = 0;

        private float[] auxColor = new float[]{1, 1, 1, 1};
        private float[] buf = new float[4];

        public ItemConsumer(IVertexConsumer parent)
        {
            super(parent);
        }

        public void setAuxColor(float... auxColor)
        {
            System.arraycopy(auxColor, 0, this.auxColor, 0, this.auxColor.length);
        }

        @Override
        public void put(int element, float... data)
        {
            if(getVertexFormat().getElement(element).getUsage() == EnumUsage.COLOR)
            {
                System.arraycopy(auxColor, 0, buf, 0, buf.length);
                int n = Math.min(4, data.length);
                for(int i = 0; i < n; i++)
                {
                    buf[i] *= data[i];
                }
                super.put(element, buf);
            }
            else
            {
                super.put(element, data);
            }
            if(element == getVertexFormat().getElementCount() - 1)
            {
                vertices++;
                if(vertices == 4)
                {
                    vertices = 0;
                }
            }
        }
    }
}
