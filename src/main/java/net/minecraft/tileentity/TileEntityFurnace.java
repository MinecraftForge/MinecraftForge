package net.minecraft.tileentity;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityFurnace extends TileEntity implements ISidedInventory
{
    private static final int[] field_145962_k = new int[] {0};
    private static final int[] field_145959_l = new int[] {2, 1};
    private static final int[] field_145960_m = new int[] {1};
    private ItemStack[] field_145957_n = new ItemStack[3];
    public int field_145956_a;
    public int field_145963_i;
    public int field_145961_j;
    private String field_145958_o;
    private static final String __OBFID = "CL_00000357";

    // JAVADOC METHOD $$ func_70302_i_
    public int getSizeInventory()
    {
        return this.field_145957_n.length;
    }

    // JAVADOC METHOD $$ func_70301_a
    public ItemStack getStackInSlot(int par1)
    {
        return this.field_145957_n[par1];
    }

    // JAVADOC METHOD $$ func_70298_a
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.field_145957_n[par1] != null)
        {
            ItemStack itemstack;

            if (this.field_145957_n[par1].stackSize <= par2)
            {
                itemstack = this.field_145957_n[par1];
                this.field_145957_n[par1] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.field_145957_n[par1].splitStack(par2);

                if (this.field_145957_n[par1].stackSize == 0)
                {
                    this.field_145957_n[par1] = null;
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
        if (this.field_145957_n[par1] != null)
        {
            ItemStack itemstack = this.field_145957_n[par1];
            this.field_145957_n[par1] = null;
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
        this.field_145957_n[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    public String func_145825_b()
    {
        return this.func_145818_k_() ? this.field_145958_o : "container.furnace";
    }

    public boolean func_145818_k_()
    {
        return this.field_145958_o != null && this.field_145958_o.length() > 0;
    }

    public void func_145951_a(String p_145951_1_)
    {
        this.field_145958_o = p_145951_1_;
    }

    public void func_145839_a(NBTTagCompound p_145839_1_)
    {
        super.func_145839_a(p_145839_1_);
        NBTTagList nbttaglist = p_145839_1_.func_150295_c("Items", 10);
        this.field_145957_n = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.field_145957_n.length)
            {
                this.field_145957_n[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        this.field_145956_a = p_145839_1_.getShort("BurnTime");
        this.field_145961_j = p_145839_1_.getShort("CookTime");
        this.field_145963_i = func_145952_a(this.field_145957_n[1]);

        if (p_145839_1_.func_150297_b("CustomName", 8))
        {
            this.field_145958_o = p_145839_1_.getString("CustomName");
        }
    }

    public void func_145841_b(NBTTagCompound p_145841_1_)
    {
        super.func_145841_b(p_145841_1_);
        p_145841_1_.setShort("BurnTime", (short)this.field_145956_a);
        p_145841_1_.setShort("CookTime", (short)this.field_145961_j);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.field_145957_n.length; ++i)
        {
            if (this.field_145957_n[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.field_145957_n[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        p_145841_1_.setTag("Items", nbttaglist);

        if (this.func_145818_k_())
        {
            p_145841_1_.setString("CustomName", this.field_145958_o);
        }
    }

    // JAVADOC METHOD $$ func_70297_j_
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @SideOnly(Side.CLIENT)
    public int func_145953_d(int p_145953_1_)
    {
        return this.field_145961_j * p_145953_1_ / 200;
    }

    @SideOnly(Side.CLIENT)
    public int func_145955_e(int p_145955_1_)
    {
        if (this.field_145963_i == 0)
        {
            this.field_145963_i = 200;
        }

        return this.field_145956_a * p_145955_1_ / this.field_145963_i;
    }

    public boolean func_145950_i()
    {
        return this.field_145956_a > 0;
    }

    public void func_145845_h()
    {
        boolean flag = this.field_145956_a > 0;
        boolean flag1 = false;

        if (this.field_145956_a > 0)
        {
            --this.field_145956_a;
        }

        if (!this.field_145850_b.isRemote)
        {
            if (this.field_145956_a == 0 && this.func_145948_k())
            {
                this.field_145963_i = this.field_145956_a = func_145952_a(this.field_145957_n[1]);

                if (this.field_145956_a > 0)
                {
                    flag1 = true;

                    if (this.field_145957_n[1] != null)
                    {
                        --this.field_145957_n[1].stackSize;

                        if (this.field_145957_n[1].stackSize == 0)
                        {
                            this.field_145957_n[1] = field_145957_n[1].getItem().getContainerItem(field_145957_n[1]);
                        }
                    }
                }
            }

            if (this.func_145950_i() && this.func_145948_k())
            {
                ++this.field_145961_j;

                if (this.field_145961_j == 200)
                {
                    this.field_145961_j = 0;
                    this.func_145949_j();
                    flag1 = true;
                }
            }
            else
            {
                this.field_145961_j = 0;
            }

            if (flag != this.field_145956_a > 0)
            {
                flag1 = true;
                BlockFurnace.func_149931_a(this.field_145956_a > 0, this.field_145850_b, this.field_145851_c, this.field_145848_d, this.field_145849_e);
            }
        }

        if (flag1)
        {
            this.onInventoryChanged();
        }
    }

    private boolean func_145948_k()
    {
        if (this.field_145957_n[0] == null)
        {
            return false;
        }
        else
        {
            ItemStack itemstack = FurnaceRecipes.smelting().func_151395_a(this.field_145957_n[0]);
            if (itemstack == null) return false;
            if (this.field_145957_n[2] == null) return true;
            if (!this.field_145957_n[2].isItemEqual(itemstack)) return false;
            int result = field_145957_n[2].stackSize + itemstack.stackSize;
            return result < getInventoryStackLimit() && result <= this.field_145957_n[2].getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
        }
    }

    public void func_145949_j()
    {
        if (this.func_145948_k())
        {
            ItemStack itemstack = FurnaceRecipes.smelting().func_151395_a(this.field_145957_n[0]);

            if (this.field_145957_n[2] == null)
            {
                this.field_145957_n[2] = itemstack.copy();
            }
            else if (this.field_145957_n[2].getItem() == itemstack.getItem())
            {
                this.field_145957_n[2].stackSize += itemstack.stackSize; // Forge BugFix: Results may have multiple items
            }

            --this.field_145957_n[0].stackSize;

            if (this.field_145957_n[0].stackSize <= 0)
            {
                this.field_145957_n[0] = null;
            }
        }
    }

    public static int func_145952_a(ItemStack p_145952_0_)
    {
        if (p_145952_0_ == null)
        {
            return 0;
        }
        else
        {
            Item item = p_145952_0_.getItem();

            if (item instanceof ItemBlock && Block.func_149634_a(item) != Blocks.air)
            {
                Block block = Block.func_149634_a(item);

                if (block == Blocks.wooden_slab)
                {
                    return 150;
                }

                if (block.func_149688_o() == Material.field_151575_d)
                {
                    return 300;
                }

                if (block == Blocks.coal_block)
                {
                    return 16000;
                }
            }

            if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword)item).func_150932_j().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe)item).getMaterialName().equals("WOOD")) return 200;
            if (item == Items.stick) return 100;
            if (item == Items.coal) return 1600;
            if (item == Items.lava_bucket) return 20000;
            if (item == Item.func_150898_a(Blocks.sapling)) return 100;
            if (item == Items.blaze_rod) return 2400;
            return GameRegistry.getFuelValue(p_145952_0_);
        }
    }

    public static boolean func_145954_b(ItemStack p_145954_0_)
    {
        return func_145952_a(p_145954_0_) > 0;
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
        return par1 == 2 ? false : (par1 == 1 ? func_145954_b(par2ItemStack) : true);
    }

    // JAVADOC METHOD $$ func_94128_d
    public int[] getAccessibleSlotsFromSide(int par1)
    {
        return par1 == 0 ? field_145959_l : (par1 == 1 ? field_145962_k : field_145960_m);
    }

    // JAVADOC METHOD $$ func_102007_a
    public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3)
    {
        return this.isItemValidForSlot(par1, par2ItemStack);
    }

    // JAVADOC METHOD $$ func_102008_b
    public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3)
    {
        return par3 != 0 || par1 != 1 || par2ItemStack.getItem() == Items.bucket;
    }
}