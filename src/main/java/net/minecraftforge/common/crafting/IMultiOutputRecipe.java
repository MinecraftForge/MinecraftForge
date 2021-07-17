package net.minecraftforge.common.crafting;

import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public interface IMultiOutputRecipe<T extends IInventory> extends IRecipe<T> {
    @Deprecated
    @Override
    default ItemStack getCraftingResult(T inputs) {
        return getCraftingResults(inputs).get(0);
    }

    /**
     * Gets the additional results from the recipe.
     * @return The list of results.
     *    The float value on the pair is the chance for that result to happen.
     *    A chance of 1.0 means 100%.
     */
    List<Pair<ItemStack, Float>> getAdditionalOutputs();

    /**
     * Gets the final results
     * @param inputs The inventory exposing the input slots
     * @return The list of outputs from the crafting.
     */
    NonNullList<ItemStack> getCraftingResults(T inputs);

    abstract class Impl<T extends IInventory> implements IMultiOutputRecipe<T>
    {
        protected static final Random rand = new Random();

        private final ItemStack primaryOutput;
        private final List<Pair<ItemStack, Float>> additionalOutputs;

        protected Impl(ItemStack primaryOutput, List<Pair<ItemStack, Float>> additionalOutputs)
        {
            this.primaryOutput = primaryOutput;
            this.additionalOutputs = additionalOutputs;
        }

        @Override
        public ItemStack getRecipeOutput()
        {
            return primaryOutput;
        }

        @Override
        public List<Pair<ItemStack, Float>> getAdditionalOutputs()
        {
            return Collections.unmodifiableList(additionalOutputs);
        }

        @Override
        public NonNullList<ItemStack> getCraftingResults(T inputs)
        {
            NonNullList<ItemStack> outputs = NonNullList.create();
            outputs.add(primaryOutput);
            for(Pair<ItemStack, Float> additional : additionalOutputs)
            {
                if (rand.nextFloat() < additional.getSecond())
                    outputs.add(additional.getFirst());
            }
            return outputs;
        }
    }
}
