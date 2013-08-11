package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class NBTShapelessRecipes implements IRecipe
{
    /** Is the ItemStack that you get when craft the recipe. */
    private final ItemStack recipeOutput;

    /** Is a List of ItemStack that composes the recipe. */
    public final List recipeItems;

    /**
     * Required???? private boolean field_92101_f = false;
     */

    public NBTShapelessRecipes(ItemStack par1ItemStack, List par2List)
    {
        this.recipeOutput = par1ItemStack;
        this.recipeItems = par2List;
    }

    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world)
    {
        ArrayList arraylist = new ArrayList(this.recipeItems);

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                ItemStack itemstack = inventorycrafting.getStackInRowAndColumn(
                        j, i);

                if (itemstack != null)
                {
                    boolean flag = false;
                    Iterator iterator = arraylist.iterator();

                    while (iterator.hasNext())
                    {
                        ItemStack itemstack1 = (ItemStack) iterator.next();

                        if (itemstack.itemID == itemstack1.itemID
                                && (itemstack1.getItemDamage() == Short.MAX_VALUE
                                || itemstack.getItemDamage() == itemstack1.getItemDamage()))
                        {
                            if (itemstack1.getTagCompound() != null)
                            {
                                if (itemstack.getTagCompound() != null
                                        && compareNBTTags(
                                                itemstack1.getTagCompound(),
                                                itemstack.getTagCompound()))
                                {
                                    flag = true;
                                    arraylist.remove(itemstack1);
                                    break;
                                }
                            }
                            else
                            {
                                // vanilla
                                flag = true;
                                arraylist.remove(itemstack1);
                                break;
                            }
                        }
                    }

                    if (!flag)
                    {
                        return false;
                    }
                }
            }
        }

        return arraylist.isEmpty();
    }

    private boolean compareNBTTags(NBTTagCompound tagCompound, NBTTagCompound tagCompound2)
    {
        Iterator iterator = tagCompound.getTags().iterator();
        while (iterator.hasNext())
        {
            NBTBase childCompound = (NBTBase) iterator.next();

            if (!(childCompound instanceof NBTTagCompound
                    && tagCompound2.hasKey(childCompound.getName())))
            {
                return childCompound.equals(tagCompound2.getTag(childCompound.getName()));
            }
            else if (!tagCompound2.hasKey(childCompound.getName())
                    || !compareNBTTags(
                            (NBTTagCompound) childCompound,
                            (NBTTagCompound) tagCompound2
                                .getTag(childCompound.getName())))
            {

                return false;
            }
        }

        return true;

    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventorycrafting)
    {
        return this.recipeOutput.copy();
    }

    @Override
    public int getRecipeSize()
    {
        return this.recipeItems.size();
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return this.recipeOutput;
    }

}
