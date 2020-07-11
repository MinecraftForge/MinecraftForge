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

import net.minecraft.client.renderer.vertex.VertexFormat;

public abstract class QuadGatheringTransformer implements IVertexConsumer
{
    protected IVertexConsumer parent;
    protected VertexFormat format;
    protected int vertices = 0;

    protected byte[] dataLength = null;
    protected float[][][] quadData = null;

    public void setParent(IVertexConsumer parent)
    {
        this.parent = parent;
    }

    public void setVertexFormat(VertexFormat format)
    {
        this.format = format;
        dataLength = new byte[format.getElements().size()];
        quadData = new float[format.getElements().size()][4][4];
    }

    @Override
    public VertexFormat getVertexFormat()
    {
        return format;
    }

    @Override
    public void put(int element, float... data)
    {
        System.arraycopy(data, 0, quadData[element][vertices], 0, data.length);
        if (vertices == 0)
        {
            dataLength[element] = (byte)data.length;
        }
        if (element == getVertexFormat().getElements().size() - 1)
        {
            vertices++;
        }
        if (vertices == 4)
        {
            vertices = 0;
            processQuad();
        }
    }

    protected abstract void processQuad();
}
