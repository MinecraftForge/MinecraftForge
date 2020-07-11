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

package net.minecraftforge.client.model;

import com.google.common.base.Objects;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.client.renderer.model.IModelTransform;

/**
 * An {@link IModelTransform} that combines the transforms from two child {@link IModelTransform}.
 */
public class ModelTransformComposition implements IModelTransform
{
    private final IModelTransform first;
    private final IModelTransform second;
    private final boolean uvLock;

    public ModelTransformComposition(IModelTransform first, IModelTransform second)
    {
        this(first, second, false);
    }

    public ModelTransformComposition(IModelTransform first, IModelTransform second, boolean uvLock)
    {
        this.first = first;
        this.second = second;
        this.uvLock = uvLock;
    }

    @Override
    public boolean isUvLock()
    {
        return uvLock;
    }

    @Override
    public TransformationMatrix getRotation()
    {
        return first.getRotation().compose(second.getRotation());
    }

    @Override
    public TransformationMatrix getPartTransformation(Object part)
    {
        return first.getPartTransformation(part).compose(second.getPartTransformation(part));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        ModelTransformComposition that = (ModelTransformComposition) o;
        return Objects.equal(first, that.first) && Objects.equal(second, that.second);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(first, second);
    }
}
