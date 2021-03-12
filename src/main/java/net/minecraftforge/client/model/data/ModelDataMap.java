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

package net.minecraftforge.client.model.data;

import java.util.IdentityHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public class ModelDataMap implements IModelData
{
    private final Map<ModelProperty<?>, Object> backingMap;
    
    private ModelDataMap(Map<ModelProperty<?>, Object> map)
    {
        this.backingMap = new IdentityHashMap<>(map);
    }

    protected ModelDataMap()
    {
        this.backingMap = new IdentityHashMap<>();
    }

    @Override
    public boolean hasProperty(ModelProperty<?> prop)
    {
        return backingMap.containsKey(prop);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getData(ModelProperty<T> prop)
    {
        return (T) backingMap.get(prop);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T setData(ModelProperty<T> prop, T data)
    {
        Preconditions.checkArgument(prop.test(data), "Value is invalid for this property");
        return (T) backingMap.put(prop, data);
    }
    
    public static class Builder
    {
        private final Map<ModelProperty<?>, Object> defaults = new IdentityHashMap<>();
        
        public Builder withProperty(ModelProperty<?> prop)
        {
            return withInitial(prop, null);
        }
        
        public <T> Builder withInitial(ModelProperty<T> prop, T data)
        {
            this.defaults.put(prop, data);
            return this;
        }
        
        public ModelDataMap build()
        {
            return new ModelDataMap(defaults);
        }
    }
}
