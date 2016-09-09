package net.minecraftforge.ingredients.capability.templates;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.ingredients.IngredientStack;
import net.minecraftforge.ingredients.capability.CapabilityIngredientHandler;
import net.minecraftforge.ingredients.capability.IIngredientHandler;
import net.minecraftforge.ingredients.capability.IIngredientSourceProperties;
import net.minecraftforge.ingredients.capability.IngredientSourceProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * IngredientHandlerItemSTackSimple is a template capability provider for ItemStacks
 * Data is stored directly in the NBT
 *
 * This implementation only allows for containers to be completely filled or emptied.
 * This functionality is the ingredient equivalient to vanilla buckets.
 * */
public class IngredientHandlerItemStackSimple implements IIngredientHandler, ICapabilityProvider
{
    public static final String INGREDIENT_NBT_KEY = "Ingredient";

    protected final ItemStack container;
    protected final int capacity;
    protected boolean addable = true;
    protected boolean removable = true;

    /**
     * @param container  The container itemStack, data is stored on it directly as NBT.
     * @param capacity   The maximum capacity of this fluid tank.
     */
    public IngredientHandlerItemStackSimple(ItemStack container, int capacity)
    {
        this.container = container;
        this.capacity = capacity;
    }

    public IngredientHandlerItemStackSimple setCanAdd(boolean canAdd)
    {
        addable = canAdd;
        return this;
    }

    public IngredientHandlerItemStackSimple setCanRemove(boolean canRemove)
    {
        removable = canRemove;
        return this;
    }

    @Nullable
    public IngredientStack getIngredient()
    {
        NBTTagCompound tag = container.getTagCompound();

        if(tag == null || !tag.hasKey(INGREDIENT_NBT_KEY))
        {
            return null;
        }
        return IngredientStack.loadIngredientStackFromNBT(tag.getCompoundTag(INGREDIENT_NBT_KEY));
    }

    protected void setIngredient(IngredientStack ingredient)
    {
        if(!container.hasTagCompound())
        {
            container.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound tag = new NBTTagCompound();
        ingredient.writeToNBT(tag);
        container.getTagCompound().setTag(INGREDIENT_NBT_KEY, tag);
    }

    @Override
    public IIngredientSourceProperties[] getContainerProperties() {
        return new IIngredientSourceProperties[0];
    }

    @Override
    public int add(IngredientStack resource, boolean doAdd)
    {
        if (container.stackSize != 1 || resource == null || resource.amount <= 0 || !canAddIngredientType(resource))
        {
            return 0;
        }
        IngredientStack contained = getIngredient();

        if(contained == null)
        {
            int add = Math.min(capacity, resource.amount);

            if(add == capacity)
            {
                if (doAdd)
                {
                    IngredientStack added = resource.copy();
                    added.amount = add;
                    setIngredient(added);
                }
                return add;
            }
        }
        return 0;
    }

    @Nullable
    @Override
    public IngredientStack remove(IngredientStack resource, boolean doRemove) {
        if (container.stackSize != 1 || resource == null || resource.amount <= 0 || !resource.isIngredientEqual(getIngredient()))
        {
            return null;
        }
        return remove(resource.amount, doRemove);
    }

    @Nullable
    @Override
    public IngredientStack remove(int maxRemoved, boolean doRemove) {
        if(container.stackSize != 1 || maxRemoved <= 0)
        {
            return null;
        }
        IngredientStack contained = getIngredient();

        if(contained == null || contained.amount <= 0 || !canRemoveIngredientType(contained))
        {
            return null;
        }
        final int remove = Math.min(contained.amount, maxRemoved);

        if(remove == capacity)
        {
            IngredientStack removed = contained.copy();

            if (doRemove)
            {
                setContainerToEmpty();
            }
            return removed;
        }
        return null;
    }

    @Nonnull
    @Override
    public IngredientSourceProperties getPrimarySourceIngredient() {
        return new IngredientSourceProperties(getIngredient(), capacity, addable, removable);
    }

    public boolean canAddIngredientType(IngredientStack resource)
    {
        return addable;
    }

    public boolean canRemoveIngredientType(IngredientStack resource)
    {
        return removable;
    }

    /**
     * Override this method for special handling.
     * Can be used to swap out container's item for a different one with "container.setItem".
     * Can be used to destroy the container with "container.stackSize--"
     * */
    public void setContainerToEmpty()
    {
        container.getTagCompound().removeTag(INGREDIENT_NBT_KEY);
    }


    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityIngredientHandler.INGREDIENT_HANDLER_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityIngredientHandler.INGREDIENT_HANDLER_CAPABILITY ? (T) this : null;
    }

    /**
     * Destroys the container when it's empty.
     * */
    public static class Consumable extends IngredientHandlerItemStackSimple
    {
        public Consumable(ItemStack container, int capacity)
        {
            super(container, capacity);
        }

        @Override
        public void setContainerToEmpty()
        {
            super.setContainerToEmpty();
            container.stackSize--;
        }
    }

    /**
     * Swaps the container Item for a supplied empty stack.
     * */
    public static class SwapEmpty extends IngredientHandlerItemStackSimple
    {
        protected final ItemStack emptyContainer;

        public SwapEmpty(ItemStack container, ItemStack emptyContainer, int capacity)
        {
            super(container, capacity);
            this.emptyContainer = emptyContainer;
        }

        @Override
        public void setContainerToEmpty()
        {
            super.setContainerToEmpty();
            container.deserializeNBT(emptyContainer.serializeNBT());
        }
    }
}
