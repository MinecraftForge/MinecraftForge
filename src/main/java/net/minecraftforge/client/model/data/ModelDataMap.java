package net.minecraftforge.client.model.data;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class ModelDataMap implements IModelData
{
    private final Set<IModelProperty<?>> validProperties;
    private final Map<IModelProperty<?>, Object> backingMap;
    
    private ModelDataMap(Map<IModelProperty<?>, Object> map)
    {
        this.validProperties = Sets.newIdentityHashSet();
        this.validProperties.addAll(map.keySet());
        this.backingMap = new IdentityHashMap<>(map);
    }

    @Override
    public boolean hasProperty(IModelProperty<?> prop)
    {
        return validProperties.contains(prop);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getData(IModelProperty<T> prop)
    {
        return (T) backingMap.get(prop);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T setData(IModelProperty<T> prop, T data)
    {
        Preconditions.checkArgument(hasProperty(prop), "Cannot set model property here as it does not exist");
        return (T) backingMap.put(prop, data);
    }
    
    public static class Builder
    {
        private final Map<IModelProperty<?>, Object> defaults = new IdentityHashMap<>();
        
        public Builder withProperty(IModelProperty<?> prop)
        {
            return withInitial(prop, null);
        }
        
        public <T> Builder withInitial(IModelProperty<T> prop, T data)
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
