package net.minecraftforge.common.smelting;

import static com.google.common.base.Preconditions.checkArgument;
import net.minecraft.item.ItemStack;

/**
 * Base class for {@code SmeltingRecipe} implementations.
 */
public abstract class AbstractSmeltingRecipe implements SmeltingRecipe
{

    protected final float xp;
    protected final int duration;

    protected AbstractSmeltingRecipe(float xp, int duration)
    {
        checkArgument(duration > 0);
        checkArgument(xp >= 0);
        this.duration = duration;
        this.xp = xp;
    }

    @Override
    public final ItemStack getOutput(ItemStack input)
    {
        return matches(input) ? ItemStack.copyItemStack(getOutput0(input)) : null;
    }

    /**
     * Get the raw output for this recipe based on the given input. The resulting stack should not be
     * a defensive copy. This method is only called, if {@code matches} returns true.
     *
     * @param input the input stack
     * @return the raw output stack
     */
    protected abstract ItemStack getOutput0(ItemStack input);

    @Override
    public float getExperience(ItemStack input)
    {
        return xp;
    }

    @Override
    public int getDuration(ItemStack input)
    {
        return duration;
    }

}
