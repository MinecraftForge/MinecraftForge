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

package net.minecraftforge.client.model;

import net.minecraft.client.renderer.model.IModelTransform;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.math.vector.TransformationMatrix;

/**
 * Simple implementation of IModelState via a map and a default value.
 */
public final class SimpleModelTransform implements IModelTransform
{
    public static final SimpleModelTransform IDENTITY = new SimpleModelTransform(TransformationMatrix.identity());

    private final ImmutableMap<?, TransformationMatrix> map;
    private final TransformationMatrix base;

    public SimpleModelTransform(ImmutableMap<?, TransformationMatrix> map)
    {
        this(map, TransformationMatrix.identity());
    }

    public SimpleModelTransform(TransformationMatrix base)
    {
        this(ImmutableMap.of(), base);
    }

    public SimpleModelTransform(ImmutableMap<?, TransformationMatrix> map, TransformationMatrix base)
    {
        this.map = map;
        this.base = base;
    }

    @Override
    public TransformationMatrix getRotation()
    {
        return base;
    }

    @Override
    public TransformationMatrix getPartTransformation(Object part)
    {
        return map.getOrDefault(part, TransformationMatrix.identity());
    }
}
