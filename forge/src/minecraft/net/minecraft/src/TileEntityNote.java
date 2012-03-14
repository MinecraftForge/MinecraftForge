package net.minecraft.src;

public class TileEntityNote extends TileEntity
{
    /** Note to play */
    public byte note = 0;

    /** stores the latest redstone state */
    public boolean previousRedstoneState = false;

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setByte("note", this.note);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.note = par1NBTTagCompound.getByte("note");

        if (this.note < 0)
        {
            this.note = 0;
        }

        if (this.note > 24)
        {
            this.note = 24;
        }
    }

    /**
     * change pitch by -> (currentPitch + 1) % 25
     */
    public void changePitch()
    {
        this.note = (byte)((this.note + 1) % 25);
        this.onInventoryChanged();
    }

    /**
     * plays the stored note
     */
    public void triggerNote(World par1World, int par2, int par3, int par4)
    {
        if (par1World.getBlockMaterial(par2, par3 + 1, par4) == Material.air)
        {
            Material var5 = par1World.getBlockMaterial(par2, par3 - 1, par4);
            byte var6 = 0;

            if (var5 == Material.rock)
            {
                var6 = 1;
            }

            if (var5 == Material.sand)
            {
                var6 = 2;
            }

            if (var5 == Material.glass)
            {
                var6 = 3;
            }

            if (var5 == Material.wood)
            {
                var6 = 4;
            }

            par1World.playNoteAt(par2, par3, par4, var6, this.note);
        }
    }
}
