package net.minecraft.entity.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ReportedException;

public class InventoryPlayer implements IInventory
{
    // JAVADOC FIELD $$ field_70462_a
    public ItemStack[] mainInventory = new ItemStack[36];
    // JAVADOC FIELD $$ field_70460_b
    public ItemStack[] armorInventory = new ItemStack[4];
    // JAVADOC FIELD $$ field_70461_c
    public int currentItem;
    // JAVADOC FIELD $$ field_70456_f
    @SideOnly(Side.CLIENT)
    private ItemStack currentItemStack;
    // JAVADOC FIELD $$ field_70458_d
    public EntityPlayer player;
    private ItemStack itemStack;
    // JAVADOC FIELD $$ field_70459_e
    public boolean inventoryChanged;
    private static final String __OBFID = "CL_00001709";

    public InventoryPlayer(EntityPlayer par1EntityPlayer)
    {
        this.player = par1EntityPlayer;
    }

    // JAVADOC METHOD $$ func_70448_g
    public ItemStack getCurrentItem()
    {
        return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
    }

    // JAVADOC METHOD $$ func_70451_h
    public static int getHotbarSize()
    {
        return 9;
    }

    private int func_146029_c(Item p_146029_1_)
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == p_146029_1_)
            {
                return i;
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)
    private int func_146024_c(Item p_146024_1_, int p_146024_2_)
    {
        for (int j = 0; j < this.mainInventory.length; ++j)
        {
            if (this.mainInventory[j] != null && this.mainInventory[j].getItem() == p_146024_1_ && this.mainInventory[j].getItemDamage() == p_146024_2_)
            {
                return j;
            }
        }

        return -1;
    }

    // JAVADOC METHOD $$ func_70432_d
    private int storeItemStack(ItemStack par1ItemStack)
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == par1ItemStack.getItem() && this.mainInventory[i].isStackable() && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize() && this.mainInventory[i].stackSize < this.getInventoryStackLimit() && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getItemDamage() == par1ItemStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(this.mainInventory[i], par1ItemStack))
            {
                return i;
            }
        }

        return -1;
    }

    // JAVADOC METHOD $$ func_70447_i
    public int getFirstEmptyStack()
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] == null)
            {
                return i;
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)
    public void func_146030_a(Item p_146030_1_, int p_146030_2_, boolean p_146030_3_, boolean p_146030_4_)
    {
        boolean flag2 = true;
        this.currentItemStack = this.getCurrentItem();
        int k;

        if (p_146030_3_)
        {
            k = this.func_146024_c(p_146030_1_, p_146030_2_);
        }
        else
        {
            k = this.func_146029_c(p_146030_1_);
        }

        if (k >= 0 && k < 9)
        {
            this.currentItem = k;
        }
        else
        {
            if (p_146030_4_ && p_146030_1_ != null)
            {
                int j = this.getFirstEmptyStack();

                if (j >= 0 && j < 9)
                {
                    this.currentItem = j;
                }

                this.func_70439_a(p_146030_1_, p_146030_2_);
            }
        }
    }

    public int func_146027_a(Item p_146027_1_, int p_146027_2_)
    {
        int j = 0;
        int k;
        ItemStack itemstack;

        for (k = 0; k < this.mainInventory.length; ++k)
        {
            itemstack = this.mainInventory[k];

            if (itemstack != null && (p_146027_1_ == null || itemstack.getItem() == p_146027_1_) && (p_146027_2_ <= -1 || itemstack.getItemDamage() == p_146027_2_))
            {
                j += itemstack.stackSize;
                this.mainInventory[k] = null;
            }
        }

        for (k = 0; k < this.armorInventory.length; ++k)
        {
            itemstack = this.armorInventory[k];

            if (itemstack != null && (p_146027_1_ == null || itemstack.getItem() == p_146027_1_) && (p_146027_2_ <= -1 || itemstack.getItemDamage() == p_146027_2_))
            {
                j += itemstack.stackSize;
                this.armorInventory[k] = null;
            }
        }

        if (this.itemStack != null)
        {
            if (p_146027_1_ != null && this.itemStack.getItem() != p_146027_1_)
            {
                return j;
            }

            if (p_146027_2_ > -1 && this.itemStack.getItemDamage() != p_146027_2_)
            {
                return j;
            }

            j += this.itemStack.stackSize;
            this.setItemStack((ItemStack)null);
        }

        return j;
    }

    // JAVADOC METHOD $$ func_70453_c
    @SideOnly(Side.CLIENT)
    public void changeCurrentItem(int par1)
    {
        if (par1 > 0)
        {
            par1 = 1;
        }

        if (par1 < 0)
        {
            par1 = -1;
        }

        for (this.currentItem -= par1; this.currentItem < 0; this.currentItem += 9)
        {
            ;
        }

        while (this.currentItem >= 9)
        {
            this.currentItem -= 9;
        }
    }

    @SideOnly(Side.CLIENT)
    public void func_70439_a(Item par1Item, int par2)
    {
        if (par1Item != null)
        {
            if (this.currentItemStack != null && this.currentItemStack.isItemEnchantable() && this.func_146024_c(this.currentItemStack.getItem(), this.currentItemStack.getItemDamageForDisplay()) == this.currentItem)
            {
                return;
            }

            int j = this.func_146024_c(par1Item, par2);

            if (j >= 0)
            {
                int k = this.mainInventory[j].stackSize;
                this.mainInventory[j] = this.mainInventory[this.currentItem];
                this.mainInventory[this.currentItem] = new ItemStack(par1Item, k, par2);
            }
            else
            {
                this.mainInventory[this.currentItem] = new ItemStack(par1Item, 1, par2);
            }
        }
    }

    // JAVADOC METHOD $$ func_70452_e
    private int storePartialItemStack(ItemStack par1ItemStack)
    {
        Item item = par1ItemStack.getItem();
        int i = par1ItemStack.stackSize;
        int j;

        if (par1ItemStack.getMaxStackSize() == 1)
        {
            j = this.getFirstEmptyStack();

            if (j < 0)
            {
                return i;
            }
            else
            {
                if (this.mainInventory[j] == null)
                {
                    this.mainInventory[j] = ItemStack.copyItemStack(par1ItemStack);
                }

                return 0;
            }
        }
        else
        {
            j = this.storeItemStack(par1ItemStack);

            if (j < 0)
            {
                j = this.getFirstEmptyStack();
            }

            if (j < 0)
            {
                return i;
            }
            else
            {
                if (this.mainInventory[j] == null)
                {
                    this.mainInventory[j] = new ItemStack(item, 0, par1ItemStack.getItemDamage());

                    if (par1ItemStack.hasTagCompound())
                    {
                        this.mainInventory[j].setTagCompound((NBTTagCompound)par1ItemStack.getTagCompound().copy());
                    }
                }

                int k = i;

                if (i > this.mainInventory[j].getMaxStackSize() - this.mainInventory[j].stackSize)
                {
                    k = this.mainInventory[j].getMaxStackSize() - this.mainInventory[j].stackSize;
                }

                if (k > this.getInventoryStackLimit() - this.mainInventory[j].stackSize)
                {
                    k = this.getInventoryStackLimit() - this.mainInventory[j].stackSize;
                }

                if (k == 0)
                {
                    return i;
                }
                else
                {
                    i -= k;
                    this.mainInventory[j].stackSize += k;
                    this.mainInventory[j].animationsToGo = 5;
                    return i;
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_70429_k
    public void decrementAnimations()
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                this.mainInventory[i].updateAnimation(this.player.worldObj, this.player, i, this.currentItem == i);
            }
        }

        for (int i = 0; i < armorInventory.length; i++)
        {
            if (armorInventory[i] != null)
            {
                armorInventory[i].getItem().onArmorTick(player.worldObj, player, armorInventory[i]);
            }
        }
    }

    public boolean func_146026_a(Item p_146026_1_)
    {
        int i = this.func_146029_c(p_146026_1_);

        if (i < 0)
        {
            return false;
        }
        else
        {
            if (--this.mainInventory[i].stackSize <= 0)
            {
                this.mainInventory[i] = null;
            }

            return true;
        }
    }

    public boolean func_146028_b(Item p_146028_1_)
    {
        int i = this.func_146029_c(p_146028_1_);
        return i >= 0;
    }

    // JAVADOC METHOD $$ func_70441_a
    public boolean addItemStackToInventory(final ItemStack par1ItemStack)
    {
        if (par1ItemStack != null && par1ItemStack.stackSize != 0 && par1ItemStack.getItem() != null)
        {
            try
            {
                int i;

                if (par1ItemStack.isItemDamaged())
                {
                    i = this.getFirstEmptyStack();

                    if (i >= 0)
                    {
                        this.mainInventory[i] = ItemStack.copyItemStack(par1ItemStack);
                        this.mainInventory[i].animationsToGo = 5;
                        par1ItemStack.stackSize = 0;
                        return true;
                    }
                    else if (this.player.capabilities.isCreativeMode)
                    {
                        par1ItemStack.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    do
                    {
                        i = par1ItemStack.stackSize;
                        par1ItemStack.stackSize = this.storePartialItemStack(par1ItemStack);
                    }
                    while (par1ItemStack.stackSize > 0 && par1ItemStack.stackSize < i);

                    if (par1ItemStack.stackSize == i && this.player.capabilities.isCreativeMode)
                    {
                        par1ItemStack.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return par1ItemStack.stackSize < i;
                    }
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Integer.valueOf(Item.func_150891_b(par1ItemStack.getItem())));
                crashreportcategory.addCrashSection("Item data", Integer.valueOf(par1ItemStack.getItemDamage()));
                crashreportcategory.addCrashSectionCallable("Item name", new Callable()
                {
                    private static final String __OBFID = "CL_00001710";
                    public String call()
                    {
                        return par1ItemStack.getDisplayName();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        else
        {
            return false;
        }
    }

    // JAVADOC METHOD $$ func_70298_a
    public ItemStack decrStackSize(int par1, int par2)
    {
        ItemStack[] aitemstack = this.mainInventory;

        if (par1 >= this.mainInventory.length)
        {
            aitemstack = this.armorInventory;
            par1 -= this.mainInventory.length;
        }

        if (aitemstack[par1] != null)
        {
            ItemStack itemstack;

            if (aitemstack[par1].stackSize <= par2)
            {
                itemstack = aitemstack[par1];
                aitemstack[par1] = null;
                return itemstack;
            }
            else
            {
                itemstack = aitemstack[par1].splitStack(par2);

                if (aitemstack[par1].stackSize == 0)
                {
                    aitemstack[par1] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    // JAVADOC METHOD $$ func_70304_b
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        ItemStack[] aitemstack = this.mainInventory;

        if (par1 >= this.mainInventory.length)
        {
            aitemstack = this.armorInventory;
            par1 -= this.mainInventory.length;
        }

        if (aitemstack[par1] != null)
        {
            ItemStack itemstack = aitemstack[par1];
            aitemstack[par1] = null;
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
        ItemStack[] aitemstack = this.mainInventory;

        if (par1 >= aitemstack.length)
        {
            par1 -= aitemstack.length;
            aitemstack = this.armorInventory;
        }

        aitemstack[par1] = par2ItemStack;
    }

    public float func_146023_a(Block p_146023_1_)
    {
        float f = 1.0F;

        if (this.mainInventory[this.currentItem] != null)
        {
            f *= this.mainInventory[this.currentItem].func_150997_a(p_146023_1_);
        }

        return f;
    }

    // JAVADOC METHOD $$ func_70442_a
    public NBTTagList writeToNBT(NBTTagList par1NBTTagList)
    {
        int i;
        NBTTagCompound nbttagcompound;

        for (i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                this.mainInventory[i].writeToNBT(nbttagcompound);
                par1NBTTagList.appendTag(nbttagcompound);
            }
        }

        for (i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null)
            {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)(i + 100));
                this.armorInventory[i].writeToNBT(nbttagcompound);
                par1NBTTagList.appendTag(nbttagcompound);
            }
        }

        return par1NBTTagList;
    }

    // JAVADOC METHOD $$ func_70443_b
    public void readFromNBT(NBTTagList par1NBTTagList)
    {
        this.mainInventory = new ItemStack[36];
        this.armorInventory = new ItemStack[4];

        for (int i = 0; i < par1NBTTagList.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = par1NBTTagList.func_150305_b(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null)
            {
                if (j >= 0 && j < this.mainInventory.length)
                {
                    this.mainInventory[j] = itemstack;
                }

                if (j >= 100 && j < this.armorInventory.length + 100)
                {
                    this.armorInventory[j - 100] = itemstack;
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_70302_i_
    public int getSizeInventory()
    {
        return this.mainInventory.length + 4;
    }

    // JAVADOC METHOD $$ func_70301_a
    public ItemStack getStackInSlot(int par1)
    {
        ItemStack[] aitemstack = this.mainInventory;

        if (par1 >= aitemstack.length)
        {
            par1 -= aitemstack.length;
            aitemstack = this.armorInventory;
        }

        return aitemstack[par1];
    }

    public String func_145825_b()
    {
        return "container.inventory";
    }

    public boolean func_145818_k_()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_70297_j_
    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean func_146025_b(Block p_146025_1_)
    {
        if (p_146025_1_.func_149688_o().isToolNotRequired())
        {
            return true;
        }
        else
        {
            ItemStack itemstack = this.getStackInSlot(this.currentItem);
            return itemstack != null ? itemstack.func_150998_b(p_146025_1_) : false;
        }
    }

    // JAVADOC METHOD $$ func_70440_f
    public ItemStack armorItemInSlot(int par1)
    {
        return this.armorInventory[par1];
    }

    // JAVADOC METHOD $$ func_70430_l
    public int getTotalArmorValue()
    {
        int i = 0;

        for (int j = 0; j < this.armorInventory.length; ++j)
        {
            if (this.armorInventory[j] != null && this.armorInventory[j].getItem() instanceof ItemArmor)
            {
                int k = ((ItemArmor)this.armorInventory[j].getItem()).damageReduceAmount;
                i += k;
            }
        }

        return i;
    }

    // JAVADOC METHOD $$ func_70449_g
    public void damageArmor(float par1)
    {
        par1 /= 4.0F;

        if (par1 < 1.0F)
        {
            par1 = 1.0F;
        }

        for (int i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor)
            {
                this.armorInventory[i].damageItem((int)par1, this.player);

                if (this.armorInventory[i].stackSize == 0)
                {
                    this.armorInventory[i] = null;
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_70436_m
    public void dropAllItems()
    {
        int i;

        for (i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                this.player.func_146097_a(this.mainInventory[i], true, false);
                this.mainInventory[i] = null;
            }
        }

        for (i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null)
            {
                this.player.func_146097_a(this.armorInventory[i], true, false);
                this.armorInventory[i] = null;
            }
        }
    }

    // JAVADOC METHOD $$ func_70296_d
    public void onInventoryChanged()
    {
        this.inventoryChanged = true;
    }

    public void setItemStack(ItemStack par1ItemStack)
    {
        this.itemStack = par1ItemStack;
    }

    public ItemStack getItemStack()
    {
        return this.itemStack;
    }

    // JAVADOC METHOD $$ func_70300_a
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.player.isDead ? false : par1EntityPlayer.getDistanceSqToEntity(this.player) <= 64.0D;
    }

    // JAVADOC METHOD $$ func_70431_c
    public boolean hasItemStack(ItemStack par1ItemStack)
    {
        int i;

        for (i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null && this.armorInventory[i].isItemEqual(par1ItemStack))
            {
                return true;
            }
        }

        for (i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].isItemEqual(par1ItemStack))
            {
                return true;
            }
        }

        return false;
    }

    public void openChest() {}

    public void closeChest() {}

    // JAVADOC METHOD $$ func_94041_b
    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return true;
    }

    // JAVADOC METHOD $$ func_70455_b
    public void copyInventory(InventoryPlayer par1InventoryPlayer)
    {
        int i;

        for (i = 0; i < this.mainInventory.length; ++i)
        {
            this.mainInventory[i] = ItemStack.copyItemStack(par1InventoryPlayer.mainInventory[i]);
        }

        for (i = 0; i < this.armorInventory.length; ++i)
        {
            this.armorInventory[i] = ItemStack.copyItemStack(par1InventoryPlayer.armorInventory[i]);
        }

        this.currentItem = par1InventoryPlayer.currentItem;
    }
}