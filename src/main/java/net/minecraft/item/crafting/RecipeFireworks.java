package net.minecraft.item.crafting;

import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class RecipeFireworks implements IRecipe
{
    private ItemStack field_92102_a;
    private static final String __OBFID = "CL_00000083";

    // JAVADOC METHOD $$ func_77569_a
    public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World)
    {
        this.field_92102_a = null;
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;

        for (int k1 = 0; k1 < par1InventoryCrafting.getSizeInventory(); ++k1)
        {
            ItemStack itemstack = par1InventoryCrafting.getStackInSlot(k1);

            if (itemstack != null)
            {
                if (itemstack.getItem() == Items.gunpowder)
                {
                    ++j;
                }
                else if (itemstack.getItem() == Items.firework_charge)
                {
                    ++l;
                }
                else if (itemstack.getItem() == Items.dye)
                {
                    ++k;
                }
                else if (itemstack.getItem() == Items.paper)
                {
                    ++i;
                }
                else if (itemstack.getItem() == Items.glowstone_dust)
                {
                    ++i1;
                }
                else if (itemstack.getItem() == Items.diamond)
                {
                    ++i1;
                }
                else if (itemstack.getItem() == Items.fire_charge)
                {
                    ++j1;
                }
                else if (itemstack.getItem() == Items.feather)
                {
                    ++j1;
                }
                else if (itemstack.getItem() == Items.gold_nugget)
                {
                    ++j1;
                }
                else
                {
                    if (itemstack.getItem() != Items.skull)
                    {
                        return false;
                    }

                    ++j1;
                }
            }
        }

        i1 += k + j1;

        if (j <= 3 && i <= 1)
        {
            NBTTagCompound nbttagcompound;
            NBTTagCompound nbttagcompound1;

            if (j >= 1 && i == 1 && i1 == 0)
            {
                this.field_92102_a = new ItemStack(Items.fireworks);

                nbttagcompound = new NBTTagCompound();
                if (l > 0)
                {
                    nbttagcompound1 = new NBTTagCompound();
                    NBTTagList nbttaglist = new NBTTagList();

                    for (int k2 = 0; k2 < par1InventoryCrafting.getSizeInventory(); ++k2)
                    {
                        ItemStack itemstack3 = par1InventoryCrafting.getStackInSlot(k2);

                        if (itemstack3 != null && itemstack3.getItem() == Items.firework_charge && itemstack3.hasTagCompound() && itemstack3.getTagCompound().func_150297_b("Explosion", 10))
                        {
                            nbttaglist.appendTag(itemstack3.getTagCompound().getCompoundTag("Explosion"));
                        }
                    }

                    nbttagcompound1.setTag("Explosions", nbttaglist);
                    nbttagcompound1.setByte("Flight", (byte)j);
                    nbttagcompound.setTag("Fireworks", nbttagcompound1);
                }
                this.field_92102_a.setTagCompound(nbttagcompound); //Forge BugFix: NPE Protection

                return true;
            }
            else if (j == 1 && i == 0 && l == 0 && k > 0 && j1 <= 1)
            {
                this.field_92102_a = new ItemStack(Items.firework_charge);
                nbttagcompound = new NBTTagCompound();
                nbttagcompound1 = new NBTTagCompound();
                byte b0 = 0;
                ArrayList arraylist = new ArrayList();

                for (int l1 = 0; l1 < par1InventoryCrafting.getSizeInventory(); ++l1)
                {
                    ItemStack itemstack2 = par1InventoryCrafting.getStackInSlot(l1);

                    if (itemstack2 != null)
                    {
                        if (itemstack2.getItem() == Items.dye)
                        {
                            arraylist.add(Integer.valueOf(ItemDye.field_150922_c[itemstack2.getItemDamage()]));
                        }
                        else if (itemstack2.getItem() == Items.glowstone_dust)
                        {
                            nbttagcompound1.setBoolean("Flicker", true);
                        }
                        else if (itemstack2.getItem() == Items.diamond)
                        {
                            nbttagcompound1.setBoolean("Trail", true);
                        }
                        else if (itemstack2.getItem() == Items.fire_charge)
                        {
                            b0 = 1;
                        }
                        else if (itemstack2.getItem() == Items.feather)
                        {
                            b0 = 4;
                        }
                        else if (itemstack2.getItem() == Items.gold_nugget)
                        {
                            b0 = 2;
                        }
                        else if (itemstack2.getItem() == Items.skull)
                        {
                            b0 = 3;
                        }
                    }
                }

                int[] aint1 = new int[arraylist.size()];

                for (int l2 = 0; l2 < aint1.length; ++l2)
                {
                    aint1[l2] = ((Integer)arraylist.get(l2)).intValue();
                }

                nbttagcompound1.setIntArray("Colors", aint1);
                nbttagcompound1.setByte("Type", b0);
                nbttagcompound.setTag("Explosion", nbttagcompound1);
                this.field_92102_a.setTagCompound(nbttagcompound);
                return true;
            }
            else if (j == 0 && i == 0 && l == 1 && k > 0 && k == i1)
            {
                ArrayList arraylist1 = new ArrayList();

                for (int j2 = 0; j2 < par1InventoryCrafting.getSizeInventory(); ++j2)
                {
                    ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(j2);

                    if (itemstack1 != null)
                    {
                        if (itemstack1.getItem() == Items.dye)
                        {
                            arraylist1.add(Integer.valueOf(ItemDye.field_150922_c[itemstack1.getItemDamage()]));
                        }
                        else if (itemstack1.getItem() == Items.firework_charge)
                        {
                            this.field_92102_a = itemstack1.copy();
                            this.field_92102_a.stackSize = 1;
                        }
                    }
                }

                int[] aint = new int[arraylist1.size()];

                for (int i2 = 0; i2 < aint.length; ++i2)
                {
                    aint[i2] = ((Integer)arraylist1.get(i2)).intValue();
                }

                if (this.field_92102_a != null && this.field_92102_a.hasTagCompound())
                {
                    NBTTagCompound nbttagcompound2 = this.field_92102_a.getTagCompound().getCompoundTag("Explosion");

                    if (nbttagcompound2 == null)
                    {
                        return false;
                    }
                    else
                    {
                        nbttagcompound2.setIntArray("FadeColors", aint);
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    // JAVADOC METHOD $$ func_77572_b
    public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting)
    {
        return this.field_92102_a.copy();
    }

    // JAVADOC METHOD $$ func_77570_a
    public int getRecipeSize()
    {
        return 10;
    }

    public ItemStack getRecipeOutput()
    {
        return this.field_92102_a;
    }
}