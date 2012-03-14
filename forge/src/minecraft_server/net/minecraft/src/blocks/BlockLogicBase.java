package net.minecraft.src.blocks;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class BlockLogicBase extends TileEntity
{
    protected boolean created;
    private boolean active;
    private short facing;
    public boolean prevActive;
    public short prevFacing;

    public BlockLogicBase()
    {
        created = false;
        active = false;
        facing = 0;
        prevActive = false;
        prevFacing = 0;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        prevFacing = facing = nbttagcompound.getShort("facing");
    }

    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("facing", facing);
    }

    public boolean getActive()
    {
        return active;
    }

    public void setActive(boolean flag)
    {
        active = flag;
    }

    public void setActiveWithoutNotify(boolean flag)
    {
        active = flag;
        prevActive = flag;
    }

    public short getFacing()
    {
        return facing;
    }

    public void setFacing(short word0)
    {
        facing = word0;
    }
}
