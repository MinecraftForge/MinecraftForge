package net.minecraft.src.flora;

import net.minecraft.src.*;
import net.minecraft.src.forge.MinecraftForgeClient;

public class FloraLeavesItem extends ItemBlock
{
    /*public static final String blockType[] =
    {
        "normal", "dark", "ash", "sulfur"
    };*/

    public FloraLeavesItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_FloraSoma.floraLeaves.blockID, this);
    }

    @Override
    public int getMetadata(int md)
    {
        return md;
    }

    /*public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("cloud").toString();
    }*/
}
