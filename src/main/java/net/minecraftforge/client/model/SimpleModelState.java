/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.IModelTransform;

import com.google.common.collect.ImmutableMap;

/**
 * Simple implementation of IModelState via a map and a default value.
 */
public final class SimpleModelState implements IModelTransform
{
    private final ImmutableMap<? extends Object, TransformationMatrix> map;
    private final TransformationMatrix def;

    public SimpleModelState(ImmutableMap<? extends Object, TransformationMatrix> map)
    {
        this(map, TransformationMatrix.func_227983_a_());
    }

    public SimpleModelState(ImmutableMap<? extends Object, TransformationMatrix> map, TransformationMatrix def)
    {
        this.map = map;
        this.def = def;
    }

    @Override
    public TransformationMatrix func_225615_b_()
    {
        return def;
    }

    @Override
    public TransformationMatrix getPartTransformation(Object part)
    {
        return map.getOrDefault(part, TransformationMatrix.func_227983_a_());
    }
}
