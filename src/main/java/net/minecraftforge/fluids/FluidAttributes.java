/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fluids;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockDisplayReader;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.Rarity;
import net.minecraft.world.biome.BiomeColors;

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
 */
public class FluidAttributes
{
    public static final int BUCKET_VOLUME = 1000;

    private String translationKey;

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;

    @Nullable
    private final ResourceLocation overlayTexture;

    private final SoundEvent fillSound;
    private final SoundEvent emptySound;

    /**
     * The light level emitted by this fluid.
     *
     * Default value is 0, as most fluids do not actively emit light.
     */
    private final int luminosity;

    /**
     * Density of the fluid - completely arbitrary; negative density indicates that the fluid is
     * lighter than air.
     *
     * Default value is approximately the real-life density of water in kg/m^3.
     */
    private final int density;

    /**
     * Temperature of the fluid - completely arbitrary; higher temperature indicates that the fluid is
     * hotter than air.
     *
     * Default value is approximately the real-life room temperature of water in degrees Kelvin.
     */
    private final int temperature;

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
    private final int viscosity;

    /**
     * This indicates if the fluid is gaseous.
     *
     * Generally this is associated with negative density fluids.
     */
    private final boolean isGaseous;

    /**
     * The rarity of the fluid.
     *
     * Used primarily in tool tips.
     */
    private final Rarity rarity;

    /**
     * Color used by universal bucket and the ModelFluid baked model.
     * Note that this int includes the alpha so converting this to RGB with alpha would be
     *   float r = ((color >> 16) & 0xFF) / 255f; // red
     *   float g = ((color >> 8) & 0xFF) / 255f; // green
     *   float b = ((color >> 0) & 0xFF) / 255f; // blue
     *   float a = ((color >> 24) & 0xFF) / 255f; // alpha
     */
    private final int color;

    protected FluidAttributes(Builder builder, Fluid fluid)
    {
        this.translationKey = builder.translationKey != null ? builder.translationKey :  Util.makeTranslationKey("fluid", fluid.getRegistryName());
        this.stillTexture = builder.stillTexture;
        this.flowingTexture = builder.flowingTexture;
        this.overlayTexture = builder.overlayTexture;
        this.color = builder.color;
        this.fillSound = builder.fillSound;
        this.emptySound = builder.emptySound;
        this.luminosity = builder.luminosity;
        this.temperature = builder.temperature;
        this.viscosity = builder.viscosity;
        this.density = builder.density;
        this.isGaseous = builder.isGaseous;
        this.rarity = builder.rarity;
    }

    public ItemStack getBucket(FluidStack stack)
    {
        return new ItemStack(stack.getFluid().getFilledBucket());
    }

    public BlockState getBlock(IBlockDisplayReader reader, BlockPos pos, FluidState state)
    {
        return state.getBlockState();
    }

    public FluidState getStateForPlacement(IBlockDisplayReader reader, BlockPos pos, FluidStack state)
    {
        return state.getFluid().getDefaultState();
    }

    public final boolean canBePlacedInWorld(IBlockDisplayReader reader, BlockPos pos, FluidState state)
    {
        return !getBlock(reader, pos, state).isAir(reader, pos);
    }

    public final boolean canBePlacedInWorld(IBlockDisplayReader reader, BlockPos pos, FluidStack state)
    {
        return !getBlock(reader, pos, getStateForPlacement(reader, pos, state)).isAir(reader, pos);
    }

    public final boolean isLighterThanAir()
    {
        return this.density <= 0;
    }

    /**
     * Determines if this fluid should vaporize in dimensions where water vaporizes when placed.
     * To preserve the intentions of vanilla, fluids that can turn lava into obsidian should vaporize.
     * This prevents players from making the nether safe with a single bucket.
     * Based on {@link net.minecraft.item.BucketItem#tryPlaceContainedLiquid(PlayerEntity, World, BlockPos)}
     *
     * @param fluidStack The fluidStack is trying to be placed.
     * @return true if this fluid should vaporize in dimensions where water vaporizes when placed.
     */
    public boolean doesVaporize(IBlockDisplayReader reader, BlockPos pos, FluidStack fluidStack)
    {
        BlockState blockstate = getBlock(reader, pos, getStateForPlacement(reader, pos, fluidStack));
        if (blockstate == null)
            return false;
        return blockstate.getMaterial() == net.minecraft.block.material.Material.WATER;
    }

