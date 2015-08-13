package net.minecraftforge.client.model;

import net.minecraft.client.renderer.block.model.ModelBlock;

public interface ISmartVariant
{
    IModel process(IModel base, ModelLoader loader);
}
