package net.minecraft.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.brewing.PotionBrewedEvent;

public class TileEntityBrewingStand extends TileEntity implements ISidedInventory
{
    private static final int[] field_145941_a = new int[] {3};
    private static final int[] field_145947_i = new int[] {0, 1, 2};
    private ItemStack[] field_145945_j = new ItemStack[4];
    private int field_145946_k;
    private int field_145943_l;
    private Item field_145944_m;
    private String field_145942_n;
    private static final String __OBFID = "CL_00000345";

    public String func_145825_b()
    {
        return this.func_145818_k_() ? this.field_145942_n : "container.brewing";
    }

    public boolean func_145818_k_()
    {
        return this.field_145942_n != null && this.field_145942_n.length() > 0;
    }

    public void func_145937_a(String p_145937_1_)
    {
        this.field_145942_n = p_145937_1_;
    }

    // JAVADOC METHOD $$ func_70302_i_
    public int getSizeInventory()
    {
        return this.field_145945_j.length;
    }

    public void func_145845_h()
    {
        if (this.field_145946_k > 0)
        {
            --this.field_145946_k;

            if (this.field_145946_k == 0)
            {
                this.func_145940_l();
                this.onInventoryChanged();
            }
            else if (!this.func_145934_k())
            {
                this.field_145946_k = 0;
                this.onInventoryChanged();
            }
            else if (this.field_145944_m != this.field_145945_j[3].getItem())
            {
                this.field_145946_k = 0;
                this.onInventoryChanged();
            }
        }
        else if (this.func_145934_k())
        {
            this.field_145946_k = 400;
            this.field_145944_m = this.field_145945_j[3].getItem();
        }

        int i = this.func_145939_j();

        if (i != this.field_145943_l)
        {
            this.field_145943_l = i;
            this.field_145850_b.setBlockMetadataWithNotify(this.field_145851_c, this.field_145848_d, this.field_145849_e, i, 2);
        }

        super.func_145845_h();
    }

    public int func_145935_i()
    {
        return this.field_145946_k;
    }

    private boolean func_145934_k()
    {
        if (this.field_145945_j[3] != null && this.field_145945_j[3].stackSize > 0)
        {
            ItemStack itemstack = this.field_145945_j[3];

            if (!itemstack.getItem().func_150892_m(itemstack))
            {
                return false;
            }
            else
            {
                boolean flag = false;

                for (int i = 0; i < 3; ++i)
                {
                    if (this.field_145945_j[i] != null && this.field_145945_j[i].getItem() instanceof ItemPotion)
                    {
                        int j = this.field_145945_j[i].getItemDamage();
                        int k = this.func_145936_c(j, itemstack);

                        if (!ItemPotion.isSplash(j) && ItemPotion.isSplash(k))
                        {
                            flag = true;
                            break;
                        }

                        List list = Items.potionitem.getEffects(j);
                        List list1 = Items.potionitem.getEffects(k);

                        if ((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null) && j != k)
                        {
                            flag = true;
                            break;
                        }
                    }
                }

                return flag;
            }
        }
        else
        {
            return false;
        }
    }

    private void func_145940_l()
    {
        if (this.func_145934_k())
        {
            ItemStack itemstack = this.field_145945_j[3];

            for (int i = 0; i < 3; ++i)
            {
                if (this.field_145945_j[i] != null && this.field_145945_j[i].getItem() instanceof ItemPotion)
                {
                    int j = this.field_145945_j[i].getItemDamage();
                    int k = this.func_145936_c(j, itemstack);
                    List list = Items.potionitem.getEffects(j);
                    List list1 = Items.potionitem.getEffects(k);

                    if ((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null))
                    {
                        if (j != k)
                        {
                            this.field_145945_j[i].setItemDamage(k);
                        }
                    }
                    else if (!ItemPotion.isSplash(j) && ItemPotion.isSplash(k))
                    {
                        this.field_145945_j[i].setItemDamage(k);
                    }
                }
            }

            if (itemstack.getItem().hasContainerItem())
            {
                this.field_145945_j[3] = itemstack.getItem().getContainerItem(itemstack);
            }
            else
            {
                --this.field_145945_j[3].stackSize;

                if (this.field_145945_j[3].stackSize <= 0)
                {
                    this.field_145945_j[3] = null;
                }
            }
            MinecraftForge.EVENT_BUS.post(new PotionBrewedEvent(field_145945_j));
        }
    }

