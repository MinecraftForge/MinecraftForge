package net.minecraftforge.common.smelting;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * A smelting recipe that always produces the same output stack regardless of input.
 */
public interface ISimpleOutputSmeltingRecipe extends ISmeltingRecipe
{

    /**
     * Get the output stack for this recipe.
     *
     * @return the output stack
     */
    @Nonnull
    ItemStack getOutput();

}
