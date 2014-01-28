package net.minecraft.block;

import net.minecraft.dispenser.IBlockSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSourceImpl implements IBlockSource
{
    private final World worldObj;
    private final int xPos;
    private final int yPos;
    private final int zPos;
    private static final String __OBFID = "CL_00001194";

    public BlockSourceImpl(World par1World, int par2, int par3, int par4)
    {
        this.worldObj = par1World;
        this.xPos = par2;
        this.yPos = par3;
        this.zPos = par4;
    }

    public World getWorld()
    {
        return this.worldObj;
    }

    public double getX()
    {
        return (double)this.xPos + 0.5D;
    }

    public double getY()
    {
        return (double)this.yPos + 0.5D;
    }

    public double getZ()
    {
        return (double)this.zPos + 0.5D;
    }

    public int getXInt()
    {
        return this.xPos;
    }

    public int getYInt()
    {
        return this.yPos;
    }

    public int getZInt()
    {
        return this.zPos;
    }

    public int getBlockMetadata()
    {
        return this.worldObj.getBlockMetadata(this.xPos, this.yPos, this.zPos);
    }

    public TileEntity func_150835_j()
    {
        return this.worldObj.func_147438_o(this.xPos, this.yPos, this.zPos);
    }
}