package net.minecraftforge.permissions.api.context;

import net.minecraft.world.World;
import net.minecraftforge.permissions.api.context.IContext.IDimensionContext;

/**
 * Default context for a world. Feel free to use or override.
 */
public class WorldContext implements IDimensionContext
{
    private final World world;

    public WorldContext(World world)
    {
        this.world = world;
    }

    public int getDimensionId()
    {
        return world.provider.dimensionId;
    }
    
    public World getWorld()
    {
        return world;
    }
}
