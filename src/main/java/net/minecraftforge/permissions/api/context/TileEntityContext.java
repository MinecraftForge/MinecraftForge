package net.minecraftforge.permissions.api.context;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.permissions.api.context.IContext.IBlockContext;

public class TileEntityContext implements IBlockContext{
    
    private int x, y, z, dim;
    private Block block;
    private TileEntity tileEntity;

    public TileEntityContext(TileEntity entity)
    {
        block = entity.getBlockType();
        tileEntity = entity;
    }

    public Block getBlock()
    {
        return block;
    }

    public TileEntity getTileEntity()
    {
        return tileEntity;
    }

    public int getBlockX()
    {
        return x;
    }

    public int getBlockY()
    {
        return y;
    }

    public int getBlockZ()
    {
        return z;
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
        return getTileEntity().getWorldObj().provider.dimensionId;
    }

    @Override
    public boolean hasTileEntity()
    {
        return false;
    }
}
