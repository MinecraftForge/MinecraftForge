package net.minecraftforge.common.smelting;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * A smelting recipe that checks against a single input.
 */
public class SimpleSmeltingRecipe extends AbstractSmeltingRecipe
{

    protected final ItemStack input, output;

    public SimpleSmeltingRecipe(@Nonnull ItemStack input, @Nonnull ItemStack output, int duration)
    {
        super(duration);
        this.input = checkNotNull(input);
        this.output = checkNotNull(output);
    }

    @Override
    public boolean matches(@Nonnull ItemStack input)
    {
        return OreDictionary.itemMatches(this.input, input, false);
    }

    @Nonnull
    @Override
    public ItemStack getOutput(@Nonnull ItemStack input)
    {
        return output.copy();
    }

    @Override
    public Collection<Item> getPossibleOutputs()
    {
        return Collections.singleton(output.getItem());
    }
}
