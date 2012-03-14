package net.minecraft.src.flora;

import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.*;

public class FloraSeeds extends Item
    implements ITextureProvider
{
    private int blockType;
    private int plantableBlock;

    public FloraSeeds(int i, int j, int k)
    {
        super(i);
        blockType = j;
        plantableBlock = k;
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
        if (i1 == plantableBlock && world.isAirBlock(i, j + 1, k))
        {
            world.setBlockWithNotify(i, j + 1, k, blockType);
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
        return "/floratex/seeds.png";
    }
}
