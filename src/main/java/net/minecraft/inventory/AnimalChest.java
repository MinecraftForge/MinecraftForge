package net.minecraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AnimalChest extends InventoryBasic
{
    private static final String __OBFID = "CL_00001731";

    public AnimalChest(String par1Str, int par2)
    {
        super(par1Str, false, par2);
    }

    @SideOnly(Side.CLIENT)
    public AnimalChest(String par1Str, boolean par2, int par3)
    {
        super(par1Str, par2, par3);
    }
}