    private int func_145936_c(int p_145936_1_, ItemStack p_145936_2_)
    {
        return p_145936_2_ == null ? p_145936_1_ : (p_145936_2_.getItem().func_150892_m(p_145936_2_) ? PotionHelper.applyIngredient(p_145936_1_, p_145936_2_.getItem().func_150896_i(p_145936_2_)) : p_145936_1_);
    }

    public void func_145839_a(NBTTagCompound p_145839_1_)
    {
        super.func_145839_a(p_145839_1_);
        NBTTagList nbttaglist = p_145839_1_.func_150295_c("Items", 10);
        this.field_145945_j = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.field_145945_j.length)
            {
                this.field_145945_j[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        this.field_145946_k = p_145839_1_.getShort("BrewTime");

        if (p_145839_1_.func_150297_b("CustomName", 8))
        {
            this.field_145942_n = p_145839_1_.getString("CustomName");
        }
    }

    public void func_145841_b(NBTTagCompound p_145841_1_)
    {
        super.func_145841_b(p_145841_1_);
        p_145841_1_.setShort("BrewTime", (short)this.field_145946_k);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.field_145945_j.length; ++i)
        {
            if (this.field_145945_j[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.field_145945_j[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        p_145841_1_.setTag("Items", nbttaglist);

        if (this.func_145818_k_())
        {
            p_145841_1_.setString("CustomName", this.field_145942_n);
        }
    }

    // JAVADOC METHOD $$ func_70301_a
    public ItemStack getStackInSlot(int par1)
    {
        return par1 >= 0 && par1 < this.field_145945_j.length ? this.field_145945_j[par1] : null;
    }

    // JAVADOC METHOD $$ func_70298_a
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (par1 >= 0 && par1 < this.field_145945_j.length)
        {
            ItemStack itemstack = this.field_145945_j[par1];
            this.field_145945_j[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    // JAVADOC METHOD $$ func_70304_b
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (par1 >= 0 && par1 < this.field_145945_j.length)
        {
            ItemStack itemstack = this.field_145945_j[par1];
            this.field_145945_j[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    // JAVADOC METHOD $$ func_70299_a
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        if (par1 >= 0 && par1 < this.field_145945_j.length)
        {
            this.field_145945_j[par1] = par2ItemStack;
        }
    }

    // JAVADOC METHOD $$ func_70297_j_
    public int getInventoryStackLimit()
    {
        return 64;
    }

    // JAVADOC METHOD $$ func_70300_a
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.field_145850_b.func_147438_o(this.field_145851_c, this.field_145848_d, this.field_145849_e) != this ? false : par1EntityPlayer.getDistanceSq((double)this.field_145851_c + 0.5D, (double)this.field_145848_d + 0.5D, (double)this.field_145849_e + 0.5D) <= 64.0D;
    }

    public void openChest() {}

    public void closeChest() {}

    // JAVADOC METHOD $$ func_94041_b
    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return par1 == 3 ? par2ItemStack.getItem().func_150892_m(par2ItemStack) : par2ItemStack.getItem() instanceof ItemPotion || par2ItemStack.getItem() == Items.glass_bottle;
    }

    @SideOnly(Side.CLIENT)
    public void func_145938_d(int p_145938_1_)
    {
        this.field_145946_k = p_145938_1_;
    }

    public int func_145939_j()
    {
        int i = 0;

        for (int j = 0; j < 3; ++j)
        {
            if (this.field_145945_j[j] != null)
            {
                i |= 1 << j;
            }
        }

        return i;
    }

    // JAVADOC METHOD $$ func_94128_d
    public int[] getAccessibleSlotsFromSide(int par1)
    {
        return par1 == 1 ? field_145941_a : field_145947_i;
    }

    // JAVADOC METHOD $$ func_102007_a
    public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3)
    {
        return this.isItemValidForSlot(par1, par2ItemStack);
    }

    // JAVADOC METHOD $$ func_102008_b
    public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3)
    {
        return true;
    }
}