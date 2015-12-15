package net.minecraftforge.fluids;

import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraft.item.EnumRarity;

/**
 * Minecraft Forge Fluid Implementation
 *
 * This class is a fluid (liquid or gas) equivalent to "Item." It describes the nature of a fluid
 * and contains its general properties.
 *
 * These properties do not have inherent gameplay mechanics - they are provided so that mods may
 * choose to take advantage of them.
 *
 * Fluid implementations are not required to actively use these properties, nor are objects
 * interfacing with fluids required to make use of them, but it is encouraged.
 *
 * The default values can be used as a reference point for mods adding fluids such as oil or heavy
 * water.
 *
 * @author King Lemming
 *
 */
public class Fluid
{
    /** The unique identification name for this fluid. */
    protected final String fluidName;

    /** The unlocalized name of this fluid. */
    protected String unlocalizedName;

    protected final ResourceLocation still;
    protected final ResourceLocation flowing;

    /**
     * The light level emitted by this fluid.
     *
     * Default value is 0, as most fluids do not actively emit light.
     */
    protected int luminosity = 0;

    /**
     * Density of the fluid - completely arbitrary; negative density indicates that the fluid is
     * lighter than air.
     *
     * Default value is approximately the real-life density of water in kg/m^3.
     */
    protected int density = 1000;

    /**
     * Temperature of the fluid - completely arbitrary; higher temperature indicates that the fluid is
     * hotter than air.
     *
     * Default value is approximately the real-life room temperature of water in degrees Kelvin.
     */
    protected int temperature = 300;

    /**
     * Viscosity ("thickness") of the fluid - completely arbitrary; negative values are not
     * permissible.
     *
     * Default value is approximately the real-life density of water in m/s^2 (x10^-3).
     *
     * Higher viscosity means that a fluid flows more slowly, like molasses.
     * Lower viscosity means that a fluid flows more quickly, like helium.
     *
     */
    protected int viscosity = 1000;

    /**
     * This indicates if the fluid is gaseous.
     *
     * Useful for rendering the fluid in containers and the world.
     *
     * Generally this is associated with negative density fluids.
     */
    protected boolean isGaseous;

    /**
     * The rarity of the fluid.
     *
     * Used primarily in tool tips.
     */
    protected EnumRarity rarity = EnumRarity.COMMON;

    /**
     * If there is a Block implementation of the Fluid, the Block is linked here.
     *
     * The default value of null should remain for any Fluid without a Block implementation.
     */
    protected Block block = null;

    public Fluid(String fluidName, ResourceLocation still, ResourceLocation flowing)
    {
        this.fluidName = fluidName.toLowerCase(Locale.ENGLISH);
        this.unlocalizedName = fluidName;
        this.still = still;
        this.flowing = flowing;
    }

    public Fluid setUnlocalizedName(String unlocalizedName)
    {
        this.unlocalizedName = unlocalizedName;
        return this;
    }

    public Fluid setBlock(Block block)
    {
        if (this.block == null || this.block == block)
        {
            this.block = block;
        }
        else
        {
            FMLLog.warning("A mod has attempted to assign Block " + block + " to the Fluid '" + fluidName + "' but this Fluid has already been linked to the Block "
                    + this.block + ". You may have duplicate Fluid Blocks as a result. It *may* be possible to configure your mods to avoid this.");
        }
        return this;
    }

    public Fluid setLuminosity(int luminosity)
    {
        this.luminosity = luminosity;
        return this;
    }

    public Fluid setDensity(int density)
    {
        this.density = density;
        return this;
    }

    public Fluid setTemperature(int temperature)
    {
        this.temperature = temperature;
        return this;
    }

    public Fluid setViscosity(int viscosity)
    {
        this.viscosity = viscosity;
        return this;
    }

    public Fluid setGaseous(boolean isGaseous)
    {
        this.isGaseous = isGaseous;
        return this;
    }

    public Fluid setRarity(EnumRarity rarity)
    {
        this.rarity = rarity;
        return this;
    }

    public final String getName()
    {
        return this.fluidName;
    }

    @Deprecated // Modders should never actually use int ID, use String
    public final int getID()
    {
        return FluidRegistry.getFluidID(this.fluidName);
    }

    public final Block getBlock()
    {
        return block;
    }

    public final boolean canBePlacedInWorld()
    {
        return block != null;
    }

    /**
     * Returns the localized name of this fluid.
     */
    public String getLocalizedName(FluidStack stack)
    {
        String s = this.getUnlocalizedName();
        return s == null ? "" : StatCollector.translateToLocal(s);
    }

    /**
     * A FluidStack sensitive version of getUnlocalizedName
     */
    public String getUnlocalizedName(FluidStack stack)
    {
        return this.getUnlocalizedName();
    }

    /**
     * Returns the unlocalized name of this fluid.
     */
    public String getUnlocalizedName()
    {
        return "fluid." + this.unlocalizedName;
    }

    /* Default Accessors */
    public final int getLuminosity()
    {
        return this.luminosity;
    }

    public final int getDensity()
    {
        return this.density;
    }

    public final int getTemperature()
    {
        return this.temperature;
    }

    public final int getViscosity()
    {
        return this.viscosity;
    }

    public final boolean isGaseous()
    {
        return this.isGaseous;
    }

    public EnumRarity getRarity()
    {
        return rarity;
    }

    public int getColor()
    {
        return 0xFFFFFFFF;
    }

    public ResourceLocation getStill()
    {
        return still;
    }

    public ResourceLocation getFlowing()
    {
        return flowing;
    }

    /* Stack-based Accessors */
    public int getLuminosity(FluidStack stack){ return getLuminosity(); }
    public int getDensity(FluidStack stack){ return getDensity(); }
    public int getTemperature(FluidStack stack){ return getTemperature(); }
    public int getViscosity(FluidStack stack){ return getViscosity(); }
    public boolean isGaseous(FluidStack stack){ return isGaseous(); }
    public EnumRarity getRarity(FluidStack stack){ return getRarity(); }
    public int getColor(FluidStack stack){ return getColor(); }
    public ResourceLocation getStill(FluidStack stack) { return getStill(); }
    public ResourceLocation getFlowing(FluidStack stack) { return getFlowing(); }

    /* World-based Accessors */
    public int getLuminosity(World world, BlockPos pos){ return getLuminosity(); }
    public int getDensity(World world, BlockPos pos){ return getDensity(); }
    public int getTemperature(World world, BlockPos pos){ return getTemperature(); }
    public int getViscosity(World world, BlockPos pos){ return getViscosity(); }
    public boolean isGaseous(World world, BlockPos pos){ return isGaseous(); }
    public EnumRarity getRarity(World world, BlockPos pos){ return getRarity(); }
    public int getColor(World world, BlockPos pos){ return getColor(); }
    public ResourceLocation getStill(World world, BlockPos pos) { return getStill(); }
    public ResourceLocation getFlowing(World world, BlockPos pos) { return getFlowing(); }

}
