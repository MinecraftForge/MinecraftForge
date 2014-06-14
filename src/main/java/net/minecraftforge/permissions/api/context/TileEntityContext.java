package net.minecraftforge.permissions.api.context;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class TileEntityContext extends Point{
    
    private int x, y, z, dim;
    private Block block;
    private TileEntity hasTE;

    public TileEntityContext(TileEntity entity)
    {
        super(entity.xCoord, entity.yCoord, entity.zCoord, entity.getWorldObj().provider.dimensionId);
        block = entity.getBlockType();
        hasTE = entity;
    }

    public Block getBlock()
    {
        return block;
    }

    public TileEntity getTileEntity()
    {
        return hasTE;
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
}
