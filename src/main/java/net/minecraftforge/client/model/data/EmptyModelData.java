package net.minecraftforge.client.model.data;

public enum EmptyModelData implements IModelData
{
    INSTANCE;

    @Override
    public boolean hasProperty(IModelProperty<?> prop) { return false; }

    @Override
    public <T> T getData(IModelProperty<T> prop) { return null; }

    @Override
    public <T> T setData(IModelProperty<T> prop, T data) { return null; }
}
