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

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

public class BatchedBufferConfig
{
    public static final BatchedBufferConfig BLOCK = new BatchedBufferConfig(DefaultVertexFormats.BLOCK, GL11.GL_QUADS, BlockBufferDrawer.INSTANCE);

    public final VertexFormat vertexFormat;
    public final int glMode;
    public final IBufferDrawer drawer;

    public BatchedBufferConfig(VertexFormat vertexFormat, int glMode, IBufferDrawer drawer)
    {
        this.vertexFormat = vertexFormat;
        this.glMode = glMode;
        this.drawer = drawer;
    }
}
