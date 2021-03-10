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

/**
 * Assumes that the data length is not less than e.getElements().size().
 * Also assumes that element index passed will increment from 0 to format.getElements().size() - 1.
 * Normal, Color and UV are assumed to be in 0-1 range.
 */
public interface IVertexConsumer
{
    /**
     * @return the format that should be used for passed data.
     */
    VertexFormat getVertexFormat();

    void setQuadTint(int tint);
    void setQuadOrientation(Direction orientation);
    void setApplyDiffuseLighting(boolean diffuse);
    void setTexture(TextureAtlasSprite texture);
    void put(int element, float... data);
}
