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

package net.minecraftforge.client.render;

import net.minecraft.client.renderer.Tessellator;

import java.util.LinkedHashMap;
import java.util.Map;

public class BatchedTileEntityRenderer
{

    private static final Map<BatchedBufferConfig, Tessellator> BATCHED_BUFFERS = new LinkedHashMap<>();
    private static boolean drawingBatch = false;

    static
    {
        registerBatchedVertexFormat(BatchedBufferConfig.BLOCK);
    }

    public static void registerBatchedVertexFormat(BatchedBufferConfig config)
    {
        BATCHED_BUFFERS.put(config, new Tessellator(0x20000));
    }

    /**
     * Prepare for a batched TESR rendering.
     * You probably shouldn't call this manually.
     */
    public static void preDrawBatch()
    {
        for (Map.Entry<BatchedBufferConfig, Tessellator> entry : BATCHED_BUFFERS.entrySet())
        {
            BatchedBufferConfig config = entry.getKey();
            entry.getValue().getBuffer().begin(config.glMode, config.vertexFormat);
        }
        drawingBatch = true;
    }

    /**
     * Render all TESRs batched so far.
     * You probably shouldn't call this manually.
     */
    public static void drawBatch(int pass)
    {
        for (Map.Entry<BatchedBufferConfig, Tessellator> entry : BATCHED_BUFFERS.entrySet())
        {
            BatchedBufferConfig config = entry.getKey();
            Tessellator tessellator = entry.getValue();
            config.drawer.preDraw(pass, tessellator.getBuffer());
            tessellator.draw();
            config.drawer.postDraw(pass);
        }

        drawingBatch = false;
    }

    public static boolean isDrawingBatch()
    {
        return drawingBatch;
    }

    public static Map<BatchedBufferConfig, Tessellator> getAll()
    {
        return BATCHED_BUFFERS;
    }
}
