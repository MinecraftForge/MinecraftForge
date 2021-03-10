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

package net.minecraftforge.client.extensions;

import net.minecraft.util.math.vector.TransformationMatrix;

/**
 * An {@code IModelState} is a function from model part to a transformation that should be applied
 * when that part is baked, thus representing the current "state" of the model and its parts.
 */
public interface IForgeModelTransform
{
    /**
     * @param part Part of the model we are wanting to transform. An empty optional means
     *             we want a transform for the entire model.
     * @return A transformation to apply to the part, if any. The coordinate system of the transform
     *         is determined by the part type.
     */
    default TransformationMatrix getPartTransformation(Object part)
    {
        return TransformationMatrix.identity();
    }
}
