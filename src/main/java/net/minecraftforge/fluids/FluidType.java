/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids;

import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A definition of common attributes, properties, and methods that is applied
 * to a {@link Fluid}. This is used to link a flowing and source fluid together
 * without relying on tags. Most accessors do not correlate to in-game features;
 * they are provided for mods to take advantage of.
 *
 * <p>Accessors are typically implemented in a method call chain. As such, it
 * can provide a general implementation while more specific implementations
 * can be implemented by overriding methods further in the call chain (on fluids,
 * entities, etc.).
 */
public class FluidType
{
    /**
     * The number of fluid units that a bucket represents.
     */
    public static final int BUCKET_VOLUME = 1000;

    /**
     * A lazy value which computes the number of fluid types within the
     * registry.
     */
    public static final Lazy<Integer> SIZE = Lazy.of(() -> ForgeRegistries.FLUID_TYPES.get().getKeys().size());

    private String descriptionId;
    private final double motionScale;
    private final boolean canPushEntity;
    private final boolean canSwim;
    private final boolean canDrown;
    private final float fallDistanceModifier;
    private final boolean canExtinguish;
    private final boolean canConvertToSource;
    private final boolean supportsBoating;
    @Nullable
    private final BlockPathTypes pathType, adjacentPathType;
    private final boolean canHydrate;
    private final int lightLevel;
    private final int density;
    private final int temperature;
    private final int viscosity;
    private final Rarity rarity;

    /**
     * A map of actions performed to sound that should be played.
     */
    protected final Map<SoundAction, SoundEvent> sounds;

    /**
     * Default constructor.
     *
     * @param properties the general properties of the fluid type
     */
    public FluidType(final Properties properties)
    {
        this.descriptionId = properties.descriptionId;
        this.motionScale = properties.motionScale;
        this.canPushEntity = properties.canPushEntity;
        this.canSwim = properties.canSwim;
        this.canDrown = properties.canDrown;
        this.fallDistanceModifier = properties.fallDistanceModifier;
        this.canExtinguish = properties.canExtinguish;
        this.canConvertToSource = properties.canConvertToSource;
        this.supportsBoating = properties.supportsBoating;
        this.pathType = properties.pathType;
        this.adjacentPathType = properties.adjacentPathType;
        this.sounds = ImmutableMap.copyOf(properties.sounds);
        this.canHydrate = properties.canHydrate;
        this.lightLevel = properties.lightLevel;
        this.density = properties.density;
        this.temperature = properties.temperature;
        this.viscosity = properties.viscosity;
        this.rarity = properties.rarity;

        this.initClient();
    }

    /* Default Accessors */

    /**
     * Returns the component representing the name of the fluid type.
     *
     * @return the component representing the name of the fluid type
     */
    public Component getDescription()
    {
        return Component.translatable(this.getDescriptionId());
    }

    /**
     * Returns the identifier representing the name of the fluid type.
     * If no identifier was specified, then the identifier will be defaulted
     * to {@code fluid_type.<modid>.<registry_name>}.
     *
     * @return the identifier representing the name of the fluid type
     */
    public String getDescriptionId()
    {
        if (this.descriptionId == null)
            this.descriptionId = Util.makeDescriptionId("fluid_type", ForgeRegistries.FLUID_TYPES.get().getKey(this));
        return this.descriptionId;
    }

    /**
     * Returns the light level emitted by the fluid.
     *
     * <p>Note: This should be a value between {@code [0,15]}. If not specified, the
     * light level is {@code 0} as most fluids do not emit light.
     *
     * <p>Implementation: This is used by the bucket model to determine whether the fluid
     * should render full-bright when {@code applyFluidLuminosity} is {@code true}.
     *
     * @return the light level emitted by the fluid
     */
    public int getLightLevel()
    {
        return this.lightLevel;
    }

    /**
     * Returns the density of the fluid.
     *
     * <p>Note: This is an arbitrary number. Negative or zero values indicate
     * that the fluid is lighter than air. If not specified, the density is
     * approximately equivalent to the real-life density of water in {@code kg/m^3}.
     *
     * @return the density of the fluid
     */
    public int getDensity()
    {
        return this.density;
    }

