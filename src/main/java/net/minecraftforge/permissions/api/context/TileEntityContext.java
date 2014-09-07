package net.minecraftforge.permissions.api.context;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.api.context.IContext.IBlockContext;

/**
 * Default context for tile entities. Feel free to use or override.
 */
public class TileEntityContext implements IBlockContext{
    
    private int x, y, z, dim;
    private TileEntity entity;

    public TileEntityContext(TileEntity entity)
    {
        x = entity.xCoord;
        y = entity.yCoord;
        z = entity.zCoord;
        dim = entity.getWorldObj().provider.dimensionId;
        this.entity = entity;
    }

    public Block getBlock()
    {
        return DimensionManager.getWorld(dim).getBlock(x, y, z);
    }

    public TileEntity getTileEntity()
    {
        return entity;
    }

    @Override
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
    }

    @Override
    public double getZ()
    {
        return z;
    }

    @Override
    public int getDimensionId()
    {
        return dim;
    }

    @Override
    public boolean hasTileEntity()
    {
        return true;
    }
}
