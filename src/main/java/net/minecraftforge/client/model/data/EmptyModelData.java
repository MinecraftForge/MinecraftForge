package net.minecraftforge.client.model.data;

public enum EmptyModelData implements IModelData
{
    INSTANCE;
    
    @Override
    public boolean hasProperty(ModelProperty<?> prop) { return false; }

    @Override
    public <T> T getData(ModelProperty<T> prop) { return null; }

    @Override
    public <T> T setData(ModelProperty<T> prop, T data) { return null; }
}
