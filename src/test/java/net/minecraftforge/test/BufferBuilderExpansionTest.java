/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.test;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.junit.Test;

import java.util.Arrays;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertTrue;

public class BufferBuilderExpansionTest
{

    public VertexFormat format = DefaultVertexFormats.POSITION;
    // 4 quads.
    public int num_quads = 4;
    public int BUFFER_SIZE = format.getNextOffset() * (num_quads * 4);

    @Test//Test the expansion of the buffer with addVertexData
    public void testAddVertexExpansion()
    {
        BufferBuilder buffer = new BufferBuilder(BUFFER_SIZE / 4);
        int prevCap = buffer.getByteBuffer().capacity();

        int[] ints = new int[3 * 4];// 3 floats per the 4 vertices.
        Arrays.fill(ints, Float.floatToIntBits(1.233F));//Just a random value.

        buffer.begin(0x07, format);
        for (int i = 0; i < num_quads + 2; i++)
        {
            buffer.addVertexData(ints);
        }
        buffer.finishDrawing();

        assertTrue("BufferBuilder's capacity didn't change.", buffer.getByteBuffer().capacity() > prevCap);
    }

    @Test//Test the expansion of the buffer with endVertex.
    public void testEndVertexExpansion()
    {
        BufferBuilder buffer = new BufferBuilder(BUFFER_SIZE / 4);
        int prevCap = buffer.getByteBuffer().capacity();

        buffer.begin(0x07, format);
        for (int i = 0; i < num_quads + 2; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                buffer.pos(1.233, 1.233, 1.233).endVertex();
            }
        }
        buffer.finishDrawing();

        assertTrue("BufferBuilder's capacity didn't change.", buffer.getByteBuffer().capacity() > prevCap);
    }

    @Test//Test the expansion of the buffer if addVertexData fills it and pos / tex / endVertex is used.
    public void testMixedExpansion()
    {
        BufferBuilder buffer = new BufferBuilder(BUFFER_SIZE / 4);
        int prevCap = buffer.getByteBuffer().capacity();

        int[] ints = new int[3 * 4];
        Arrays.fill(ints, Float.floatToIntBits(1.233F));

        buffer.begin(0x07, format);
        for (int i = 0; i < num_quads; i++)
        {
            buffer.addVertexData(ints);
        }

        for (int i = 0; i < num_quads + 2; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                buffer.pos(1.233, 1.233, 1.233).endVertex();
            }
        }
        buffer.finishDrawing();

        assertTrue("BufferBuilder's capacity didn't change.", buffer.getByteBuffer().capacity() > prevCap);
    }

    @Test//Test the expansion of the buffer with putBulkData
    public void testAddVertexExpansionBytes()
    {
        BufferBuilder buffer = new BufferBuilder(BUFFER_SIZE / 4);
        int prevCap = buffer.getByteBuffer().capacity();

        ByteBuffer buf = ByteBuffer.allocate(3 * 4 * 4); //3 floats per the 4 verticles (32bit float as 4 8bit bytes)
        for(int i=0;i<4;i++)
            buf.putFloat(1.233F); //Just a random value.
        
        buffer.begin(0x07, format);
        for (int i = 0; i < num_quads + 2; i++)
        {
            buffer.putBulkData(buf);
        }
        buffer.finishDrawing();

        assertTrue("BufferBuilder's capacity didn't change.", buffer.getByteBuffer().capacity() > prevCap);
    }

    @Test//Test the expansion of the buffer if putBulkData fills it and pos / tex / endVertex is used.
    public void testMixedExpansionBytes()
    {
        BufferBuilder buffer = new BufferBuilder(BUFFER_SIZE / 4);
        int prevCap = buffer.getByteBuffer().capacity();

        ByteBuffer buf = ByteBuffer.allocate(3 * 4 * 4);
        for(int i=0;i<4;i++)
            buf.putFloat(1.233F);

        buffer.begin(0x07, format);
        for (int i = 0; i < num_quads; i++)
        {
            buffer.putBulkData(buf);
        }

        for (int i = 0; i < num_quads + 2; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                buffer.pos(1.233, 1.233, 1.233).endVertex();
            }
        }
        buffer.finishDrawing();

        assertTrue("BufferBuilder's capacity didn't change.", buffer.getByteBuffer().capacity() > prevCap);
    }
}
