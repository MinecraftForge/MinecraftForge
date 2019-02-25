package net.minecraftforge.client.model.data;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class ModelDataMap implements IModelData
{
    private final Set<ModelProperty<?>> validProperties;
    private final Map<ModelProperty<?>, Object> backingMap;
    
    private ModelDataMap(Map<ModelProperty<?>, Object> map)
    {
        this.validProperties = Sets.newIdentityHashSet();
        this.validProperties.addAll(map.keySet());
        this.backingMap = new IdentityHashMap<>(map);
    }

    @Override
    public boolean hasProperty(ModelProperty<?> prop)
    {
        return validProperties.contains(prop);
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
        Preconditions.checkArgument(hasProperty(prop), "Cannot set model property here as it does not exist");
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
