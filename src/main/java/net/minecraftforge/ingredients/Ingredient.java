package net.minecraftforge.ingredients;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

/**
 * Material equivalent to Fluids.
 * <p/>
 * Intended to aid in the quantification and qualification of what
 * otherwise are ambiguous materials.
 * */
public class Ingredient
{
    /**
     * Volume of one block represented by {@value FULL_BLOCK_VOLUME} millblocks
     * */
    public static final int FULL_BLOCK_VOLUME = 1000;

    /**
     * Volume of one ore's worth of non stone material.
     *<p/>
     * Rounded {@value #FULL_BLOCK_VOLUME} / 9 up
     * to {@value ORE_VOLUME} milliblocks.
     * */
    public static final int ORE_VOLUME = 112;

    /**
     * Volume of one ore's worth of stone/base material
     * <p/>
     * Rounded down the other component of {@link Ingredient#ORE_VOLUME}
     * to {@value ORE_VOLUME_BASE}
     * */
    public static final int ORE_VOLUME_BASE = 888;

    /**
     * Volume of one nugget's worth of material.
     * <p/>
     * Rounded {@value ORE_VOLUME} / 9 up to {@value NUGGET_VOLUME} millblocks
     * */
    public static final int NUGGET_VOLUME = 13;

    /**
     * The unique identification String for this ingredient.
     * */
    protected final String ingredientName;

    /**
     * The unlocalized name for this ingredient.
     * */
    protected String unlocalizedName;

    /**
     * Density of the ingredient in kg/m^3 at room temp (300 degrees kelvin).
     *
     * Default value is approximately the real-life density of water.
     * */
    protected int density = 1000;

    /**
     * The melting point of the ingredient in kelvin.
     *
     * Default value is approximately the real-life melting point of water.
     * */
    protected int meltingPoint = 273;

    /**
     * The boiliing point of the ingredient in kelvin.
     *
     * Default value is approximately the real-life boiling point of water.
     *  */
    protected int boilingPoint = 323;

    /**
     * The light level emitted by this ingredient.
     *
     * Default value is 0, as most ingredients do not actively emit light.
     */
    protected int luminosity = 0;

    /**
     * The rarity of the ingredient.
     *
     * Primarily for usage in tool tips
     * */
    protected EnumRarity rarity = EnumRarity.COMMON;

    /**
     * The state of the ingredient at room temp (300 degrees kelvin).
     * */
    protected EnumMatterState roomTempState;

    /**
     * True if the ingredient melt at 1 atm.
     * */
    protected boolean canMelt = true;

    /**
     * True if the ingredient can boil at 1 atm.
     * */
    protected boolean canBoil = true;

    /**
     * The pure block of the ingredient, representing 1000 milliblocks of the refined material
     * */
    protected Block block = null;

    /**
     * The correlating fluid, if the ingredient has one.
     * */
    protected Fluid fluid = null;

    protected Set<String> indentifiers = Sets.newHashSet();

    private SoundEvent addSound;
    private SoundEvent removeSound;

    public Ingredient(String name)
    {
        ingredientName = name;
        unlocalizedName = name;
    }

    /**
     * Sets the translation handle of the ingredient.
     * */
    public Ingredient setUnlocalizedName(String unlocalizedName)
    {
        this.unlocalizedName = unlocalizedName;
        return this;
    }

    /**
     * Sets the room temp (300 degrees kelvin) density of the ingredient.
     * */
    public Ingredient setDensity(int density)
    {
        this.density = density;
        return this;
    }

    /**
     * Sets the rarity for the ingredient
     *
     * Predominately used for tool tips
     * */
    public Ingredient setRarity(EnumRarity rarity)
    {
        this.rarity = rarity;
        return this;
    }

    /**
     * Sets the boiling point, and rechecks the room temp state of the ingredient.
     * 
     * @param meltingPoint
     *          The melting point of the ingredient in kelvin
     * */
    public Ingredient setMeltingPoint(int meltingPoint)
    {
        this.meltingPoint = meltingPoint;
        recheckDefaultState();
        return this;
    }

    /**
     * Sets the boiling point, and rechecks the room temp state of the ingredient.
     *
     * @param boilingPoint
     *          The boiling point of the ingredient in kelvin
     * */
    public Ingredient setBoilingPoint(int boilingPoint)
    {
        this.boilingPoint = boilingPoint;
        recheckDefaultState();
        return this;
    }

    /**
     * Sets whether or not the ingredient can melt at 1 atm
     * */
    public Ingredient setCanMelt(boolean canMelt)
    {
        this.canMelt = canMelt;
        return this;
    }

    /**
     * Sets whether or not the ingredient can boil at 1 atm
     * */
    public Ingredient setCanBoil(boolean canBoil)
    {
        this.canBoil = canBoil;
        return this;
    }

    /**
     * Sets the block that represents 1000 milliblocks of the refined ingredient, if one exists
     * <p/>
     * A good example is the iron block
     * */
    public Ingredient setBlock(Block block)
    {
        this.block = block;
        return this;
    }

    /**
     * Sets the luminosity of the ingredient.
     * */
    public Ingredient setLuminosity(int luminosity)
    {
        this.luminosity = luminosity;
        return this;
    }

    /**
     * Sets the correlating fluid for the ingredient, if one exists
     * */
    public Ingredient setFluid(Fluid fluid)
    {
        this.fluid = fluid;
        return this;
    }

