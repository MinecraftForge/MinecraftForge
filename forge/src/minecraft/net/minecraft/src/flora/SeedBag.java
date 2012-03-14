package net.minecraft.src.flora;

import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.*;

public class SeedBag extends Item
    implements ITextureProvider
{
    private int blockType;
    private int soilBlock;

    public SeedBag(int i)
    {
        super(i);
        blockType = Block.crops.blockID;
        soilBlock = Block.tilledField.blockID;
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
        boolean flag = false;
        for (int i1 = i - 1; i1 <= i + 1; i1++)
        {
            for (int j1 = j + 1; j1 <= j + 1; j1++)
            {
                for (int k1 = k - 1; k1 <= k + 1; k1++)
                {
                    int l1 = world.getBlockId(i1, j1 - 1, k1);
                    if (l1 == soilBlock && world.isAirBlock(i, j + 1, k))
                    {
                        world.setBlockWithNotify(i1, j1, k1, blockType);
                        flag = true;
                    }
                }
            }
        }

        if (flag)
        {
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