    /**
     * Returns the temperature of the fluid.
     *
     * <p>Note: This is an arbitrary number. Higher temperature values indicate
     * that the fluid is hotter. If not specified, the temperature is approximately
     * equivalent to the real-life room temperature of water in {@code Kelvin}.
     *
     * @return the temperature of the fluid
     */
    public int getTemperature()
    {
        return this.temperature;
    }

    /**
     * Returns the viscosity, or thickness, of the fluid.
     *
     * <p>Note: This is an arbitrary number. The value should never be negative.
     * Higher viscosity values indicate that the fluid flows more slowly. If not
     * specified, the viscosity is approximately equivalent to the real-life
     * viscosity of water in {@code m/s^2}.
     *
     * @return the viscosity of the fluid
     */
    public int getViscosity()
    {
        return this.viscosity;
    }

    /**
     * Returns the rarity of the fluid.
     *
     * <p>Note: If not specified, the rarity of the fluid is {@link Rarity#COMMON}.
     *
     * @return the rarity of the fluid
     */
    public Rarity getRarity()
    {
        return this.rarity;
    }

    /**
     * Returns a sound to play when a certain action is performed. If no
     * sound is present, then the sound will be {@code null}.
     *
     * @param action the action being performed
     * @return the sound to play when performing the action
     */
    @Nullable
    public SoundEvent getSound(SoundAction action)
    {
        return this.sounds.get(action);
    }

    /* Entity-Based Accessors */

    /**
     * Returns how much the velocity of the fluid should be scaled by
     * when applied to an entity.
     *
     * @param entity the entity in the fluid
     * @return a scalar to multiply to the fluid velocity
     */
    public double motionScale(Entity entity)
    {
        return this.motionScale;
    }

    /**
     * Returns whether the fluid can push an entity.
     *
     * @param entity the entity in the fluid
     * @return {@code true} if the entity can be pushed by the fluid, {@code false} otherwise
     */
    public boolean canPushEntity(Entity entity)
    {
        return this.canPushEntity;
    }

    /**
     * Returns whether the entity can swim in the fluid.
     *
     * @param entity the entity in the fluid
     * @return {@code true} if the entity can swim in the fluid, {@code false} otherwise
     */
    public boolean canSwim(Entity entity)
    {
        return this.canSwim;
    }

    /**
     * Returns how much the fluid should scale the damage done to a falling
     * entity when hitting the ground per tick.
     *
     * <p>Implementation: If the entity is in many fluids, the smallest modifier
     * is applied.
     *
     * @param entity the entity in the fluid
     * @return a scalar to multiply to the fall damage
     */
    public float getFallDistanceModifier(Entity entity)
    {
        return this.fallDistanceModifier;
    }

    /**
     * Returns whether the entity can be extinguished by this fluid.
     *
     * @param entity the entity in the fluid
     * @return {@code true} if the entity can be extinguished, {@code false} otherwise
     */
    public boolean canExtinguish(Entity entity)
    {
        return this.canExtinguish;
    }

