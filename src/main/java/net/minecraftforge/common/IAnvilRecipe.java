package net.minecraftforge.common;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IAnvilRecipe
{
    /**
     * Returns the input for this recipe, any mod accessing this value should never
     * manipulate the values in this array as it will effect the recipe itself.
     *
     * @return The recipes input vales.
     */
    Pair<List<ItemStack>, List<ItemStack>> getInput();

    /**
     * The material cost of this recipe
     *
     * @return Returns the amount the inputRight item's stacksize will decrease by.
     */
    int getMaterialCost();

    /**
     * Used to check if a recipe matches current anvil inventory
     *
     * @param inputLeft  The item in the left input slot
     * @param inputRight The item in the right input slot
     * @param newName    The items new name, empty if not being renamed.
     * @param world      The current world
     */
    boolean matches(ItemStack inputLeft, ItemStack inputRight, String newName, World world);

    /**
     * Returns an Item that is the result of this recipe
     *
     * @param inputLeft  The item in the left input slot
     * @param inputRight The item in the right input slot
     * @param newName    The items new name, empty if not being renamed.
     * @param world      The current world
     */
    ItemStack getResult(ItemStack inputLeft, ItemStack inputRight, String newName, World world);

    /**
     * Returns the experience cost of this recipe
     *
     * @param inputLeft  The item in the left input slot
     * @param inputRight The item in the right input slot
     * @param newName    The items new name, empty if not being renamed.
     * @param world      The current world
     */
    int getExpCost(ItemStack inputLeft, ItemStack inputRight, String newName, World world);

    /**
     * Called when the repair is done, Use this to modify the input stacks as needed.
     * To remove the item set it's stacksize to 0
     *
     * @param inputLeft  The item in the left input slot
     * @param inputRight The item in the right input slot
     * @param newName    The items new name, empty if not being renamed.
     * @param world      The current world
     */
    void doRepair(ItemStack inputLeft, ItemStack inputRight, String newName, World world);
}
