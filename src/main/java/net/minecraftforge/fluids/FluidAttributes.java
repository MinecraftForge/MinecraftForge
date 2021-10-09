/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.Util;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.WaterWalkerEnchantment;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.IModelConfiguration;

import javax.annotation.Nullable;
import java.util.function.*;
import java.util.stream.Stream;

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

    private final String translationKey;

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;

    /**
     * A texture grabbed directly from the Texture Atlas.
     * Used to render as an overlay when a fluid is touching another "non-solid" or "transparent" block.
     * This is what renders for example the "Water Texture" when you place a piece of glass next to water.
     */
    @Nullable
    private final ResourceLocation overlayTexture;

    /**
     * A texture grabbed through the TextureManager.
     * Used to render as an overlay on all blocks on the screen when a player is submerged in a fluid in first-person.
     * This is what applies the "water" texture-esq shadow when submerged in water.
     */
    @Nullable
    private final ResourceLocation viewOverlayTexture;

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

    /**
     * The scaled motion {@link ToDoubleBiFunction} of the fluid when "pushing" entities.
     */
    private final ToDoubleBiFunction<FluidState, Entity> motionScale;
    
    /**
     * The fall distance multiplier {@link ToDoubleBiFunction} while within a fluid.
     * For example used if it should increase/dampen fall damage.
     */
    private final ToDoubleBiFunction<FluidState, Entity> fallDistanceModifier;
    
    /**
     * The {@link Predicate} used to check if an entity can swim in the {@link Fluid}.
     */
    private final Predicate<FluidState> canSwim;

    /**
     * The {@link BiPredicate} used to determine if a {@link LivingEntity} can drown in the {@link Fluid}.
     */
    private final BiPredicate<FluidState, LivingEntity> canDrown;
    
    /**
     * The {@link BiPredicate} used to determine if the {@link Fluid} should extinguish a burning entity.
     */
    private final BiPredicate<FluidState, Entity> canExtinguish;
    
    /**
     * The {@link Predicate} used to determine if the {@link Fluid} can hydrate a specific {@link BlockState}.
     * Used for example with {@link net.minecraft.world.level.block.ConcretePowderBlock#canSolidify(BlockState)} (BlockState)}, {@link net.minecraft.world.level.block.SpongeBlock#tryAbsorbWater(Level, BlockPos)} and {@link net.minecraft.world.level.block.FarmBlock#isNearWater(LevelReader, BlockPos)}.
     */
    private final BiPredicate<FluidState, BlockState> canHydrate;

    /**
     * Used to decide if a {@link Boat} should have intended interaction behaviour with a fluid.
     */
    private final BiPredicate<FluidState, Boat> canBoat;

    protected FluidAttributes(Builder builder, Fluid fluid)
    {
        this.translationKey = builder.translationKey != null ? builder.translationKey :  Util.makeDescriptionId("fluid", fluid.getRegistryName());
        this.stillTexture = builder.stillTexture;
        this.flowingTexture = builder.flowingTexture;
        this.overlayTexture = builder.overlayTexture;
        this.viewOverlayTexture = builder.viewOverlayTexture;
        this.color = builder.color;
        this.fillSound = builder.fillSound;
        this.emptySound = builder.emptySound;
        this.luminosity = builder.luminosity;
        this.temperature = builder.temperature;
        this.viscosity = builder.viscosity;
        this.density = builder.density;
        this.rarity = builder.rarity;
        this.motionScale = builder.motionScale;
        this.fallDistanceModifier = builder.fallDistanceModifier;
        this.canSwim = builder.canSwim;
        this.canDrown = builder.canDrown;
        this.canExtinguish = builder.canExtinguish;
        this.canHydrate = builder.canHydrate;
        this.canBoat = builder.canBoat;
    }

    public ItemStack getBucket(FluidStack stack)
    {
        return new ItemStack(stack.getFluid().getBucket());
    }

    public BlockState getBlock(BlockAndTintGetter reader, BlockPos pos, FluidState state)
    {
        return state.createLegacyBlock();
    }

    public FluidState getStateForPlacement(BlockAndTintGetter reader, BlockPos pos, FluidStack state)
    {
        return state.getFluid().defaultFluidState();
    }

    public final boolean canBePlacedInLevel(BlockAndTintGetter reader, BlockPos pos, FluidState state)
    {
        return !getBlock(reader, pos, state).isAir();
    }

    public final boolean canBePlacedInLevel(BlockAndTintGetter reader, BlockPos pos, FluidStack state)
    {
        return !getBlock(reader, pos, getStateForPlacement(reader, pos, state)).isAir();
    }

    /**
     * Determines if the fluid is lighter than "air" based of the fluids density value.
     * If the density value is lower than or equal to 0 where 0 is the "canonical" density of air.
     * Then the fluid will be using the upside-down bucket texture/model, as well as the upward-flowing fluid block model.
     * @return Returns whether the fluid is lighter than air, making it use the upside down bucket texture and fluid block model.
     */
    public final boolean isLighterThanAir()
    {
        return this.getDensity() <= 0;
    }

    /**
     * Determines if this fluid should vaporize in dimensions where water vaporizes when placed.
     * To preserve the intentions of vanilla, fluids that can turn lava into obsidian should vaporize.
     * This prevents players from making the nether safe with a single bucket.
     * Based on {@link net.minecraft.world.item.BucketItem#emptyContents(Player, Level, BlockPos, BlockHitResult)}
     *
     * @param fluidStack The fluidStack is trying to be placed.
     * @return true if this fluid should vaporize in dimensions where water vaporizes when placed.
     */
    public boolean doesVaporize(BlockAndTintGetter reader, BlockPos pos, FluidStack fluidStack)
    {
        BlockState blockstate = getBlock(reader, pos, getStateForPlacement(reader, pos, fluidStack));
        if (blockstate == null)
            return false;
        return blockstate.getMaterial() == net.minecraft.world.level.material.Material.WATER;
    }

    /**
     * Called instead of placing the fluid block if {@link net.minecraft.world.level.dimension.DimensionType#ultraWarm()} and {@link #doesVaporize(BlockAndTintGetter, BlockPos, FluidStack)} are true.
     * Override this to make your explosive liquid blow up instead of the default smoke, etc.
     * Based on {@link net.minecraft.world.item.BucketItem#emptyContents(Player, Level, BlockPos, BlockHitResult)}
     *
     * @param player     Player who tried to place the fluid. May be null for blocks like dispensers.
     * @param level      Level to vaporize the fluid in.
     * @param pos        The position in the level the fluid block was going to be placed.
     * @param fluidStack The fluidStack that was going to be placed.
     */
    public void vaporize(@Nullable Player player, Level level, BlockPos pos, FluidStack fluidStack)
    {
        level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

        for (int l = 0; l < 8; ++l)
        {
            level.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, (double) pos.getX() + Math.random(), (double) pos.getY() + Math.random(), (double) pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Returns the localized name of the provided {@code FluidStack}.
     * @param stack the provided {@code FluidStack} to get the display name of.
     * @return Returns the display name of the provided {@code FluidStack} as a {@code Component}.
     */
    public Component getDisplayName(FluidStack stack)
    {
        return new TranslatableComponent(getTranslationKey(stack));
    }

    /**
     * A FluidStack sensitive version of getTranslationKey
     * @param stack the provided {@code FluidStack} to get the translation key of.
     * @return Returns the translation key of the provided {@code FluidStack} as a string.
     */
    public String getTranslationKey(FluidStack stack)
    {
        return this.getTranslationKey();
    }

    /**
     * @return Returns the translation key of this fluid.
     */
    @Deprecated(forRemoval = true, since = "1.18")
    public String getTranslationKey()
    {
        return this.translationKey;
    }

    /* Default Accessors */

    /**
     * Luminosity is currently only used as part of {@link net.minecraftforge.client.model.DynamicBucketModel#bake(IModelConfiguration, ModelBakery, Function, ModelState, ItemOverrides, ResourceLocation)} to determine if the fluid should render fullbright in the bucket texture or not.
     * The luminosity check for fullbright on the DynamicBucketModel is calculated as 'luminosity > 0' and requires the "applyFluidLuminosity" model attribute to be present and enabled in the fluid model file.
     * @return Returns the luminosity value for the fluid.
     */
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

    public Rarity getRarity()
    {
        return rarity;
    }

    public int getColor()
    {
        return color;
    }

    /**
     * Gets the scaled motion of a fluid when "pushing" entities.
     *
     * @param state the fluid pushing the entity
     * @param entity the entity being pushed
     * @return the scaled motion of the fluid when "pushing" entities
     */
    public double getMotionScale(FluidState state, Entity entity)
    {
        return motionScale.applyAsDouble(state, entity);
    }

    /**
     * Gets how much the fall distance should be scaled per tick while an entity
     * falls within a fluid.
     *
     * @param state the fluid the entity is falling through
     * @param entity the falling entity
     * @return the fall distance multiplier
     */
    public double getFallDistanceModifier(FluidState state, Entity entity)
    {
        return fallDistanceModifier.applyAsDouble(state, entity);
    }

    /**
     * Gets whether an entity can swim within a fluid.
     *
     * @param state the fluid the entity is swimming through
     * @return {@code true} if the entity can swim, {@code false} otherwise
     */
    public boolean canSwim(FluidState state)
    {
        return canSwim.test(state);
    }

    /**
     * Gets whether an entity can drown within a fluid.
     *
     * @param state the fluid the entity is in
     * @param entity the potentially drowning entity
     * @return {@code true} if the entity can drown, {@code false} otherwise
     */
    public boolean canDrown(FluidState state, LivingEntity entity)
    {
        return canDrown.test(state, entity);
    }

    /**
     * Gets whether a fluid can extinguish a burning entity.
     *
     * @param state the fluid the entity is in
     * @param entity the burning entity
     * @return {@code true} if the entity can be extinguished, {@code false} otherwise
     */
    public boolean canExtinguish(FluidState state, Entity entity)
    {
        return canExtinguish.test(state, entity);
    }

    /**
     * Gets whether a fluid can hydrate a specific block.
     *
     * @param fluidState the fluid hydrating the block
     * @param blockState the potentially hydrated block
     * @return {@code true} if the fluid can hydrate a block, {@code false} otherwise
     */
    public boolean canHydrate(FluidState fluidState, BlockState blockState)
    {
        return canHydrate.test(fluidState, blockState);
    }

    /**
     * Gets whether a boat can interact as intended with a fluid.
     *
     * @param state the fluid that the boat is interacting with
     * @param boat the boat interacting with the fluid
     * @return {@code true} if the boat can interact with the fluid, {@code false} otherwise
     */
    public boolean canBoat(FluidState state, Boat boat)
    {
        return this.canBoat.test(state, boat);
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

    @Nullable
    public ResourceLocation getViewOverlayTexture()
    {
        return viewOverlayTexture;
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
    public Rarity getRarity(FluidStack stack){ return getRarity(); }
    public int getColor(FluidStack stack){ return getColor(); }
    public ResourceLocation getStillTexture(FluidStack stack) { return getStillTexture(); }
    public ResourceLocation getFlowingTexture(FluidStack stack) { return getFlowingTexture(); }
    public SoundEvent getFillSound(FluidStack stack) { return getFillSound(); }
    public SoundEvent getEmptySound(FluidStack stack) { return getEmptySound(); }

    /* Level-based Accessors */
    public int getLuminosity(BlockAndTintGetter level, BlockPos pos){ return getLuminosity(); }
    public int getDensity(BlockAndTintGetter level, BlockPos pos){ return getDensity(); }
    public int getTemperature(BlockAndTintGetter level, BlockPos pos){ return getTemperature(); }
    public int getViscosity(BlockAndTintGetter level, BlockPos pos){ return getViscosity(); }
    public Rarity getRarity(BlockAndTintGetter level, BlockPos pos){ return getRarity(); }
    public int getColor(BlockAndTintGetter level, BlockPos pos){ return getColor(); }
    public ResourceLocation getStillTexture(BlockAndTintGetter level, BlockPos pos) { return getStillTexture(); }
    public ResourceLocation getFlowingTexture(BlockAndTintGetter level, BlockPos pos) { return getFlowingTexture(); }
    public SoundEvent getFillSound(BlockAndTintGetter level, BlockPos pos) { return getFillSound(); }
    public SoundEvent getEmptySound(BlockAndTintGetter level, BlockPos pos) { return getEmptySound(); }
    public boolean canSwim(BlockAndTintGetter level, BlockPos pos) { return canSwim(level.getFluidState(pos)); }
    public boolean canHydrate(BlockAndTintGetter level, BlockPos pos) { return canHydrate(level.getFluidState(pos), level.getBlockState(pos)); }

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
        private final BiFunction<Builder,Fluid,FluidAttributes> factory;
        private ResourceLocation overlayTexture;
        private ResourceLocation viewOverlayTexture;
        private int color = 0xFFFFFFFF;
        private String translationKey;
        private SoundEvent fillSound;
        private SoundEvent emptySound;
        private int luminosity = 0;
        private int density = 1000;
        private int temperature = 300;
        private int viscosity = 1000;
        private Rarity rarity = Rarity.COMMON;
        private ToDoubleBiFunction<FluidState, Entity> motionScale = (state, entity) -> 0.014D;
        private ToDoubleBiFunction<FluidState, Entity> fallDistanceModifier = (state, entity) -> 0.0D;
        private Predicate<FluidState> canSwim = state -> state.is(FluidTags.WATER);
        private BiPredicate<FluidState, LivingEntity> canDrown = (state, entity) -> state.is(FluidTags.WATER);
        private BiPredicate<FluidState, Entity> canExtinguish = (state, entity) -> state.is(FluidTags.WATER);
        private BiPredicate<FluidState, BlockState> canHydrate = (fluidState, blockState) -> fluidState.is(FluidTags.WATER);
        private BiPredicate<FluidState, Boat> canBoat = (state, boat) -> state.is(FluidTags.WATER);

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
            return viewOverlay(texture);
        }

        public final Builder viewOverlay(ResourceLocation texture)
        {
            viewOverlayTexture = new ResourceLocation(texture.getNamespace(), "textures/" + texture.getPath() + ".png");
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

        /**
         * Sets the scaled motion of the fluid when "pushing" entities.
         *
         * @param motionScale a function representing the scaled motion of the fluid
         * @return the builder instance
         */
        public final Builder motionScale(ToDoubleBiFunction<FluidState, Entity> motionScale)
        {
            this.motionScale = motionScale;
            return this;
        }

        /**
         * Sets how much the fall distance should be scaled per tick while an entity
         * falls within the fluid.
         *
         * @param fallDistanceModifier a function representing the scaled fall distance
         * @return the builder instance
         */
        public final Builder fallDistanceModifier(ToDoubleBiFunction<FluidState, Entity> fallDistanceModifier)
        {
            this.fallDistanceModifier = fallDistanceModifier;
            return this;
        }

        /**
         * Sets whether an entity can swim within the fluid.
         *
         * @param canSwim a predicate representing whether an entity can swim in the fluid
         * @return the builder instance
         */
        public final Builder canSwim(Predicate<FluidState> canSwim)
        {
            this.canSwim = canSwim;
            return this;
        }

        /**
         * Sets whether an entity can drown within the fluid.
         *
         * @param canDrown a predicate representing whether an entity can drown in the fluid
         * @return the builder instance
         */
        public final Builder canDrown(BiPredicate<FluidState, LivingEntity> canDrown)
        {
            this.canDrown = canDrown;
            return this;
        }

        /**
         * Sets whether the fluid can extinguish a burning entity.
         *
         * @param canExtinguish a predicate representing whether an entity can be extinguished by the fluid
         * @return the builder instance
         */
        public final Builder canExtinguish(BiPredicate<FluidState, Entity> canExtinguish)
        {
            this.canExtinguish = canExtinguish;
            return this;
        }

        /**
         * Sets whether the fluid can hydrate a specific block.
         *
         * @param canHydrate a predicate representing if a block can be hydrated by the fluid
         * @return the builder instance
         */
        public final Builder canHydrate(BiPredicate<FluidState, BlockState> canHydrate)
        {
            this.canHydrate = canHydrate;
            return this;
        }

        /**
         * Sets whether a boat can interact as intended with the fluid.
         *
         * @param canBoat a predicate representing if a boat can interact with the fluid
         * @return the builder instance
         */
        public final Builder canBoat(BiPredicate<FluidState, Boat> canBoat)
        {
            this.canBoat = canBoat;
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
        public int getColor(BlockAndTintGetter level, BlockPos pos)
        {
            return BiomeColors.getAverageWaterColor(level, pos) | 0xFF000000;
        }

        public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            return new Builder(stillTexture, flowingTexture, Water::new);
        }
    }

}
