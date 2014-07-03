package net.minecraftforge.permissions.api.context;

import java.util.List;

import net.minecraft.block.Block;

/**
 * This is a marker context for all other Context classes.
 * <p>
 *     Any class implementing IContext should be immutable and not carry any potentially problematic references.
 *     Problematic references include but are not limited to instances of World, Entity, and TileEntity.
 *     It is best instead for contexts to supply information in the way of IDs or locations in order to obtain
 *     the desired instances. It is also a good idea to simply copy the information into primitive types and allow
 *     them to be accessible va getters.
 * </p>
 */
public interface IContext 
{
    public interface IAreaContext extends IContext
    {
        boolean overlapsWith(IAreaContext context);

        boolean contains(IAreaContext area);

        boolean contains(ILocationContext loc);

        List<IBlockLocationContext> getLocations();
    }
    
    public interface IBlockContext extends IBlockLocationContext
    {
        Block getBlock();

        boolean hasTileEntity();
    }
    
    public interface IBlockLocationContext extends ILocationContext
    {
        int getBlockX();

        int getBlockY();

        int getBlockZ();
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
