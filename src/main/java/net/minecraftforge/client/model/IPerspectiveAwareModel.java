/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

/*
 * Model that changes based on the rendering perspective
 * (first-person, GUI, e.t.c - see TransformType)
 */
@Deprecated
public interface IPerspectiveAwareModel extends IBakedModel
{
    @Deprecated
    public static class MapWrapper extends PerspectiveMapWrapper {
        public MapWrapper(IBakedModel parent, IModelState state)
        {
            super(parent, state);
        }

        public MapWrapper(IBakedModel parent, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms)
        {
            super(parent, transforms);
        }
    }
}
