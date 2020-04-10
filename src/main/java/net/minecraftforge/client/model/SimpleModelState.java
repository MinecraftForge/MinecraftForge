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

import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Optional;
import com.google.common.collect.ImmutableMap;

/*
 * Simple implementation of IModelState via a map and a default value.
 */
public final class SimpleModelState implements IModelState
{
    private final ImmutableMap<? extends IModelPart, TRSRTransformation> map;
    private final Optional<TRSRTransformation> def;

    public SimpleModelState(ImmutableMap<? extends IModelPart, TRSRTransformation> map)
    {
        this(map, Optional.empty());
    }

    public SimpleModelState(ImmutableMap<? extends IModelPart, TRSRTransformation> map, Optional<TRSRTransformation> def)
    {
        this.map = map;
        this.def = def;
    }

    @Override
    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        if(!part.isPresent())
        {
            return def;
        }
        if(!map.containsKey(part.get()))
        {
            return Optional.empty();
        }
        return Optional.ofNullable(map.get(part.get()));
    }
}
