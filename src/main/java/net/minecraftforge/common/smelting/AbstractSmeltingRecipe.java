package net.minecraftforge.common.smelting;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import net.minecraft.item.ItemStack;

/**
 * Base class for {@code SmeltingRecipe} implementations with a set duration value.
 */
public abstract class AbstractSmeltingRecipe implements SmeltingRecipe
{
    protected final int duration;

    protected AbstractSmeltingRecipe(int duration)
    {
        checkArgument(duration > 0);
        this.duration = duration;
    }

    @Override
    public int getDuration(@Nonnull ItemStack input)
    {
        return duration;
    }

}