    /**
     * Adds an indentifier to the ingredient. Useful for when the ingredient is being qualified.
     * <p/>
     * Use as generic of identifiers as possible. Such as 'metal' or 'mineable'
     * */
    public Ingredient addIdentifier(String... identifiers)
    {
        for(String toAdd : identifiers)
        {
            indentifiers.add(toAdd);
        }
        return this;
    }

    /* Checks the default state based on the given temp of 300 degrees kelvin*/
    private void recheckDefaultState()
    {
        if(canBoil && boilingPoint <= 300)
        {
            roomTempState = EnumMatterState.GAS;
        }
        else if(canMelt && meltingPoint <= 300)
        {
            roomTempState = EnumMatterState.LIQUID;
        }
        else
        {
            roomTempState = EnumMatterState.SOLID;
        }
    }

    public final String getName()
    {
        return this.ingredientName;
    }

    public EnumMatterState getRoomTempState()
    {
        return roomTempState;
    }

    public String getUnlocalizedName()
    {
        return unlocalizedName;
    }

    public EnumRarity getRarity()
    {
        return rarity;
    }

    public int getDensity()
    {
        return density;
    }

    public int getMeltingPoint()
    {
        return meltingPoint;
    }

    public int getBoilingPoint()
    {
        return boilingPoint;
    }

    public int getLuminosity()
    {
        return luminosity;
    }

    public Block getBlock()
    {
        return block;
    }

    public boolean canMelt()
    {
        return canMelt;
    }

    public boolean canBoil()
    {
        return canBoil;
    }

    public SoundEvent getAddSound()
    {
        return addSound;
    }

    public SoundEvent getRemoveSound()
    {
        return removeSound;
    }

    public Fluid getFluid()
    {
        return fluid;
    }

    public FluidStack getFluidStack()
    {
        return new FluidStack(fluid, Fluid.BUCKET_VOLUME);
    }

    public final boolean canBePlacedInWorld()
    {
        return block != null;
    }

    /**
     * Returns whether or not this ingredient has the given identifier
     * <p/>
     * This is useful for qualifying an item, eg. when checking if it is mineable, or a metal
     * */
    public final boolean hasIdentifier(String identifier)
    {
        return indentifiers.contains(identifier);
    }

    /* Stack based accessors */

    /**
     * Returns the localized Ingredient name for the given stack.
     * */
    public String getLocalizedName(IngredientStack stack)
    {
        String s = this.getUnlocalizedName(stack);
        return s == null ? "" : I18n.translateToLocal(s);
    }

    public String getUnlocalizedName(IngredientStack stack)
    {
        return "ingredient." + this.unlocalizedName;
    }

    /**
     * Returns the sound to be played when the source is pulled from a container
     * */
    public SoundEvent getAddedSound(IngredientStack stack)
    {
        return getRemoveSound();
    }

    /**
     * Returns the sound to be played when the source is added into a container
     * */
    public SoundEvent getRemovedSound(IngredientStack stack)
    {
        return getAddSound();
    }

    public EnumRarity getRarity(IngredientStack stack)
    {
        return getRarity();
    }

    public int getDensity(IngredientStack stack)
    {
        return getDensity();
    }

    public int getMeltingPoint(IngredientStack stack)
    {
        return getMeltingPoint();
    }

    public int getBoilingPoint(IngredientStack stack)
    {
        return getBoilingPoint();
    }

    public int getLuminosity(IngredientStack stack)
    {
        return getLuminosity();
    }

    public Block getBlock(IngredientStack stack)
    {
        return getBlock();
    }

    public boolean canMelt(IngredientStack stack)
    {
        return canMelt();
    }

    public boolean canBoil(IngredientStack stack)
    {
        return canBoil();
    }

    public SoundEvent getAddSound(IngredientStack stack)
    {
        return getAddSound();
    }

    public SoundEvent getRemoveSound(IngredientStack stack)
    {
        return getRemoveSound();
    }

    public Fluid getFluid(IngredientStack stack)
    {
        return getFluid();
    }

    public FluidStack getFluidStack(IngredientStack stack)
    {
        return new FluidStack(getFluidStack(), stack.amount);
    }

    /**
     * Returns the sound to be played when the source is pulled from a container
     * */
    public SoundEvent getAddedSound(World world, BlockPos pos)
    {
        return getRemoveSound();
    }

    /**
     * Returns the sound to be played when the source is added into a container
     * */
    public SoundEvent getRemovedSound(World world, BlockPos pos)
    {
        return getAddSound();
    }

    public EnumRarity getRarity(World world, BlockPos pos)
    {
        return getRarity();
    }

    public int getDensity(World world, BlockPos pos)
    {
        return getDensity();
    }

    public int getMeltingPoint(World world, BlockPos pos)
    {
        return getMeltingPoint();
    }

    public int getBoilingPoint(World world, BlockPos pos)
    {
        return getBoilingPoint();
    }

    public int getLuminosity(World world, BlockPos pos)
    {
        return getLuminosity();
    }

    public boolean canMelt(World world, BlockPos pos)
    {
        return canMelt();
    }

    public boolean canBoil(World world, BlockPos pos)
    {
        return canBoil();
    }

    public SoundEvent getAddSound(World world, BlockPos pos)
    {
        return getAddSound();
    }

    public SoundEvent getRemoveSound(World world, BlockPos pos)
    {
        return getRemoveSound();
    }

    public FluidStack getFluidStack(World world, BlockPos pos)
    {
        return new FluidStack(getFluidStack(), FULL_BLOCK_VOLUME);
    }
}