    /**
     * Called instead of placing the fluid block if {@link net.minecraft.world.dimension.Dimension#doesWaterVaporize()} and {@link #doesVaporize(FluidStack)} are true.
     * Override this to make your explosive liquid blow up instead of the default smoke, etc.
     * Based on {@link net.minecraft.item.BucketItem#tryPlaceContainedLiquid(PlayerEntity, World, BlockPos)}
     *
     * @param player     Player who tried to place the fluid. May be null for blocks like dispensers.
     * @param worldIn    World to vaporize the fluid in.
     * @param pos        The position in the world the fluid block was going to be placed.
     * @param fluidStack The fluidStack that was going to be placed.
     */
    public void vaporize(@Nullable PlayerEntity player, World worldIn, BlockPos pos, FluidStack fluidStack)
    {
        worldIn.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

        for (int l = 0; l < 8; ++l)
        {
            worldIn.addOptionalParticle(ParticleTypes.LARGE_SMOKE, (double) pos.getX() + Math.random(), (double) pos.getY() + Math.random(), (double) pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Returns the localized name of this fluid.
     */
    public ITextComponent getDisplayName(FluidStack stack)
    {
        return new TranslationTextComponent(getTranslationKey());
    }

    /**
     * A FluidStack sensitive version of getTranslationKey
     */
    public String getTranslationKey(FluidStack stack)
    {
        return this.getTranslationKey();
    }

    /**
     * Returns the translation key of this fluid.
     */
    public String getTranslationKey()
    {
        return this.translationKey;
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

    public Rarity getRarity()
    {
        return rarity;
    }

    public int getColor()
    {
        return color;
    }

    public ResourceLocation getStillTexture()
    {
        return stillTexture;
    }

    public ResourceLocation getFlowingTexture()
    {
        return flowingTexture;
    }

    @Nullable
    public ResourceLocation getOverlayTexture()
    {
        return overlayTexture;
    }

    public SoundEvent getFillSound()
    {
        return fillSound;
    }

    public SoundEvent getEmptySound()
    {
        return emptySound;
    }

    /* Stack-based Accessors */
    public int getLuminosity(FluidStack stack){ return getLuminosity(); }
    public int getDensity(FluidStack stack){ return getDensity(); }
    public int getTemperature(FluidStack stack){ return getTemperature(); }
    public int getViscosity(FluidStack stack){ return getViscosity(); }
    public boolean isGaseous(FluidStack stack){ return isGaseous(); }
    public Rarity getRarity(FluidStack stack){ return getRarity(); }
    public int getColor(FluidStack stack){ return getColor(); }
    public ResourceLocation getStillTexture(FluidStack stack) { return getStillTexture(); }
    public ResourceLocation getFlowingTexture(FluidStack stack) { return getFlowingTexture(); }
    public SoundEvent getFillSound(FluidStack stack) { return getFillSound(); }
    public SoundEvent getEmptySound(FluidStack stack) { return getEmptySound(); }

    /* World-based Accessors */
    public int getLuminosity(IBlockDisplayReader world, BlockPos pos){ return getLuminosity(); }
    public int getDensity(IBlockDisplayReader world, BlockPos pos){ return getDensity(); }
    public int getTemperature(IBlockDisplayReader world, BlockPos pos){ return getTemperature(); }
    public int getViscosity(IBlockDisplayReader world, BlockPos pos){ return getViscosity(); }
    public boolean isGaseous(IBlockDisplayReader world, BlockPos pos){ return isGaseous(); }
    public Rarity getRarity(IBlockDisplayReader world, BlockPos pos){ return getRarity(); }
    public int getColor(IBlockDisplayReader world, BlockPos pos){ return getColor(); }
    public ResourceLocation getStillTexture(IBlockDisplayReader world, BlockPos pos) { return getStillTexture(); }
    public ResourceLocation getFlowingTexture(IBlockDisplayReader world, BlockPos pos) { return getFlowingTexture(); }
    public SoundEvent getFillSound(IBlockDisplayReader world, BlockPos pos) { return getFillSound(); }
    public SoundEvent getEmptySound(IBlockDisplayReader world, BlockPos pos) { return getEmptySound(); }

    public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        return new Builder(stillTexture, flowingTexture, FluidAttributes::new);
    }

    public Stream<ResourceLocation> getTextures()
    {
        if (overlayTexture != null)
            return Stream.of(stillTexture, flowingTexture, overlayTexture);
        return Stream.of(stillTexture, flowingTexture);
    }

    public static class Builder
    {
        private final ResourceLocation stillTexture;
        private final ResourceLocation flowingTexture;
        private ResourceLocation overlayTexture;
        private int color = 0xFFFFFFFF;
        private String translationKey;
        private SoundEvent fillSound;
        private SoundEvent emptySound;
        private int luminosity = 0;
        private int density = 1000;
        private int temperature = 300;
        private int viscosity = 1000;
        private boolean isGaseous;
        private Rarity rarity = Rarity.COMMON;
        private BiFunction<Builder,Fluid,FluidAttributes> factory;

        protected Builder(ResourceLocation stillTexture, ResourceLocation flowingTexture, BiFunction<Builder,Fluid,FluidAttributes> factory) {
            this.factory = factory;
            this.stillTexture = stillTexture;
            this.flowingTexture = flowingTexture;
        }

        public final Builder translationKey(String translationKey)
        {
            this.translationKey = translationKey;
            return this;
        }

        public final Builder color(int color)
        {
            this.color = color;
            return this;
        }

        public final Builder overlay(ResourceLocation texture)
        {
            overlayTexture = texture;
            return this;
        }

        public final Builder luminosity(int luminosity)
        {
            this.luminosity = luminosity;
            return this;
        }

        public final Builder density(int density)
        {
            this.density = density;
            return this;
        }

        public final Builder temperature(int temperature)
        {
            this.temperature = temperature;
            return this;
        }

        public final Builder viscosity(int viscosity)
        {
            this.viscosity = viscosity;
            return this;
        }

        public final Builder gaseous()
        {
            isGaseous = true;
            return this;
        }

        public final Builder rarity(Rarity rarity)
        {
            this.rarity = rarity;
            return this;
        }

        public final Builder sound(SoundEvent sound)
        {
            this.fillSound = this.emptySound = sound;
            return this;
        }

        public final Builder sound(SoundEvent fillSound, SoundEvent emptySound)
        {
            this.fillSound = fillSound;
            this.emptySound = emptySound;
            return this;
        }

        public FluidAttributes build(Fluid fluid)
        {
            return factory.apply(this, fluid);
        }
    }

    public static class Water extends FluidAttributes
    {
        protected Water(Builder builder, Fluid fluid)
        {
            super(builder, fluid);
        }

        @Override
        public int getColor(IBlockDisplayReader world, BlockPos pos)
        {
            return BiomeColors.getWaterColor(world, pos) | 0xFF000000;
        }

        public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            return new Builder(stillTexture, flowingTexture, Water::new);
        }
    }
}
