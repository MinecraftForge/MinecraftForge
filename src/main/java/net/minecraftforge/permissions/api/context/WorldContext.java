package net.minecraftforge.permissions.api.context;

import net.minecraft.world.World;

public class WorldContext implements IContext
{
    private final int id;

    public WorldContext(World world)
    {
        id = world.provider.dimensionId;
    }

    public int getDimensionId()
    {
        return id;
    }
}
