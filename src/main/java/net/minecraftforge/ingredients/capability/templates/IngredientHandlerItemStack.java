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
 * IngredientHandlerItemStack is a template capability provider for ItemStacks.
 *
 * This class allows an itemStack to contain any partial level of an ingredient up to its capacity, unlike {@link IngredientHandlerItemStackSimple}
 *
 * Additional examples are provided to enable consumable fluid containers (see {@link Consumable}),
 * fluid containers with different empty and full items (see {@link SwapEmpty},
 */
public class IngredientHandlerItemStack implements IIngredientHandler, ICapabilityProvider
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
    public IngredientHandlerItemStack(ItemStack container, int capacity)
    {
        this.container = container;
        this.capacity = capacity;
    }

    public IngredientHandlerItemStack setCanAdd(boolean canAdd)
    {
        addable = canAdd;
        return this;
    }

    public IngredientHandlerItemStack setCanRemove(boolean canRemove)
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
        return new IIngredientSourceProperties[]{ new IngredientSourceProperties(getIngredient(), capacity, addable, removable)};
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

            if(doAdd)
            {
                IngredientStack added = resource.copy();
                added.amount = add;
                setIngredient(added);
            }

            return add;
        }
        else
        {
            if(contained.isIngredientEqual(resource))
            {
                int add = Math.min(capacity - contained.amount, resource.amount);

                if(doAdd && add > 0)
                {
                    contained.amount += add;
                    setIngredient(contained);
                }
                return add;
            }
        }
        return 0;
    }

    @Nullable
    @Override
    public IngredientStack remove(IngredientStack resource, boolean doRemove)
    {
        if (container.stackSize != 1 || resource == null || resource.amount <= 0 || !resource.isIngredientEqual(getIngredient()))
        {
            return null;
        }
        return remove(resource.amount, doRemove);
    }

    @Nullable
    @Override
    public IngredientStack remove(int maxRemoved, boolean doRemove)
    {
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
        IngredientStack removed = contained.copy();
        removed.amount = remove;

        if(doRemove)
        {
            contained.amount -= remove;

            if(contained.amount == 0)
            {
                setContainerToEmpty();
            }
            else
            {
                setIngredient(contained);
            }

            return  removed;
        }

        return null;
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

    @Nonnull
    @Override
    public IngredientSourceProperties getPrimarySourceIngredient()
    {
        return new IngredientSourceProperties(getIngredient(), capacity, addable, removable);
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
    public static class Consumable extends IngredientHandlerItemStack
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
    public static class SwapEmpty extends IngredientHandlerItemStack
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
