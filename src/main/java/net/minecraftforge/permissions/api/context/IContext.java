package net.minecraftforge.permissions.api.context;

import java.util.List;

import net.minecraft.block.Block;

/**
 * This is a marker context for all other Context classes.
 * Contexts are not meant to be saved and never should be. Use them once, and only once.
 * 
 * For ready-made contexts, refer to the other classes in these packages.
 */
public interface IContext 
{
    public interface IAreaContext extends IContext
    {
        boolean overlapsWith(IAreaContext context);

        boolean contains(IAreaContext area);

        boolean contains(ILocationContext loc);

        List<IBlockContext> getBlocks();
    }
    
    public interface IBlockContext extends ILocationContext
    {
        Block getBlock();

        boolean hasTileEntity();
    }
    
    public interface IDimensionContext extends IContext
    {
        int getDimensionId();
    }
    
    public interface IHealthContext extends IContext
    {
        float getMaxHealth();

        float getCurrentHealth();
    }
    
    public interface ILocationContext extends IDimensionContext
    {
        double getX();

        double getY();

        double getZ();
    }
    
    public interface IRotationContext extends IContext
    {
        float getPitch();

        float getYaw();
    }
}
