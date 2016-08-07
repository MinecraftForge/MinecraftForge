package net.minecraftforge.ingredients;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

/**
 * FluidStack equivalent for Ingredient.
 * <p/>
 * NOTE: Equality is based purely on the Ingredient.
 * */
public class IngredientStack
{

    public int amount;
    public int temperature = 300;
    public EnumMatterState materialState;
    public EnumRefinementLevel refinementLevel;
    public NBTTagCompound tag;
    private RegistryDelegate<Ingredient> ingredientDelegate;

    public IngredientStack(Ingredient ingredient, int amount, EnumMatterState state, EnumRefinementLevel refinement, NBTTagCompound tagData)
    {
        if (ingredient == null)
        {
            FMLLog.bigWarning("Null ingredient supplied to ingredientstack. Did you try and create a stack for an unregistered ingredient?");
            throw new IllegalArgumentException("Cannot create an ingredientstack from a null ingredient");
        }
        else if (!IngredientRegistry.isIngredientRegistered(ingredient))
        {
            FMLLog.bigWarning("Failed attempt to create an IngredientStack for an unregistered Ingredient %s (type %s)", ingredient.getName(), ingredient.getClass().getName());
            throw new IllegalArgumentException("Cannot create an ingredientstack from an unregistered ingredient");
        }
        this.ingredientDelegate = IngredientRegistry.makeDelegate(ingredient);
        this.amount = amount;
        this.materialState = state;
        this.refinementLevel = refinement;
        if(tagData != null)
        {
            this.tag = tagData.copy();
        }
    }

    public IngredientStack(Ingredient ingredient, int amount)
    {
        this(ingredient, amount, ingredient.getRoomTempState(), EnumRefinementLevel.RAW, null);
    }

    public IngredientStack(Ingredient ingredient, int amount, EnumMatterState state, EnumRefinementLevel refinement)
    {
        this(ingredient, amount, state, refinement, null);
    }

    public IngredientStack(IngredientStack resource, int amount)
    {
        this(resource.getIngredient(), amount, resource.materialState, resource.refinementLevel, resource.tag);
    }

    public IngredientStack(Ingredient ingredient, int amount, EnumRefinementLevel refinement)
    {
        this(ingredient, amount, ingredient.roomTempState, refinement);
    }

    /**
     * Provides a safe method for retrieving an IngredientStack- if the Ingredient is invalid, the stack
     * will return null
     * */
    public static IngredientStack loadIngredientStackFromNBT(NBTTagCompound nbt)
    {
        if(nbt == null)
        {
            return null;
        }
        String ingredientName = nbt.getString("IngredientName");

        if(ingredientName == null)
        {
            return null;
        }
        Ingredient ingredient = IngredientRegistry.getIngredient(ingredientName);

        if(ingredient == null)
        {
            return null;
        }
        EnumMatterState state = EnumMatterState.fromByte(nbt.getByte("MaterialState"));
        EnumRefinementLevel refinement = EnumRefinementLevel.fromByte(nbt.getByte("RefinementLevel"));

        IngredientStack stack = new IngredientStack(ingredient, nbt.getInteger("Volume"), state, refinement);

        if(nbt.hasKey("Temperature"))
        {
            stack.temperature = nbt.getInteger("Temperature"); //Defaults back to room temp if not available
            if(stack.temperature < 0) //impossibility check
            {
                stack.temperature = 0;
            }
        }

        if(nbt.hasKey("Tag"))
        {
            stack.tag = nbt.getCompoundTag("Tag");
        }

        return stack;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setString("IngredientName", IngredientRegistry.getIngredientName(getIngredient()));
        nbt.setInteger("Volume", amount);
        nbt.setByte("MaterialState", (byte) materialState.ordinal());
        nbt.setByte("RefinementLevel", (byte) refinementLevel.ordinal());

        if(temperature != 300)
        {
            nbt.setInteger("Temperature", temperature);
        }

        if(tag != null)
        {
            nbt.setTag("Tag", tag);
        }

        return nbt;
    }

    public final Ingredient getIngredient()
    {
        return ingredientDelegate.get();
    }

    public String getLocalizedName()
    {
        return this.getIngredient().getLocalizedName(this);
    }

    /**
     * Returns a copy of the Ingredient Stack
     * */
    public IngredientStack copy()
    {
        return new IngredientStack(getIngredient(), amount, materialState, refinementLevel, tag);
    }

    /**
     * Determines if the ingredient ID, material state, refinement level, and tags are equal.
     * This doesn't check amounts
     *
     * @param other
     *          The IngredientStack for comparison
     * @return true if the Ingredient (ID, MatterState, RefinementLevel, and tags) are the same
     * */
    public boolean isIngredientEqual(IngredientStack other)
    {
        return other != null && getIngredient() == other.getIngredient() && materialState == other.materialState && refinementLevel == other.refinementLevel && isIngredientTagEqual(other);
    }

    private boolean isIngredientTagEqual(IngredientStack other)
    {
        return tag == null ? other.tag == null : other.tag == null ? false : tag.equals(other.tag);
    }

    /**
     * Determines if the NBT Tags are equal. Useful if the IngredientIDs are known to be equal
     * */
    public static boolean areIngredientStacksEqual(IngredientStack stack1, IngredientStack stack2)
    {
        return stack1 == null && stack2 == null ? true : stack1 == null || stack2 == null ? false : stack1.isIngredientTagEqual(stack2);
    }

    /**
     * Determines if the Ingredients are equal and this stack is larger
     *
     * @param other
     *          The IngredientStack for comparison
     * @return true if this IngredientStack contains the other IngredientStack (same ingredient and >= amount)
     * */
    public boolean containsIngredient(IngredientStack other)
    {
        return this.isIngredientEqual(other) && this.amount >= other.amount;
    }

    /**
     * Determins if the ingredients, temperatures, and amounts are equal
     *
     * @param other
     *          the IngredientStack for comparison
     * @return true if the two IngredientStacks are exactly the same
     * */
    public boolean isIngredientStackIdentical(IngredientStack other)
    {
        return this.isIngredientEqual(other) && this.amount == other.amount && this.temperature == other.temperature;
    }

    @Override
    public final int hashCode()
    {
        int code = 1;
        code = 31*code + getIngredient().hashCode();
        code = 31*code + amount;
        code = 31*code + materialState.hashCode();
        code = 31*code + refinementLevel.hashCode();
        if (tag != null)
            code = 31*code + tag.hashCode();
        return code;
    }

    /**
     * Default equality comparison for a FluidStack. Same functionality as isFluidEqual().
     *
     * This is included for use in data structures.
     */
    @Override
    public final boolean equals(Object o)
    {
        if (!(o instanceof IngredientStack))
        {
            return false;
        }

        return isIngredientEqual((IngredientStack) o);
    }
}
