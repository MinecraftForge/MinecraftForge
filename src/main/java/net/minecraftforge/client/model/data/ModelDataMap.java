/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
