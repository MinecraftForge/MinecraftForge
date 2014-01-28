package net.minecraft.village;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;

public class MerchantRecipeList extends ArrayList
{
    private static final String __OBFID = "CL_00000127";

    public MerchantRecipeList() {}

    public MerchantRecipeList(NBTTagCompound par1NBTTagCompound)
    {
        this.readRecipiesFromTags(par1NBTTagCompound);
    }

    // JAVADOC METHOD $$ func_77203_a
    public MerchantRecipe canRecipeBeUsed(ItemStack par1ItemStack, ItemStack par2ItemStack, int par3)
    {
        if (par3 > 0 && par3 < this.size())
        {
            MerchantRecipe merchantrecipe1 = (MerchantRecipe)this.get(par3);
            return par1ItemStack.getItem() == merchantrecipe1.getItemToBuy().getItem() && (par2ItemStack == null && !merchantrecipe1.hasSecondItemToBuy() || merchantrecipe1.hasSecondItemToBuy() && par2ItemStack != null && merchantrecipe1.getSecondItemToBuy().getItem() == par2ItemStack.getItem()) && par1ItemStack.stackSize >= merchantrecipe1.getItemToBuy().stackSize && (!merchantrecipe1.hasSecondItemToBuy() || par2ItemStack.stackSize >= merchantrecipe1.getSecondItemToBuy().stackSize) ? merchantrecipe1 : null;
        }
        else
        {
            for (int j = 0; j < this.size(); ++j)
            {
                MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(j);

                if (par1ItemStack.getItem() == merchantrecipe.getItemToBuy().getItem() && par1ItemStack.stackSize >= merchantrecipe.getItemToBuy().stackSize && (!merchantrecipe.hasSecondItemToBuy() && par2ItemStack == null || merchantrecipe.hasSecondItemToBuy() && par2ItemStack != null && merchantrecipe.getSecondItemToBuy().getItem() == par2ItemStack.getItem() && par2ItemStack.stackSize >= merchantrecipe.getSecondItemToBuy().stackSize))
                {
                    return merchantrecipe;
                }
            }

            return null;
        }
    }

    // JAVADOC METHOD $$ func_77205_a
    public void addToListWithCheck(MerchantRecipe par1MerchantRecipe)
    {
        for (int i = 0; i < this.size(); ++i)
        {
            MerchantRecipe merchantrecipe1 = (MerchantRecipe)this.get(i);

            if (par1MerchantRecipe.hasSameIDsAs(merchantrecipe1))
            {
                if (par1MerchantRecipe.hasSameItemsAs(merchantrecipe1))
                {
                    this.set(i, par1MerchantRecipe);
                }

                return;
            }
        }

        this.add(par1MerchantRecipe);
    }

    public void func_151391_a(PacketBuffer p_151391_1_) throws IOException
    {
        p_151391_1_.writeByte((byte)(this.size() & 255));

        for (int i = 0; i < this.size(); ++i)
        {
            MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(i);
            p_151391_1_.func_150788_a(merchantrecipe.getItemToBuy());
            p_151391_1_.func_150788_a(merchantrecipe.getItemToSell());
            ItemStack itemstack = merchantrecipe.getSecondItemToBuy();
            p_151391_1_.writeBoolean(itemstack != null);

            if (itemstack != null)
            {
                p_151391_1_.func_150788_a(itemstack);
            }

            p_151391_1_.writeBoolean(merchantrecipe.func_82784_g());
        }
    }

    public void readRecipiesFromTags(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagList nbttaglist = par1NBTTagCompound.func_150295_c("Recipes", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            this.add(new MerchantRecipe(nbttagcompound1));
        }
    }

    public NBTTagCompound getRecipiesAsTags()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.size(); ++i)
        {
            MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(i);
            nbttaglist.appendTag(merchantrecipe.writeToTags());
        }

        nbttagcompound.setTag("Recipes", nbttaglist);
        return nbttagcompound;
    }

    @SideOnly(Side.CLIENT)
    public static MerchantRecipeList func_151390_b(PacketBuffer p_151390_0_) throws IOException
    {
        MerchantRecipeList merchantrecipelist = new MerchantRecipeList();
        int i = p_151390_0_.readByte() & 255;

        for (int j = 0; j < i; ++j)
        {
            ItemStack itemstack = p_151390_0_.func_150791_c();
            ItemStack itemstack1 = p_151390_0_.func_150791_c();
            ItemStack itemstack2 = null;

            if (p_151390_0_.readBoolean())
            {
                itemstack2 = p_151390_0_.func_150791_c();
            }

            boolean flag = p_151390_0_.readBoolean();
            MerchantRecipe merchantrecipe = new MerchantRecipe(itemstack, itemstack2, itemstack1);

            if (flag)
            {
                merchantrecipe.func_82785_h();
            }

            merchantrecipelist.add(merchantrecipe);
        }

        return merchantrecipelist;
    }
}