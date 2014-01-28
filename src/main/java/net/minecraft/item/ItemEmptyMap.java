package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase
{
    private static final String __OBFID = "CL_00000024";

    protected ItemEmptyMap()
    {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    // JAVADOC METHOD $$ func_77659_a
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        ItemStack itemstack1 = new ItemStack(Items.filled_map, 1, par2World.getUniqueDataId("map"));
        String s = "map_" + itemstack1.getItemDamage();
        MapData mapdata = new MapData(s);
        par2World.setItemData(s, mapdata);
        mapdata.scale = 0;
        int i = 128 * (1 << mapdata.scale);
        mapdata.xCenter = (int)(Math.round(par3EntityPlayer.posX / (double)i) * (long)i);
        mapdata.zCenter = (int)(Math.round(par3EntityPlayer.posZ / (double)i) * (long)i);
        mapdata.dimension = (byte)par2World.provider.dimensionId;
        mapdata.markDirty();
        --par1ItemStack.stackSize;

        if (par1ItemStack.stackSize <= 0)
        {
            return itemstack1;
        }
        else
        {
            if (!par3EntityPlayer.inventory.addItemStackToInventory(itemstack1.copy()))
            {
                par3EntityPlayer.dropPlayerItemWithRandomChoice(itemstack1, false);
            }

            return par1ItemStack;
        }
    }
}