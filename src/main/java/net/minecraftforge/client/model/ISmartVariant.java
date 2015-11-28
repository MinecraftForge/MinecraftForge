package net.minecraftforge.client.model;


public interface ISmartVariant
{
    IModel process(IModel base, ModelLoader loader);
}
