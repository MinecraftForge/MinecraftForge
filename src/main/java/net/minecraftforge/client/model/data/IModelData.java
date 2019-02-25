package net.minecraftforge.client.model.data;

public interface IModelData
{
    boolean hasProperty(ModelProperty<?> prop);
    
    <T> T getData(ModelProperty<T> prop);
    
    <T> T setData(ModelProperty<T> prop, T data);
}
