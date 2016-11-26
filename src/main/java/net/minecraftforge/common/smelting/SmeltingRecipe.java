package net.minecraftforge.common.smelting;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * Base interface for furnace recipes.
 *
 * @see SmeltingRecipeRegistry
 */
public interface SmeltingRecipe
{

    /**
     * Determine if the recipe matches the given input.
     *
     * @param input the input stack
     * @return true if the recipe matches
     */
    boolean matches(@Nonnull ItemStack input);

    /**
     * Get the output for the recipe.
     *
     * @param input the input stack
     * @return the output stack
     */
    @Nonnull
    ItemStack getOutput(@Nonnull ItemStack input);

    /**
     * The time in ticks it takes for this recipe to apply in a furnace.
     *
     * @param input the input stack
     * @return the cooking time in ticks
     */
    int getDuration(@Nonnull ItemStack input);

    /**
     * Provide a list of Items that are possible as a result of {@code getOutput}. This is needed for e.g. crafting stats.
     * This need not be an exhaustive list. Only Item and metadata are required information. As in: A recipe that produces ItemStacks
     * of Items.STICK with metadata 0 but various different NBT tags only has to return an ItemStack of Items.STICK with metadata 0 here,
     * there is no requirement to list all NBT combinations.
     *
     * <br><br><b>The resulting ItemStacks <i>must not</i> be modified!</b>
     *
     * @return a list of outputs
     */
    Collection<ItemStack> getPossibleOutputs();

}
