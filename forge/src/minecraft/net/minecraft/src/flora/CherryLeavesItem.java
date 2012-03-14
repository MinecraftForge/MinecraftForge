package net.minecraft.src.flora;

import net.minecraft.src.*;
import net.minecraft.src.forge.MinecraftForgeClient;

public class CherryLeavesItem extends ItemBlock
{
    /*public static final String blockType[] =
    {
    };*/

    public CherryLeavesItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getIconFromDamage(int i)
    {
        return mod_FloraSoma.cloud.getBlockTextureFromSideAndMetadata(0, i);
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
