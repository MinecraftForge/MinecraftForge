package net.minecraftforge.common.smelting;

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
    boolean matches(ItemStack input);

    /**
     * Get the output for the recipe or null if the recipe does not match the input.
     *
     * @param input the input stack
     * @return the output stack
     */
    ItemStack getOutput(ItemStack input);

    /**
     * The time in ticks it takes for this recipe to apply in a furnace or -1
     * if the recipe does not match the input.
     *
     * @param input the input stack
     * @return the cooking time in ticks
     */
    int getDuration(ItemStack input);

    /**
     * The experience provided by this recipe.
     *
     * @param input the input stack
     * @return the amount of experience
     */
    float getExperience(ItemStack input);

}
