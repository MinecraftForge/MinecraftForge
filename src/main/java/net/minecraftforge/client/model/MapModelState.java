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

import java.util.Map;

import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import com.google.common.base.Objects;
import java.util.Optional;
import com.google.common.collect.ImmutableMap;

/*
 * Simple implementation of IModelState via a map and a default value. Provides a full state for each part.
 * You probably don't want to use this.
 */
public class MapModelState implements IModelState
{
    private final ImmutableMap<Wrapper, IModelState> map;
    private final IModelState def;

    public MapModelState(Map<Wrapper, IModelState> map)
    {
        this(map, TRSRTransformation.identity());
    }

    public MapModelState(Map<Wrapper, IModelState> map, TRSRTransformation def)
    {
        this(map, (IModelState)def);
    }

    public MapModelState(Map<Wrapper, IModelState> map, IModelState def)
    {
        this.map = ImmutableMap.copyOf(map);
        this.def = def;
    }

    @Override
    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        if(!part.isPresent() || !map.containsKey(part.get())) return def.apply(part);
        return map.get(part.get()).apply(Optional.empty());
    }

    public IModelState getState(Object obj)
    {
        Wrapper w = wrap(obj);
        if(!map.containsKey(w)) return def;
        return map.get(w);
    }

    public static class Wrapper implements IModelPart
    {
        private final Object obj;

        public Wrapper(Object obj)
        {
            this.obj = obj;
        }

        @Override
        public int hashCode()
        {
            return obj.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Wrapper other = (Wrapper)obj;
            return Objects.equal(this.obj, other.obj);
        }
    }

    public static Wrapper wrap(Object obj)
    {
        return new Wrapper(obj);
    }
}
