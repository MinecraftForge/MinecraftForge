package net.minecraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

@SideOnly(Side.CLIENT)
public class NpcMerchant implements IMerchant
{
    // JAVADOC FIELD $$ field_70937_a
    private InventoryMerchant theMerchantInventory;
    // JAVADOC FIELD $$ field_70935_b
    private EntityPlayer customer;
    // JAVADOC FIELD $$ field_70936_c
    private MerchantRecipeList recipeList;
    private static final String __OBFID = "CL_00001705";

    public NpcMerchant(EntityPlayer par1EntityPlayer)
    {
        this.customer = par1EntityPlayer;
        this.theMerchantInventory = new InventoryMerchant(par1EntityPlayer, this);
    }

    public EntityPlayer getCustomer()
    {
        return this.customer;
    }

    public void setCustomer(EntityPlayer par1EntityPlayer) {}

    public MerchantRecipeList getRecipes(EntityPlayer par1EntityPlayer)
    {
        return this.recipeList;
    }

    public void setRecipes(MerchantRecipeList par1MerchantRecipeList)
    {
        this.recipeList = par1MerchantRecipeList;
    }

    public void useRecipe(MerchantRecipe par1MerchantRecipe) {}

    public void func_110297_a_(ItemStack par1ItemStack) {}
}