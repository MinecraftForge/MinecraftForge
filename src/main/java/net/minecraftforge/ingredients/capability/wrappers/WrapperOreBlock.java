package net.minecraftforge.ingredients.capability.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.ingredients.Ingredient;
import net.minecraftforge.ingredients.IngredientStack;
import net.minecraftforge.ingredients.capability.CapabilityIngredientHandler;
import net.minecraftforge.ingredients.capability.IIngredientHandler;
import net.minecraftforge.ingredients.capability.IIngredientSourceProperties;
import net.minecraftforge.ingredients.capability.IngredientSourceProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Wrapper for vanilla ore blocks
 * */
public class WrapperOreBlock implements IIngredientHandler, ICapabilityProvider
{
    protected final ItemStack source;

    protected final IngredientStack base;

    protected final IngredientStack ore;

    public WrapperOreBlock(ItemStack source, Ingredient ore, Ingredient base)
    {
        this.source = source;
        this.ore = new IngredientStack(ore, Ingredient.ORE_VOLUME);
        this.base = new IngredientStack(base, Ingredient.ORE_VOLUME_BASE);
    }

    @Override
    public IIngredientSourceProperties[] getContainerProperties()
    {
        return new IIngredientSourceProperties[]{ new IngredientSourceProperties(ore, ore.amount, false, true), new IngredientSourceProperties(base, base.amount, false, true)};
    }

    @Override
    public int add(IngredientStack resource, boolean doAdd)
    {
        return 0; //Immutable so always 0
    }

    @Nullable
    @Override
    public IngredientStack remove(IngredientStack resource, boolean doRemove)
    {
        if(source.stackSize < 1 || resource == null || resource.amount < 1)
        {
            return null;
        }

        if(ore.isIngredientEqual(resource))
        {
            int loss = ore.amount * source.stackSize;

            if(resource.amount < loss)
            {
                loss = resource.amount;
            }

            if(doRemove)
            {
                //Cieling the remainder away so that the stack is consumed for the overages.
                source.stackSize -= Math.ceil(ore.amount / loss);
            }

            return new IngredientStack(ore, loss);
        }

        if(base.isIngredientEqual(resource))
        {
            int loss = base.amount * source.stackSize;

            if(resource.amount < loss)
            {
                loss = resource.amount;
            }

            if(doRemove)
            {
                //Ceiling the remainder away so that the stack is consumed for the overages.
                source.stackSize -= Math.ceil(base.amount / loss);
            }

            return new IngredientStack(base, loss);
        }

        return null;
    }

    @Nullable
    @Override
    public IngredientStack remove(int maxRemoved, boolean doRemove)
    {
        if(source.stackSize < 1 || maxRemoved < 1)
        {
            return null;
        }
        IngredientStack primary = getPrimarySourceIngredient().getContents();

        if(primary == null)
        {
            return null;
        }
        int loss = primary.amount * source.stackSize;

        if(maxRemoved < loss)
        {
            loss = maxRemoved;
        }

        if(doRemove)
        {
            //Ceiling the remainder away so that the stack is consumed for the overages.
            source.stackSize -= Math.ceil(primary.amount / loss);
        }
        return new IngredientStack(primary, loss);
    }

    @Nonnull
    @Override
    public IngredientSourceProperties getPrimarySourceIngredient()
    {
        //The ore component is always the 'primary' source
        return new IngredientSourceProperties(ore, ore.amount, false, true);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityIngredientHandler.INGREDIENT_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityIngredientHandler.INGREDIENT_HANDLER_CAPABILITY)
        {
            return CapabilityIngredientHandler.INGREDIENT_HANDLER_CAPABILITY.cast(this);
        }
        return null;
    }
}
