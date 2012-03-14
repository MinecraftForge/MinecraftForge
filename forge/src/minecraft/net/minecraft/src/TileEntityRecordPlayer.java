package net.minecraft.src;

public class TileEntityRecordPlayer extends TileEntity
{
    /** ID of record which is in Jukebox */
    public int record;

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.record = par1NBTTagCompound.getInteger("Record");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);

        if (this.record > 0)
        {
            par1NBTTagCompound.setInteger("Record", this.record);
        }
    }
}
