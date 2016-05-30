package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class AnvilRecipeHandler
{
    public static final AnvilRecipeHandler INSTANCE = new AnvilRecipeHandler();
    private List<IAnvilRecipe> anvilRecipes = new ArrayList<IAnvilRecipe>();

    /**
     * Add an Anvil Recipe
     */
    public void add(IAnvilRecipe recipe)
    {
        anvilRecipes.add(recipe);
    }

    /**
     * Remove an Anvil Recipe
     */
    public boolean remove(IAnvilRecipe recipe)
    {
        return anvilRecipes.remove(recipe);
    }

    /**
     * Get the list of registered recipes
     */
    public List<IAnvilRecipe> getAnvilRecipes()
    {
        return ImmutableList.copyOf(this.anvilRecipes);
    }

    public boolean onPickupFromSlot(IInventory inputSlots, String name, World world)
    {
        String newName = (!Strings.isNullOrEmpty(name) && !name.equals(inputSlots.getStackInSlot(0).getDisplayName())) ? name : "";
        if (getRepairResult(inputSlots.getStackInSlot(0), inputSlots.getStackInSlot(1), newName, world) != null)
        {
            doRepair(inputSlots.getStackInSlot(0), inputSlots.getStackInSlot(1), newName, world);
            for (int slot = 0; slot < 2; slot++)
            {
                if (inputSlots.getStackInSlot(slot) != null && inputSlots.getStackInSlot(slot).stackSize < 1)
                {
                    inputSlots.setInventorySlotContents(slot, null);
                }
            }
            return true;
        }
        return false;
    }

    public boolean updateRepairOutput(ContainerRepair container, ItemStack inputLeft, ItemStack inputRight, IInventory outputSlot, String name, int baseCost, World world)
    {
        String newName = (!Strings.isNullOrEmpty(name) && !name.equals(inputLeft.getDisplayName())) ? name : "";
        ItemStack result = getRepairResult(inputLeft, inputRight, newName, world);
        if (result != null)
        {
            outputSlot.setInventorySlotContents(0, result);
            container.materialCost = 0;
            container.maximumCost = getRepairCost(inputLeft, inputRight, newName, world);
            if (!Strings.isNullOrEmpty(newName))
            {
                outputSlot.getStackInSlot(0).setStackDisplayName(name);
            }
            return true;
        }
        return false;
    }

    public ItemStack getRepairResult(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
    {
        IAnvilRecipe recipe = getRecipe(inputLeft, inputRight, newName, world);
        return recipe == null ? null : recipe.getResult(inputLeft, inputRight, newName, world);
    }

    public int getRepairCost(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
    {
        IAnvilRecipe recipe = getRecipe(inputLeft, inputRight, newName, world);
        return recipe == null ? 0 : recipe.getExpCost(inputLeft, inputRight, newName, world);
    }

    public void doRepair(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
    {
        IAnvilRecipe recipe = getRecipe(inputLeft, inputRight, newName, world);
        if (recipe != null)
        {
            recipe.doRepair(inputLeft, inputRight, newName, world);
        }
    }

    private IAnvilRecipe getRecipe(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
    {
        for (IAnvilRecipe recipe : anvilRecipes)
        {
            if (recipe.matches(inputLeft, inputRight, newName, world))
            {
                return recipe;
            }
        }
        return null;
    }
}
