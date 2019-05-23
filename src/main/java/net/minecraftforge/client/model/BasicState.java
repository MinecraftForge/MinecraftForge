package net.minecraftforge.client.model;

import net.minecraft.client.renderer.texture.ISprite;
import net.minecraftforge.common.model.IModelState;

public class BasicState implements ISprite
{
    private final IModelState defaultState;
    private final boolean uvLock;

    public BasicState(IModelState defaultState, boolean uvLock)
    {
        this.defaultState = defaultState;
        this.uvLock = uvLock;
    }

    @Override
    public IModelState getState()
    {
        return defaultState;
    }

    @Override
    public boolean isUvLock()
    {
        return uvLock;
    }
}