    /**
     * Performs how an entity moves when within the fluid. If using custom
     * movement logic, the method should return {@code true}. Otherwise, the
     * movement logic will default to water.
     *
     * @param state the state of the fluid
     * @param entity the entity moving within the fluid
     * @param movementVector the velocity of how the entity wants to move
     * @param gravity the gravity to apply to the entity
     * @return {@code true} if custom movement logic is performed, {@code false} otherwise
     */
    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity)
    {
        return false;
    }

    /**
     * Returns whether the entity can drown in the fluid.
     *
     * @param entity the entity in the fluid
     * @return {@code true} if the entity can drown in the fluid, {@code false} otherwise
     */
    public boolean canDrownIn(LivingEntity entity)
    {
        return this.canDrown;
    }

    /**
     * Performs what to do when an item is in a fluid.
     *
     * @param entity the item in the fluid
     */
    public void setItemMovement(ItemEntity entity)
    {
        Vec3 vec3 = entity.getDeltaMovement();
        entity.setDeltaMovement(vec3.x * (double)0.99F, vec3.y + (double)(vec3.y < (double)0.06F ? 5.0E-4F : 0.0F), vec3.z * (double)0.99F);
    }

    /**
     * Returns whether the boat can be used on the fluid.
     *
     * @param boat the boat trying to be used on the fluid
     * @return {@code true} if the boat can be used, {@code false} otherwise
     */
    public boolean supportsBoating(Boat boat)
    {
        return this.supportsBoating;
    }

    /**
     * Returns whether the boat can be used on the fluid.
     *
     * @param state the state of the fluid
     * @param boat the boat trying to be used on the fluid
     * @return {@code true} if the boat can be used, {@code false} otherwise
     */
    public boolean supportsBoating(FluidState state, Boat boat)
    {
        return this.supportsBoating(boat);
    }

    /**
     * Returns whether the entity can ride in this vehicle under the fluid.
     *
     * @param vehicle the vehicle being ridden in
     * @param rider the entity riding the vehicle
     * @return {@code true} if the vehicle can be ridden in under this fluid,
     *         {@code false} otherwise
     */
    public boolean canRideVehicleUnder(Entity vehicle, Entity rider)
    {
        if (this == ForgeMod.WATER_TYPE.get()) return !vehicle.dismountsUnderwater();
        return true;
    }

    /**
     * Returns whether the entity can be hydrated by this fluid.
     *
     * <p>Hydration is an arbitrary word which depends on the entity.
     *
     * @param entity the entity in the fluid
     * @return {@code true} if the entity can be hydrated, {@code false}
     *         otherwise
     */
    public boolean canHydrate(Entity entity)
    {
        return this.canHydrate;
    }

    /**
     * Returns a sound to play when a certain action is performed by the
     * entity in the fluid. If no sound is present, then the sound will be
     * {@code null}.
     *
     * @param entity the entity in the fluid
     * @param action the action being performed
     * @return the sound to play when performing the action
     */
    @Nullable
    public SoundEvent getSound(Entity entity, SoundAction action)
    {
        return this.getSound(action);
    }

    /* Level-Based Accessors */

    /**
     * Returns whether the block can be extinguished by this fluid.
     *
     * @param state the state of the fluid
     * @param getter the getter which can get the fluid
     * @param pos the position of the fluid
     * @return {@code true} if the block can be extinguished, {@code false} otherwise
     */
    public boolean canExtinguish(FluidState state, BlockGetter getter, BlockPos pos)
    {
        return this.canExtinguish;
    }

    /**
     * Returns whether the fluid can create a source.
     *
     * @param state the state of the fluid
     * @param reader the reader that can get the fluid
     * @param pos the location of the fluid
     * @return {@code true} if the fluid can create a source, {@code false} otherwise
     */
    public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos)
    {
        return this.canConvertToSource;
    }

    /**
     * Gets the path type of this fluid when an entity is pathfinding. When
     * {@code null}, uses vanilla behavior.
     *
     * @param state the state of the fluid
     * @param level the level which contains this fluid
     * @param pos the position of the fluid
     * @param mob the mob currently pathfinding, may be {@code null}
     * @param canFluidLog {@code true} if the path is being applied for fluids that can log blocks,
     *                    should be checked against if the fluid can log a block
     * @return the path type of this fluid
     */
    @Nullable
    public BlockPathTypes getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog)
    {
        return this.pathType;
    }

    /**
     * Gets the path type of the adjacent fluid to a pathfinding entity.
     * Path types with a negative malus are not traversable for the entity.
     * Pathfinding entities will favor paths consisting of a lower malus.
     * When {@code null}, uses vanilla behavior.
     *
     * @param state the state of the fluid
     * @param level the level which contains this fluid
     * @param pos the position of the fluid
     * @param mob the mob currently pathfinding, may be {@code null}
     * @param originalType the path type of the source the entity is on
     * @return the path type of this fluid
     */
    @Nullable
    public BlockPathTypes getAdjacentBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, BlockPathTypes originalType)
    {
        return this.adjacentPathType;
    }

    /**
     * Returns a sound to play when a certain action is performed at a
     * position. If no sound is present, then the sound will be {@code null}.
     *
     * @param player the player listening to the sound
     * @param getter the getter which can get the fluid
     * @param pos the position of the fluid
     * @param action the action being performed
     * @return the sound to play when performing the action
     */
    @Nullable
    public SoundEvent getSound(@Nullable Player player, BlockGetter getter, BlockPos pos, SoundAction action)
    {
        return this.getSound(action);
    }

    /**
     * Returns whether the block can be hydrated by a fluid.
     *
     * <p>Hydration is an arbitrary word which depends on the block.
     * <ul>
     *     <li>A farmland has moisture</li>
     *     <li>A sponge can soak up the liquid</li>
     *     <li>A coral can live</li>
     * </ul>
     *
     * @param state the state of the fluid
     * @param getter the getter which can get the fluid
     * @param pos the position of the fluid
     * @param source the state of the block being hydrated
     * @param sourcePos the position of the block being hydrated
     * @return {@code true} if the block can be hydrated, {@code false} otherwise
     */
    public boolean canHydrate(FluidState state, BlockGetter getter, BlockPos pos, BlockState source, BlockPos sourcePos)
    {
        return this.canHydrate;
    }

    /**
     * Returns the light level emitted by the fluid.
     *
     * <p>Note: This should be a value between {@code [0,15]}. If not specified, the
     * light level is {@code 0} as most fluids do not emit light.
     *
     * @param state the state of the fluid
     * @param getter the getter which can get the fluid
     * @param pos the position of the fluid
     * @return the light level emitted by the fluid
     */
    public int getLightLevel(FluidState state, BlockAndTintGetter getter, BlockPos pos)
    {
        return this.getLightLevel();
    }

    /**
     * Returns the density of the fluid.
     *
     * <p>Note: This is an arbitrary number. Negative or zero values indicate
     * that the fluid is lighter than air. If not specified, the density is
     * approximately equivalent to the real-life density of water in {@code kg/m^3}.
     *
     * @param state the state of the fluid
     * @param getter the getter which can get the fluid
     * @param pos the position of the fluid
     * @return the density of the fluid
     */
    public int getDensity(FluidState state, BlockAndTintGetter getter, BlockPos pos)
    {
        return this.getDensity();
    }

    /**
     * Returns the temperature of the fluid.
     *
     * <p>Note: This is an arbitrary number. Higher temperature values indicate
     * that the fluid is hotter. If not specified, the temperature is approximately
     * equivalent to the real-life room temperature of water in {@code Kelvin}.
     *
     * @param state the state of the fluid
     * @param getter the getter which can get the fluid
     * @param pos the position of the fluid
     * @return the temperature of the fluid
     */
    public int getTemperature(FluidState state, BlockAndTintGetter getter, BlockPos pos)
    {
        return this.getTemperature();
    }

    /**
     * Returns the viscosity, or thickness, of the fluid.
     *
     * <p>Note: This is an arbitrary number. The value should never be negative.
     * Higher viscosity values indicate that the fluid flows more slowly. If not
     * specified, the viscosity is approximately equivalent to the real-life
     * viscosity of water in {@code m/s^2}.
     *
     * @param state the state of the fluid
     * @param getter the getter which can get the fluid
     * @param pos the position of the fluid
     * @return the viscosity of the fluid
     */
    public int getViscosity(FluidState state, BlockAndTintGetter getter, BlockPos pos)
    {
        return this.getViscosity();
    }

    /* Stack-Based Accessors */

    /**
     * Returns whether the fluid can create a source.
     *
     * @param stack the stack holding the fluid
     * @return {@code true} if the fluid can create a source, {@code false} otherwise
     */
    public boolean canConvertToSource(FluidStack stack)
    {
        return this.canConvertToSource;
    }

    /**
     * Returns a sound to play when a certain action is performed. If no
     * sound is present, then the sound will be {@code null}.
     *
     * @param stack the stack holding the fluid
     * @param action the action being performed
     * @return the sound to play when performing the action
     */
    @Nullable
    public SoundEvent getSound(FluidStack stack, SoundAction action)
    {
        return this.getSound(action);
    }

    /**
     * Returns the component representing the name of the fluid type.
     *
     * @param stack the stack holding the fluid
     * @return the component representing the name of the fluid type
     */
    public Component getDescription(FluidStack stack)
    {
        return Component.translatable(this.getDescriptionId(stack));
    }

    /**
     * Returns the identifier representing the name of the fluid.
     * If no identifier was specified, then the identifier will be defaulted
     * to {@code fluid_type.<modid>.<registry_name>}.
     *
     * @param stack the stack holding the fluid
     * @return the identifier representing the name of the fluid
     */
    public String getDescriptionId(FluidStack stack)
    {
        return this.getDescriptionId();
    }

    /**
     * Returns whether the fluid can hydrate.
     *
     * <p>Hydration is an arbitrary word which depends on the implementation.
     *
     * @param stack the stack holding the fluid
     * @return {@code true} if the fluid can hydrate, {@code false} otherwise
     */
    public boolean canHydrate(FluidStack stack)
    {
        return this.canHydrate;
    }

    /**
     * Returns the light level emitted by the fluid.
     *
     * <p>Note: This should be a value between {@code [0,15]}. If not specified, the
     * light level is {@code 0} as most fluids do not emit light.
     *
     * @param stack the stack holding the fluid
     * @return the light level emitted by the fluid
     */
    public int getLightLevel(FluidStack stack)
    {
        return this.getLightLevel();
    }

    /**
     * Returns the density of the fluid.
     *
     * <p>Note: This is an arbitrary number. Negative or zero values indicate
     * that the fluid is lighter than air. If not specified, the density is
     * approximately equivalent to the real-life density of water in {@code kg/m^3}.
     *
     * @param stack the stack holding the fluid
     * @return the density of the fluid
     */
    public int getDensity(FluidStack stack)
    {
        return this.getDensity();
    }

    /**
     * Returns the temperature of the fluid.
     *
     * <p>Note: This is an arbitrary number. Higher temperature values indicate
     * that the fluid is hotter. If not specified, the temperature is approximately
     * equivalent to the real-life room temperature of water in {@code Kelvin}.
     *
     * @param stack the stack holding the fluid
     * @return the temperature of the fluid
     */
    public int getTemperature(FluidStack stack)
    {
        return this.getTemperature();
    }

    /**
     * Returns the viscosity, or thickness, of the fluid.
     *
     * <p>Note: This is an arbitrary number. The value should never be negative.
     * Higher viscosity values indicate that the fluid flows more slowly. If not
     * specified, the viscosity is approximately equivalent to the real-life
     * viscosity of water in {@code m/s^2}.
     *
     * @param stack the stack holding the fluid
     * @return the viscosity of the fluid
     */
    public int getViscosity(FluidStack stack)
    {
        return this.getViscosity();
    }

    /**
     * Returns the rarity of the fluid.
     *
     * <p>Note: If not specified, the rarity of the fluid is {@link Rarity#COMMON}.
     *
     * @param stack the stack holding the fluid
     * @return the rarity of the fluid
     */
    public Rarity getRarity(FluidStack stack)
    {
        return this.getRarity();
    }

    /* Helper Methods */

    /**
     * Returns whether the fluid type represents air.
     *
     * @return {@code true} if the type represents air, {@code false} otherwise
     */
    public final boolean isAir()
    {
        return this == ForgeMod.EMPTY_TYPE.get();
    }

    /**
     * Returns whether the fluid type is from vanilla.
     *
     * @return {@code true} if the type is from vanilla, {@code false} otherwise
     */
    public final boolean isVanilla()
    {
        return this == ForgeMod.LAVA_TYPE.get() || this == ForgeMod.WATER_TYPE.get();
    }

    /**
     * Returns the bucket containing the fluid.
     *
     * @param stack the stack holding the fluid
     * @return the bucket containing the fluid
     */
    public ItemStack getBucket(FluidStack stack)
    {
        return new ItemStack(stack.getFluid().getBucket());
    }

    /**
     * Returns the associated {@link BlockState} for a {@link FluidState}.
     *
     * @param getter the getter which can get the level data
     * @param pos the position of where the fluid would be
     * @param state the state of the fluid
     * @return the {@link BlockState} of a fluid
     */
    public BlockState getBlockForFluidState(BlockAndTintGetter getter, BlockPos pos, FluidState state)
    {
        return state.createLegacyBlock();
    }

    /**
     * Returns the {@link FluidState} when a {@link FluidStack} is trying to
     * place it.
     *
     * @param getter the getter which can get the level data
     * @param pos the position of where the fluid is being placed
     * @param stack the stack holding the fluid
     * @return the {@link FluidState} being placed
     */
    public FluidState getStateForPlacement(BlockAndTintGetter getter, BlockPos pos, FluidStack stack)
    {
        return stack.getFluid().defaultFluidState();
    }

    /**
     * Returns whether the fluid can be placed in the level.
     *
     * @param getter the getter which can get the level data
     * @param pos the position of where the fluid is being placed
     * @param state the state of the fluid being placed
     * @return {@code true} if the fluid can be placed, {@code false} otherwise
     */
    public final boolean canBePlacedInLevel(BlockAndTintGetter getter, BlockPos pos, FluidState state)
    {
        return !this.getBlockForFluidState(getter, pos, state).isAir();
    }

    /**
     * Returns whether the fluid can be placed in the level.
     *
     * @param getter the getter which can get the level data
     * @param pos the position of where the fluid is being placed
     * @param stack the stack holding the fluid
     * @return {@code true} if the fluid can be placed, {@code false} otherwise
     */
    public final boolean canBePlacedInLevel(BlockAndTintGetter getter, BlockPos pos, FluidStack stack)
    {
        return this.canBePlacedInLevel(getter, pos, this.getStateForPlacement(getter, pos, stack));
    }

    /**
     * Returns whether a fluid is lighter than air. If the fluid's density
     * is lower than or equal {@code 0}, the fluid is considered lighter than air.
     *
     * <p>Tip: {@code 0} is the "canonical" density of air within Forge.
     *
     * <p>Note: Fluids lighter than air will have their bucket model rotated
     * upside-down; fluid block models will have their vertices inverted.
     *
     * @return {@code true} if the fluid is lighter than air, {@code false} otherwise
     */
    public final boolean isLighterThanAir()
    {
        return this.getDensity() <= 0;
    }

    /**
     * Determines if this fluid should be vaporized when placed into a level.
     *
     * <p>Note: Fluids that can turn lava into obsidian should vaporize within
     * the nether to preserve the intentions of vanilla.
     *
     * @param level the level the fluid is being placed in
     * @param pos the position to place the fluid at
     * @param stack the stack holding the fluid being placed
     * @return {@code true} if this fluid should be vaporized on placement, {@code false} otherwise
     *
     * @see BucketItem#emptyContents(Player, Level, BlockPos, BlockHitResult)
     */
    public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack)
    {
        if (level.dimensionType().ultraWarm())
        {
            return this == ForgeMod.WATER_TYPE.get() || this.getStateForPlacement(level, pos, stack).is(FluidTags.WATER);
        }
        return false;
    }

    /**
     * Performs an action when a fluid can be vaporized when placed into a level.
     *
     * <p>Note: The fluid will already have been drained from the stack.
     *
     * @param player the player placing the fluid, may be {@code null} for blocks like dispensers
     * @param level the level the fluid is vaporized in
     * @param pos the position the fluid is vaporized at
     * @param stack the stack holding the fluid being vaporized
     *
     * @see BucketItem#emptyContents(Player, Level, BlockPos, BlockHitResult)
     */
    public void onVaporize(@Nullable Player player, Level level, BlockPos pos, FluidStack stack)
    {
        SoundEvent sound = this.getSound(player, level, pos, SoundActions.FLUID_VAPORIZE);
        level.playSound(player, pos, sound != null ? sound : SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

        for (int l = 0; l < 8; ++l)
            level.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, (double) pos.getX() + Math.random(), (double) pos.getY() + Math.random(), (double) pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public String toString() {
        @Nullable ResourceLocation name = ForgeRegistries.FLUID_TYPES.get().getKey(this);
        return name != null ? name.toString() : "Unregistered FluidType";
    }

    private Object renderProperties;

    /*
       DO NOT CALL, IT WILL DISAPPEAR IN THE FUTURE
       Call RenderProperties.get instead
     */
    public Object getRenderPropertiesInternal()
    {
        return renderProperties;
    }

    private void initClient()
    {
        // Minecraft instance isn't available in datagen, so don't call initializeClient if in datagen
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT && !net.minecraftforge.fml.loading.FMLLoader.getLaunchHandler().isData())
        {
            initializeClient(properties ->
            {
                if (properties == this)
                    throw new IllegalStateException("Don't extend IFluidTypeRenderProperties in your fluid type, use an anonymous class instead.");
                this.renderProperties = properties;
            });
        }
    }

    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
    {
    }

    /**
     * The properties of the fluid. The simple forms of each property can
     * be specified while more complex logic can be overridden in the {@link FluidType}.
     */
    public static final class Properties
    {
        private String descriptionId;
        private double motionScale = 0.014D;
        private boolean canPushEntity = true;
        private boolean canSwim = true;
        private boolean canDrown = true;
        private float fallDistanceModifier = 0.5F;
        private boolean canExtinguish = false;
        private boolean canConvertToSource = false;
        private boolean supportsBoating = false;
        @Nullable
        private BlockPathTypes pathType = BlockPathTypes.WATER,
                adjacentPathType = BlockPathTypes.WATER_BORDER;
        private final Map<SoundAction, SoundEvent> sounds = new HashMap<>();
        private boolean canHydrate = false;
        private int lightLevel = 0,
                density = 1000,
                temperature = 300,
                viscosity = 1000;
        private Rarity rarity = Rarity.COMMON;

        private Properties() {}

        /**
         * Creates a new instance of the properties.
         *
         * @return the property holder instance
         */
        public static Properties create()
        {
            return new Properties();
        }

        /**
         * Sets the identifier representing the name of the fluid type.
         *
         * @param descriptionId  the identifier representing the name of the fluid type
         * @return the property holder instance
         */
        public Properties descriptionId(String descriptionId)
        {
            this.descriptionId = descriptionId;
            return this;
        }

        /**
         * Sets how much the velocity of the fluid should be scaled by.
         *
         * @param motionScale a scalar to multiply to the fluid velocity
         * @return the property holder instance
         */
        public Properties motionScale(double motionScale)
        {
            this.motionScale = motionScale;
            return this;
        }

        /**
         * Sets whether the fluid can push an entity.
         *
         * @param canPushEntity if the fluid can push an entity
         * @return the property holder instance
         */
        public Properties canPushEntity(boolean canPushEntity)
        {
            this.canPushEntity = canPushEntity;
            return this;
        }

        /**
         * Sets whether the fluid can be swum in.
         *
         * @param canSwim if the fluid can be swum in
         * @return the property holder instance
         */
        public Properties canSwim(boolean canSwim)
        {
            this.canSwim = canSwim;
            return this;
        }

        /**
         * Sets whether the fluid can drown something.
         *
         * @param canDrown if the fluid can drown something
         * @return the property holder instance
         */
        public Properties canDrown(boolean canDrown)
        {
            this.canDrown = canDrown;
            return this;
        }

        /**
         * Sets how much the fluid should scale the damage done when hitting
         * the ground per tick.
         *
         * @param fallDistanceModifier a scalar to multiply to the fall damage
         * @return the property holder instance
         */
        public Properties fallDistanceModifier(float fallDistanceModifier)
        {
            this.fallDistanceModifier = fallDistanceModifier;
            return this;
        }

        /**
         * Sets whether the fluid can extinguish.
         *
         * @param canExtinguish if the fluid can extinguish
         * @return the property holder instance
         */
        public Properties canExtinguish(boolean canExtinguish)
        {
            this.canExtinguish = canExtinguish;
            return this;
        }

        /**
         * Sets whether the fluid can create a source.
         *
         * @param canConvertToSource if the fluid can create a source
         * @return the property holder instance
         */
        public Properties canConvertToSource(boolean canConvertToSource)
        {
            this.canConvertToSource = canConvertToSource;
            return this;
        }

        /**
         * Sets whether the fluid supports boating.
         *
         * @param supportsBoating if the fluid supports boating
         * @return the property holder instance
         */
        public Properties supportsBoating(boolean supportsBoating)
        {
            this.supportsBoating = supportsBoating;
            return this;
        }

        /**
         * Sets the path type of this fluid.
         *
         * @param pathType the path type of this fluid
         * @return the property holder instance
         */
        public Properties pathType(@Nullable BlockPathTypes pathType)
        {
            this.pathType = pathType;
            return this;
        }

        /**
         * Sets the path type of the adjacent fluid. Path types with a negative
         * malus are not traversable. Pathfinding will favor paths consisting of
         * a lower malus.
         *
         * @param adjacentPathType the path type of this fluid
         * @return the property holder instance
         */
        public Properties adjacentPathType(@Nullable BlockPathTypes adjacentPathType)
        {
            this.adjacentPathType = adjacentPathType;
            return this;
        }

        /**
         * Sets a sound to play when a certain action is performed.
         *
         * @param action the action being performed
         * @param sound the sound to play when performing the action
         * @return the property holder instance
         */
        public Properties sound(SoundAction action, SoundEvent sound)
        {
            this.sounds.put(action, sound);
            return this;
        }

        /**
         * Sets whether the fluid can hydrate.
         *
         * <p>Hydration is an arbitrary word which depends on the implementation.
         *
         * @param canHydrate if the fluid can hydrate
         * @return the property holder instance
         */
        public Properties canHydrate(boolean canHydrate)
        {
            this.canHydrate = canHydrate;
            return this;
        }

        /**
         * Sets the light level emitted by the fluid.
         *
         * @param lightLevel the light level emitted by the fluid
         * @return the property holder instance
         * @throws IllegalArgumentException if light level is not between [0,15]
         */
        public Properties lightLevel(int lightLevel)
        {
            if (lightLevel < 0 || lightLevel > 15)
                throw new IllegalArgumentException("The light level should be between [0,15].");
            this.lightLevel = lightLevel;
            return this;
        }

        /**
         * Sets the density of the fluid.
         *
         * @param density the density of the fluid
         * @return the property holder instance
         */
        public Properties density(int density)
        {
            this.density = density;
            return this;
        }

        /**
         * Sets the temperature of the fluid.
         *
         * @param temperature the temperature of the fluid
         * @return the property holder instance
         */
        public Properties temperature(int temperature)
        {
            this.temperature = temperature;
            return this;
        }

        /**
         * Sets the viscosity, or thickness, of the fluid.
         *
         * @param viscosity the viscosity of the fluid
         * @return the property holder instance
         * @throws IllegalArgumentException if viscosity is negative
         */
        public Properties viscosity(int viscosity)
        {
            if (viscosity < 0)
                throw new IllegalArgumentException("The viscosity should never be negative.");
            this.viscosity = viscosity;
            return this;
        }

        /**
         * Sets the rarity of the fluid.
         *
         * @param rarity the rarity of the fluid
         * @return the property holder instance
         */
        public Properties rarity(Rarity rarity)
        {
            this.rarity = rarity;
            return this;
        }
    }
}
