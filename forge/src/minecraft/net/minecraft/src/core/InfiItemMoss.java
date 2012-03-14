package net.minecraft.src.core;

import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.*;

public class InfiItemMoss extends Item
    implements ITextureProvider
{
    public String texturePath;

    public InfiItemMoss(int i, String s)
    {
        super(i);
        texturePath = s;
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l)
    {
        if (l != 1)
        {
            return false;
        }
        if (!entityplayer.canPlayerEdit(i, j, k) || !entityplayer.canPlayerEdit(i, j + 1, k))
        {
            return false;
        }
        int i1 = world.getBlockId(i, j, k);
        if (Block.blocksList[i1].renderAsNormalBlock() && world.isAirBlock(i, j + 1, k))
        {
            world.setBlockWithNotify(i, j + 1, k, mod_InfiTools.blockMoss.blockID);
            itemstack.stackSize--;
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getTextureFile()
    {
        return texturePath;
    }
}
