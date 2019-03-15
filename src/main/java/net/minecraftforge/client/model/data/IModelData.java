package net.minecraftforge.client.model.data;

import javax.annotation.Nullable;

public interface IModelData
{
    boolean hasProperty(ModelProperty<?> prop);
    
    @Nullable
    <T> T getData(ModelProperty<T> prop);
    
    @Nullable
    <T> T setData(ModelProperty<T> prop, T data);
}
