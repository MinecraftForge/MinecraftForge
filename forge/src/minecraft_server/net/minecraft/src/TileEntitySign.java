package net.minecraft.src;

public class TileEntitySign extends TileEntity
{
    /** An array of four strings storing the lines of text on the sign. */
    public String[] signText = new String[] {"", "", "", ""};

    /**
     * The index of the line currently being edited. Only used on client side, but defined on both. Note this is only
     * really used when the > < are going to be visible.
     */
    public int lineBeingEdited = -1;
    private boolean isEditable = true;

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setString("Text1", this.signText[0]);
        par1NBTTagCompound.setString("Text2", this.signText[1]);
        par1NBTTagCompound.setString("Text3", this.signText[2]);
        par1NBTTagCompound.setString("Text4", this.signText[3]);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.isEditable = false;
        super.readFromNBT(par1NBTTagCompound);

        for (int var2 = 0; var2 < 4; ++var2)
        {
            this.signText[var2] = par1NBTTagCompound.getString("Text" + (var2 + 1));

            if (this.signText[var2].length() > 15)
            {
                this.signText[var2] = this.signText[var2].substring(0, 15);
            }
        }
    }

    /**
     * Overriden in a sign to provide the text
     */
    public Packet getDescriptionPacket()
    {
        String[] var1 = new String[4];

        for (int var2 = 0; var2 < 4; ++var2)
        {
            var1[var2] = this.signText[var2];
        }

        return new Packet130UpdateSign(this.xCoord, this.yCoord, this.zCoord, var1);
    }

    public boolean isEditable()
    {
        return this.isEditable;
    }
}
