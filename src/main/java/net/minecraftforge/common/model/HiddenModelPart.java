package net.minecraftforge.common.model;

import com.google.common.collect.ImmutableList;

final class HiddenModelPart implements IModelPart
{
    private final ImmutableList<String> path;

    HiddenModelPart(ImmutableList<String> path)
    {
        this.path = path;
    }

    ImmutableList<String> getPath()
    {
        return path;
    }
}
