package net.minecraftforge.common;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class SimpleAnvilRecipe implements IAnvilRecipe
{
    protected final List<ItemStack> left;
    protected final List<ItemStack> right;
    protected final ItemStack output;
    protected final int expCost;
    protected final int matCost;

    public SimpleAnvilRecipe(ItemStack result, Object inputLeft, Object inputRight, int materialCost, int experienceCost)
    {
        this.output = result.copy();
        this.left = processInput(inputLeft);
        this.right = processInput(inputRight);
        this.matCost = materialCost;
        this.expCost = experienceCost;
        if (this.left == null || (this.right == null && inputRight != null)) {
            String ret = "Invalid simple anvil recipe: " + inputLeft + ", " + inputRight + ", " + this.output;
            throw new RuntimeException(ret);
        }
    }

    private List<ItemStack> processInput(Object input)
    {
        if (input instanceof String)
        {
            return OreDictionary.getOres((String)input);
        }
        else
        {
            ItemStack stack = null;
            if (input instanceof ItemStack)
            {
                stack = ((ItemStack)input).copy();
            }
            else if (input instanceof Item)
            {
                stack = new ItemStack((Item)input, 1, OreDictionary.WILDCARD_VALUE);
            }
            else if (input instanceof Block)
            {
                stack = new ItemStack((Block)input, 1, OreDictionary.WILDCARD_VALUE);
            }
            return stack != null ? Collections.singletonList(stack) : OreDictionary.EMPTY_LIST;
        }
    }

    @Override
    public boolean matches(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
    {
        return checkMatch(this.left, inputLeft, false) && checkMatch(this.right, inputRight, true);
    }

    private boolean checkMatch(List<ItemStack> target, ItemStack tableItem, boolean checksize)
    {
        if (target == OreDictionary.EMPTY_LIST)
        {
            return tableItem == null;
        }
        else if (checksize && tableItem.stackSize < this.matCost)
        {
            return false;
        }
        else
        {
            for (ItemStack stack : target)
            {
                if (stack != null && OreDictionary.itemMatches(stack, tableItem, false))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the input for this recipe, any mod accessing this value should never
     * manipulate the values in this array as it will effect the recipe itself.
     *
     * @return The recipe input vales.
     */
    @Override
    public Pair<List<ItemStack>, List<ItemStack>> getInput()
    {
        return Pair.of(this.left, this.right);
    }

    /**
     * The material cost of this recipe
     *
     * @return Returns the amount the inputRight item's stacksize will decrease by.
     */
    @Override
    public int getMaterialCost()
    {
        return this.matCost;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getResult(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
    {
        return this.output.copy();
    }

    /**
     * Returns the experience cost of this recipe
     */
    @Override
    public int getExpCost(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
    {
        return this.expCost;
    }

    @Override
    public void doRepair(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
    {
        if (inputLeft != null)
        {
            inputLeft.stackSize = 0;
        }
        if (inputRight != null)
        {
            inputRight.stackSize -= this.matCost;
        }
    }
}
