package net.minecraftforge.client.model.data;

import javax.annotation.Nonnull;

public interface IModelData
{
    boolean hasProperty(IModelProperty<?> prop);
    
    <T> T getData(IModelProperty<T> prop);
    
    <T> T setData(IModelProperty<T> prop, T data);
}
