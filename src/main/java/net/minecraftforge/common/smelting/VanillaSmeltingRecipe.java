package net.minecraftforge.common.smelting;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

final class VanillaSmeltingRecipe implements SmeltingRecipe
{

    private final ItemStack input, output;

    VanillaSmeltingRecipe(@Nonnull ItemStack input, @Nonnull ItemStack output)
    {
        this.input = checkNotNull(input);
        this.output = checkNotNull(output);
    }

    public final boolean matches(@Nonnull ItemStack input)
    {
        return OreDictionary.itemMatches(this.input, input, false);
    }

    @Nonnull
    @Override
    public final ItemStack getOutput(@Nonnull ItemStack input)
    {
        return matches(input) ? output.copy() : ItemStack.field_190927_a;
    }

    @Override
    public int getDuration(@Nonnull ItemStack input)
    {
        return SmeltingRecipeRegistry.DEFAULT_COOK_TIME;
    }

    @Override
    public Collection<Item> getPossibleOutputs()
    {
        return Collections.singleton(output.getItem());
    }
}
