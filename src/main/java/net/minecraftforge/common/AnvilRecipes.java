package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.List;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class AnvilRecipes
{
    private static final AnvilRecipes instance = new AnvilRecipes();
    private List<IAnvilRecipe> anvilRecipes = new ArrayList<IAnvilRecipe>();

    public static AnvilRecipes getInstance()
    {
        return instance;
    }

    public interface IAnvilRecipe
    {
        /**
         * Returns the input for this recipe, any mod accessing this value should never
         * manipulate the values in this array as it will effect the recipe itself.
         */
        List[] getInputs();

        /**
         * Used to check if a recipe matches current anvil inventory
         * @param inputLeft The item in the left input slot
         * @param inputRight The item in the right input slot
         * @param newName The items new name, empty if not being renamed.
         * @param world The current world
         */
        boolean matches(ItemStack inputLeft, ItemStack inputRight, String newName, World world);

        /**
         * Returns an Item that is the result of this recipe
         * @param inputLeft The item in the left input slot
         * @param inputRight The item in the right input slot
         * @param newName The items new name, empty if not being renamed.
         * @param world The current world
         */
        ItemStack getResult(ItemStack inputLeft, ItemStack inputRight, String newName, World world);

        /**
         * Returns the experience cost of this recipe
         * @param inputLeft The item in the left input slot
         * @param inputRight The item in the right input slot
         * @param newName The items new name, empty if not being renamed.
         * @param world The current world
         */
        int getCost(ItemStack inputLeft, ItemStack inputRight, String newName, World world);

        /**
         * Called when the repair is done, Use this to modify the input stacks as needed. 
         * To remove the item set it's stacksize to 0 
         * @param inputLeft The item in the left input slot
         * @param inputRight The item in the right input slot
         * @param newName The items new name, empty if not being renamed.
         * @param world The current world
         */
        void doRepair(ItemStack inputLeft, ItemStack inputRight, String newName, World world);
    }

    /**
     * Add an Anvil Recipe
     */
    public void addRepair(IAnvilRecipe recipe)
    {
        anvilRecipes.add(recipe);
    }

    /**
     * Remove an Anvil Recipe
     */
    public void removeRepair(IAnvilRecipe recipe)
    {
        if(anvilRecipes.contains(recipe))
        {
            anvilRecipes.remove(recipe);
        }
    }

    /**
     * Get the list of registered recipes
     */
    public List<IAnvilRecipe> getTableRecipes()
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
            if(!Strings.isNullOrEmpty(newName))
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
        return recipe == null ? 0 :  recipe.getCost(inputLeft, inputRight, newName, world);
    }

    public void doRepair(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
    {
        IAnvilRecipe recipe = getRecipe(inputLeft, inputRight, newName, world);
        if (recipe != null) {
            recipe.doRepair(inputLeft, inputRight, newName, world);
        }
    }

    private IAnvilRecipe getRecipe(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
    {
        for(IAnvilRecipe recipe : anvilRecipes)
        {
            if(recipe.matches(inputLeft, inputRight, newName, world))
            {
                return recipe;
            }
        }
        return null;
    }
}