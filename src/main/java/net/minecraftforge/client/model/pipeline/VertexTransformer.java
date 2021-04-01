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

package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;

public class VertexTransformer implements IVertexConsumer
{
    protected final IVertexConsumer parent;

    public VertexTransformer(IVertexConsumer parent)
    {
        this.parent = parent;
    }

    @Override
    public VertexFormat getVertexFormat()
    {
        return parent.getVertexFormat();
    }

    @Override
    public void setQuadTint(int tint)
    {
        parent.setQuadTint(tint);
    }

    @Override
    public void setTexture(TextureAtlasSprite texture)
    {
        parent.setTexture(texture);
    }

    @Override
    public void setQuadOrientation(Direction orientation)
    {
        parent.setQuadOrientation(orientation);
    }

    @Override
    public void setApplyDiffuseLighting(boolean diffuse)
    {
        parent.setApplyDiffuseLighting(diffuse);
    }

    @Override
    public void put(int element, float... data)
    {
        parent.put(element, data);
    }
}
