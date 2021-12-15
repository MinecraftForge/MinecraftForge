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

package net.minecraftforge.client.animation;

import net.minecraft.client.model.geom.ModelPart;

import java.util.Map;

public interface IAnimatedModel
{
    default void gatherAnimatedParts(Map<String, ModelPart> partMap)
    {}

    /**
     * Helper method to fill part map from an iterable of model parts. The name is of each part is
     * created with the provided prefix plus it's index. For instance, a prefix of "body" will create
     * the name "body_0", "body_1" and so on.
     *
     * @param partMap the map to put the parts
     * @param parts an iterable of model parts
     * @param prefix the name to prefix for each part
     */
    static void fillFromIterable(Map<String, ModelPart> partMap, Iterable<ModelPart> parts, String prefix)
    {
        int i = 0;
        for(ModelPart part : parts) partMap.put(prefix + "_" + i++, part);
    }
}
