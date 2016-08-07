package net.minecraftforge.ingredients;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.ingredients.capability.IIngredientHandler;
import net.minecraftforge.ingredients.capability.IIngredientSourceProperties;
import net.minecraftforge.ingredients.capability.IngredientSourceProperties;
import net.minecraftforge.ingredients.capability.IngredientSourcePropertiesWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Reference implementation of {@link IIngredientSource} use/extend this, or implement your own.
 * */
public class IngredientSource implements IIngredientSource, IIngredientHandler
{
    @Nullable
    protected IngredientStack ingredient;
    protected int capacity;
    protected TileEntity tile;
    protected boolean canAdd = true;
    protected boolean canRemove = false;
    protected IIngredientSourceProperties[] sourceProps;

    public IngredientSource(int capacity)
    {
        this(null, capacity);
    }

    public IngredientSource(@Nullable IngredientStack ingredientStack, int capacity)
    {
        this.ingredient = ingredientStack;
        this.capacity = capacity;
    }

    public IngredientSource(IngredientStack ingredientStack, int amount, int capacity)
    {
        this(new IngredientStack(ingredientStack.getIngredient(), amount, ingredientStack.materialState, ingredientStack.refinementLevel, ingredientStack.tag), capacity);
    }

    public IngredientSource readFromNBT(NBTTagCompound nbt)
    {
        if(!nbt.hasKey("Empty"))
        {
            IngredientStack stack = IngredientStack.loadIngredientStackFromNBT(nbt);
            setIngredient(stack);
        }
        else
        {
            setIngredient(null);
        }
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if(ingredient != null)
        {
            ingredient.writeToNBT(nbt);
        }
        else
        {
            nbt.setString("Empty","");
        }
        return nbt;
    }

    /* IIngredientSource*/
    @Nullable
    @Override
    public IngredientStack getIngredient() {
        return ingredient;
    }

    public void setIngredient(IngredientStack stack)
    {
        this.ingredient = stack;
    }

    @Override
    public int getIngredientVolume() {
        if(ingredient == null)
        {
            return 0;
        }
        return ingredient.amount;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }

    @Override
    public IngredientSourceInfo getInfo()
    {
        return new IngredientSourceInfo(this);
    }

    @Override
    public int add(IngredientStack resource, boolean doAdd)
    {
        if(!canAddIngredientType(resource))
        {
            return 0;
        }
        return addInternal(resource, doAdd);
    }

    /**
     * Use this method to bypass the restriction from {@link #canAddIngredientType(IngredientStack)}
     * Meant for use by the tank owner when {@link #canAdd} is false
     */
    public int addInternal(IngredientStack resource, boolean doAdd)
    {
        if(resource == null || resource.amount <= 0)
        {
            return 0;
        }

        if(!doAdd)
        {
            if(ingredient == null)
            {
                return Math.min(capacity, resource.amount);
            }

            if(!ingredient.isIngredientEqual(resource))
            {
                return 0;
            }

            return Math.min(capacity - ingredient.amount, resource.amount);
        }

        if(ingredient == null)
        {
            ingredient = new IngredientStack(resource, Math.min(capacity, resource.amount));

            onContentsChanged();

            return ingredient.amount;
        }

        if(!ingredient.isIngredientEqual(resource))
        {
            return 0;
        }
        int fill = capacity - ingredient.amount;

        if(resource.amount < fill)
        {
            ingredient.amount += resource.amount;
            fill = resource.amount;
        }
        else
        {
            ingredient.amount = capacity;
        }

        onContentsChanged();

        return fill;
    }

    @Nullable
    @Override
    public IngredientStack remove(int maxRemoved, boolean doRemove)
    {
        if(!canRemoveIngredientType(getIngredient()))
        {
            return null;
        }
        return removeInternal(maxRemoved, doRemove);
    }

    @Nonnull
    @Override
    public IngredientSourceProperties getPrimarySourceIngredient()
    {
        return new IngredientSourceProperties(getIngredient(), capacity, canAdd, canRemove);
    }

    @Nullable
    @Override
    public IngredientStack remove(IngredientStack resource, boolean doRemove)
    {
        if(resource == null || resource.isIngredientEqual(getIngredient()))
        {
            return null;
        }
        return removeInternal(resource.amount, doRemove);
    }

    /**
     * Use this method to bypass resrtictions from {@link #canRemoveIngredientType(IngredientStack)}
     * Meant for the source owner when {@link #canRemove} is false
     */
    public IngredientStack removeInternal(IngredientStack resource, boolean doRemove)
    {
        if(resource == null || !resource.isIngredientEqual(getIngredient()))
        {
            return null;
        }
        return removeInternal(resource.amount, doRemove);
    }

    /**
     * Use this method to bypass resrtictions from {@link #canRemoveIngredientType(IngredientStack)}
     * Meant for the source owner when {@link #canRemove} is false
     */
    public IngredientStack removeInternal(int maxRemoved, boolean doRemove)
    {
        if(ingredient == null || maxRemoved <= 0)
        {
            return null;
        }
        int loss = maxRemoved;

        if(ingredient.amount < loss)
        {
            loss = ingredient.amount;
        }
        IngredientStack stack = new IngredientStack(ingredient, loss);

        if(doRemove)
        {
            ingredient.amount -= loss;
            if(ingredient.amount <= 0)
            {
                ingredient = null;
            }

            onContentsChanged();
        }

        return stack;
    }

    /**
     * Whether or not the source can be added to with {@link IIngredientHandler}
     *
     * @see IIngredientSourceProperties#canAdd()
     * */
    public boolean canAdd()
    {
        return canAdd;
    }

    /**
     * Whether or not the source can be removed from with {@link IIngredientHandler}
     *
     * @see IIngredientSourceProperties#canRemove()
     * */
    public boolean canRemove()
    {
        return canRemove;
    }

    /**
     * Sets whether this source can be added to with {@link IIngredientHandler}
     *
     * @see IIngredientSourceProperties#canAdd()
     */
    public void setCanAdd(boolean canAdd)
    {
        this.canAdd = canAdd;
    }

    /**
     * Sets whether this source can be removed from with {@link IIngredientHandler}
     *
     * @see IIngredientSourceProperties#canRemove()
     * */
    public void setCanRemove(boolean canRemove)
    {
        this.canRemove = canRemove;
    }

    /**
     * Returns true if the source can have the given ingredient added.
     * Used as a filter for ingredient types.
     * Does not consider the current contents, or capacity,
     * only whether it could ever add this ingredient.
     *
     * @see IIngredientSourceProperties#canAddIngredientType(IngredientStack)
     * */
    public boolean canAddIngredientType(IngredientStack stack)
    {
        return canAdd();
    }

    /**
     * Returns true if the source can have the given ingredient remove.
     * Used as a filter for ingredient types.
     * Does not consider the current contents, or capacity,
     * only whether it could ever remove this ingredient.
     *
     * @see IIngredientSourceProperties#canRemoveIngredientType(IngredientStack)
     * */
    public boolean canRemoveIngredientType(IngredientStack stack)
    {
        return canRemove();
    }

    @Override
    public IIngredientSourceProperties[] getContainerProperties()
    {
        if(this.sourceProps == null)
        {
            this.sourceProps = new IIngredientSourceProperties[] { new IngredientSourcePropertiesWrapper(this)};
        }
        return this.sourceProps;
    }

    protected void onContentsChanged()
    {
    }
}
