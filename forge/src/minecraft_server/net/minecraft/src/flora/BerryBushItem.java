package net.minecraft.src.flora;

import net.minecraft.src.*;

public class BerryBushItem extends ItemBlock
{
    public static final String blockType[] =
    {
        "rasp", "blue", "black", "geo", "rasp", "blue", "black", "geo", "rasp", "blue",
        "black", "geo", "rasp", "blue", "black", "geo"
    };

    public BerryBushItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
    }
    
    @Override
    public int getMetadata(int md)
    {
        return md;
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l)
    {
        if (!entityplayer.canPlayerEdit(i, j, k) || !entityplayer.canPlayerEdit(i, j + 1, k))
        {
            return false;
        }
        int i1 = world.getBlockId(i, j, k);
        if ((i1 == Block.dirt.blockID || i1 == Block.grass.blockID || i1 == mod_FloraSoma.berryBush.blockID) && world.isAirBlock(i, j + 1, k))
        {
            world.setBlockAndMetadataWithNotify(i, j + 1, k, mod_FloraSoma.berryBush.blockID, itemstack.getItemDamage());
            itemstack.stackSize--;
            world.playAuxSFX(2001, i, j, k, Block.grass.blockID);
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("berryBush").toString();
    }
}
