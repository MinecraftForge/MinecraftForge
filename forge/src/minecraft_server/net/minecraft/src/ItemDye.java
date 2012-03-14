package net.minecraft.src;

import net.minecraft.src.forge.ForgeHooks;

public class ItemDye extends Item
{
    /** List of dye color names */
    public static final String[] dyeColorNames = new String[] {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};
    public static final int[] dyeColors = new int[] {1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 2651799, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};

    public ItemDye(int par1)
    {
        super(par1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    public String getItemNameIS(ItemStack par1ItemStack)
    {
        int var2 = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, 15);
        return super.getItemName() + "." + dyeColorNames[var2];
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
    {
        if (par2EntityPlayer != null && !par2EntityPlayer.canPlayerEdit(par4, par5, par6))
        {
            return false;
        }
        else
        {
            if (par1ItemStack.getItemDamage() == 15)
            {
                int var8 = par3World.getBlockId(par4, par5, par6);
                if (ForgeHooks.onUseBonemeal(par3World, var8, par4, par5, par6))
                {
                    if (!par3World.isRemote)
                    {
                        par1ItemStack.stackSize--;
                    }
                    return true;
                }
                if (var8 == Block.sapling.blockID)
                {
                    if (!par3World.isRemote)
                    {
                        ((BlockSapling)Block.sapling).growTree(par3World, par4, par5, par6, par3World.rand);
                        --par1ItemStack.stackSize;
                    }

                    return true;
                }

                if (var8 == Block.mushroomBrown.blockID || var8 == Block.mushroomRed.blockID)
                {
                    if (!par3World.isRemote && ((BlockMushroom)Block.blocksList[var8]).fertilizeMushroom(par3World, par4, par5, par6, par3World.rand))
                    {
                        --par1ItemStack.stackSize;
                    }

                    return true;
                }

                if (var8 == Block.melonStem.blockID || var8 == Block.pumpkinStem.blockID)
                {
                    if (!par3World.isRemote)
                    {
                        ((BlockStem)Block.blocksList[var8]).fertilizeStem(par3World, par4, par5, par6);
                        --par1ItemStack.stackSize;
                    }

                    return true;
                }

                if (var8 == Block.crops.blockID)
                {
                    if (!par3World.isRemote)
                    {
                        ((BlockCrops)Block.crops).fertilize(par3World, par4, par5, par6);
                        --par1ItemStack.stackSize;
                    }

                    return true;
                }

                if (var8 == Block.grass.blockID)
                {
                    if (!par3World.isRemote)
                    {
                        --par1ItemStack.stackSize;
                        label73:

                        for (int var9 = 0; var9 < 128; ++var9)
                        {
                            int var10 = par4;
                            int var11 = par5 + 1;
                            int var12 = par6;

                            for (int var13 = 0; var13 < var9 / 16; ++var13)
                            {
                                var10 += itemRand.nextInt(3) - 1;
                                var11 += (itemRand.nextInt(3) - 1) * itemRand.nextInt(3) / 2;
                                var12 += itemRand.nextInt(3) - 1;

                                if (par3World.getBlockId(var10, var11 - 1, var12) != Block.grass.blockID || par3World.isBlockNormalCube(var10, var11, var12))
                                {
                                    continue label73;
                                }
                            }

                            if (par3World.getBlockId(var10, var11, var12) == 0)
                            {
                                if (itemRand.nextInt(10) != 0)
                                {
                                    par3World.setBlockAndMetadataWithNotify(var10, var11, var12, Block.tallGrass.blockID, 1);
                                }
                                else
                                {
                                    ForgeHooks.plantGrassPlant(par3World, var10, var11, var12);
                                }
                            }
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Called when a player right clicks a entity with a item.
     */
    public void useItemOnEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving)
    {
        if (par2EntityLiving instanceof EntitySheep)
        {
            EntitySheep var3 = (EntitySheep)par2EntityLiving;
            int var4 = BlockCloth.getBlockFromDye(par1ItemStack.getItemDamage());

            if (!var3.getSheared() && var3.getFleeceColor() != var4)
            {
                var3.setFleeceColor(var4);
                --par1ItemStack.stackSize;
            }
        }
    }
}
