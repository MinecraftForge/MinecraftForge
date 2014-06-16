package net.minecraftforge.permissions.api.context;

import net.minecraft.world.World;
import net.minecraftforge.permissions.api.context.IContext.IDimensionContext;

public class WorldContext implements IDimensionContext
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
