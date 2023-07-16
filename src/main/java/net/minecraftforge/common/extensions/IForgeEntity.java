/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Collection;
import java.util.function.BiPredicate;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public interface IForgeEntity extends ICapabilitySerializable<CompoundTag>
{
    private Entity self() { return (Entity) this; }

    default void deserializeNBT(CompoundTag nbt)
    {
        self().load(nbt);
    }

    default CompoundTag serializeNBT()
    {
        CompoundTag ret = new CompoundTag();
        String id = self().getEncodeId();
        if (id != null)
        {
            ret.putString("id", self().getEncodeId());
        }
        return self().saveWithoutId(ret);
    }

    boolean canUpdate();
    void canUpdate(boolean value);

    @Nullable
    Collection<ItemEntity> captureDrops();
    Collection<ItemEntity> captureDrops(@Nullable Collection<ItemEntity> captureDrops);


    /**
     * Returns a NBTTagCompound that can be used to store custom data for this entity.
     * It will be written, and read from disc, so it persists over world saves.
     * @return A NBTTagCompound
     */
    CompoundTag getPersistentData();

    /**
     * Used in model rendering to determine if the entity riding this entity should be in the 'sitting' position.
     * @return false to prevent an entity that is mounted to this entity from displaying the 'sitting' animation.
     */
    default boolean shouldRiderSit()
    {
        return true;
    }

    /**
     * Called when a user uses the creative pick block button on this entity.
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, empty ItemStack if nothing should be added.
     */
    default ItemStack getPickedResult(HitResult target)
    {
        ItemStack result = self().getPickResult();
        if (result == null) {
            SpawnEggItem egg = ForgeSpawnEggItem.fromEntityType(self().getType());
            if (egg != null)
                result = new ItemStack(egg);
            else
                result = ItemStack.EMPTY;
        }
        return result;
    }

    /**
     * If a rider of this entity can interact with this entity. Should return true on the
     * ridden entity if so.
     *
     * @return if the entity can be interacted with from a rider
     */
    default boolean canRiderInteract()
    {
        return false;
    }

    /**
     * Returns whether the entity can ride in this vehicle under the fluid.
     *
     * @param type the type of the fluid
     * @param rider the entity riding the vehicle
     * @return {@code true} if the vehicle can be ridden in under this fluid,
     *         {@code false} otherwise
     */
    default boolean canBeRiddenUnderFluidType(FluidType type, Entity rider)
    {
        return type.canRideVehicleUnder(self(), rider);
    }

    /**
     * Checks if this {@link Entity} can trample a {@link Block}.
     *
     * @param pos The block pos
     * @param fallDistance The fall distance
     * @return {@code true} if this entity can trample, {@code false} otherwise
     */
    boolean canTrample(BlockState state, BlockPos pos, float fallDistance);

    /**
     * Returns The classification of this entity
     * @param forSpawnCount If this is being invoked to check spawn count caps.
     * @return If the creature is of the type provided
     */
    default MobCategory getClassification(boolean forSpawnCount)
    {
        return self().getType().getCategory();
    }

    /**
     * Gets whether this entity has been added to a world (for tracking). Specifically
     * between the times when an entity is added to a world and the entity being removed
     * from the world's tracked lists.
     *
     * @return True if this entity is being tracked by a world
     */
    // TODO: rename in 1.19 to isAddedToLevel
    boolean isAddedToWorld();

    /**
     * Called after the entity has been added to the world's
     * ticking list. Can be overriden, but needs to call super
     * to prevent MC-136995.
     */
    // TODO: rename in 1.19 to onAddedToLevel
    void onAddedToWorld();

    /**
     * Called after the entity has been removed to the world's
     * ticking list. Can be overriden, but needs to call super
     * to prevent MC-136995.
     */
    // TODO: rename in 1.19 to onRemovedFromLevel
    void onRemovedFromWorld();

    /**
     * Revives an entity that has been removed from a world.
     * Used as replacement for entity.removed = true. Having it as a function allows
     * the entity to react to being revived.
     */
    void revive();


    /**
     * This is used to specify that your entity has multiple individual parts, such as the Vanilla Ender Dragon.
     *
     * See {@link EnderDragon} for an example implementation.
     * @return true if this is a multipart entity.
     */
    default boolean isMultipartEntity()
    {
        return false;
    }

    /**
     * Gets the individual sub parts that make up this entity.
     *
     * The entities returned by this method are NOT saved to the world in nay way, they exist as an extension
     * of their host entity. The child entity does not track its server-side(or client-side) counterpart, and
     * the host entity is responsible for moving and managing these children.
     *
     * Only used if {@link #isMultipartEntity()} returns true.
     *
     * See {@link EnderDragon} for an example implementation.
     * @return The child parts of this entity. The value to be returned here should be cached.
     */
    @Nullable
    default PartEntity<?>[] getParts()
    {
        return null;
    }

    /**
     * @return Return the height in blocks the Entity can step up without needing to jump
     * This is the sum of vanilla's {@link Entity#maxUpStep()} method and the current value
     * of the {@link net.minecraftforge.common.ForgeMod#STEP_HEIGHT_ADDITION} attribute
     * (if this Entity is a {@link LivingEntity} and has the attribute), clamped at 0.
     */
    default float getStepHeight()
    {
        float vanillaStep = self().maxUpStep();
        if (self() instanceof LivingEntity living)
        {
            AttributeInstance stepHeightAttribute = living.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
            if (stepHeightAttribute != null)
            {
                return (float) Math.max(0, vanillaStep + stepHeightAttribute.getValue());
            }
        }
        return vanillaStep;
    }

    /**
     * Returns the height of the fluid type in relation to the bounding box of
     * the entity. If the entity is not in the fluid type, then {@code 0}
     * is returned.
     *
     * @param type the type of the fluid
     * @return the height of the fluid compared to the entity
     */
    double getFluidTypeHeight(FluidType type);

    /**
     * Returns the fluid type which is the highest on the bounding box of
     * the entity.
     *
     * @return the fluid type which is the highest on the bounding box of
     *         the entity
     */
    FluidType getMaxHeightFluidType();

    /**
     * Returns whether the entity is within the fluid type of the state.
     *
     * @param state the state of the fluid
     * @return {@code true} if the entity is within the fluid type of the
     *         state, {@code false} otherwise
     */
    default boolean isInFluidType(FluidState state)
    {
        return this.isInFluidType(state.getFluidType());
    }

    /**
     * Returns whether the entity is within the fluid type.
     *
     * @param type the type of the fluid
     * @return {@code true} if the entity is within the fluid type,
     *         {@code false} otherwise
     */
    default boolean isInFluidType(FluidType type)
    {
        return this.getFluidTypeHeight(type) > 0.0D;
    }

    /**
     * Returns whether any fluid type the entity is currently in matches
     * the specified condition.
     *
     * @param predicate a test taking in the fluid type and its height
     * @return {@code true} if a fluid type meets the condition, {@code false}
     *         otherwise
     */
    default boolean isInFluidType(BiPredicate<FluidType, Double> predicate)
    {
        return isInFluidType(predicate, false);
    }

    /**
     * Returns whether the fluid type the entity is currently in matches
     * the specified condition.
     *
     * @param predicate a test taking in the fluid type and its height
     * @param forAllTypes {@code true} if all fluid types should match the
     *                    condition instead of at least one
     * @return {@code true} if a fluid type meets the condition, {@code false}
     *         otherwise
     */
    boolean isInFluidType(BiPredicate<FluidType, Double> predicate, boolean forAllTypes);

    /**
     * Returns whether the entity is in a fluid.
     *
     * @return {@code true} if the entity is in a fluid, {@code false} otherwise
     */
    boolean isInFluidType();

    /**
     * Returns the fluid that is on the entity's eyes.
     *
     * @return the fluid that is on the entity's eyes
     */
    FluidType getEyeInFluidType();

    /**
     * Returns whether the fluid is on the entity's eyes.
     *
     * @return {@code true} if the fluid is on the entity's eyes, {@code false} otherwise
     */
    default boolean isEyeInFluidType(FluidType type)
    {
        return type == this.getEyeInFluidType();
    }

    /**
     * Returns whether the entity can start swimming in the fluid.
     *
     * @return {@code true} if the entity can start swimming, {@code false} otherwise
     */
    default boolean canStartSwimming()
    {
        return !this.getEyeInFluidType().isAir() && this.canSwimInFluidType(this.getEyeInFluidType());
    }

    /**
     * Returns how much the velocity of the fluid should be scaled by
     * when applied to an entity.
     *
     * @param type the type of the fluid
     * @return a scalar to multiply to the fluid velocity
     */
    default double getFluidMotionScale(FluidType type)
    {
        return type.motionScale(self());
    }

    /**
     * Returns whether the fluid can push an entity.
     *
     * @param type the type of the fluid
     * @return {@code true} if the entity can be pushed by the fluid, {@code false} otherwise
     */
    default boolean isPushedByFluid(FluidType type)
    {
        return self().isPushedByFluid() && type.canPushEntity(self());
    }

    /**
     * Returns whether the entity can swim in the fluid.
     *
     * @param type the type of the fluid
     * @return {@code true} if the entity can swim in the fluid, {@code false} otherwise
     */
    default boolean canSwimInFluidType(FluidType type)
    {
        return type.canSwim(self());
    }

    /**
     * Returns whether the entity can be extinguished by this fluid.
     *
     * @param type the type of the fluid
     * @return {@code true} if the entity can be extinguished, {@code false} otherwise
     */
    default boolean canFluidExtinguish(FluidType type)
    {
        return type.canExtinguish(self());
    }

    /**
     * Returns how much the fluid should scale the damage done to a falling
     * entity when hitting the ground per tick.
     *
     * <p>Implementation: If the entity is in many fluids, the smallest modifier
     * is applied.
     *
     * @param type the type of the fluid
     * @return a scalar to multiply to the fall damage
     */
    default float getFluidFallDistanceModifier(FluidType type)
    {
        return type.getFallDistanceModifier(self());
    }

    /**
     * Returns whether the entity can be hydrated by this fluid.
     *
     * <p>Hydration is an arbitrary word which depends on the entity.
     *
     * @param type the type of the fluid
     * @return {@code true} if the entity can be hydrated, {@code false}
     *         otherwise
     */
    default boolean canHydrateInFluidType(FluidType type)
    {
        return type.canHydrate(self());
    }

    /**
     * Returns a sound to play when a certain action is performed by the
     * entity in the fluid. If no sound is present, then the sound will be
     * {@code null}.
     *
     * @param type the type of the fluid
     * @param action the action being performed
     * @return the sound to play when performing the action
     */
    @Nullable
    default SoundEvent getSoundFromFluidType(FluidType type, SoundAction action)
    {
        return type.getSound(self(), action);
    }

    /**
     * Returns whether this {@link Entity} has custom outline rendering behavior which does
     * not use the existing automatic outline rendering based on {@link Entity#isCurrentlyGlowing()}
     * and the entity's team color.
     *
     * @param player the local player currently viewing this {@code Entity}
     * @return {@code true} to enable outline processing
     */
    default boolean hasCustomOutlineRendering(Player player)
    {
        return false;
    }

    default float getEyeHeightForge(Pose pose, EntityDimensions size)
    {
        float eyeHeight = self().getEyeHeightAccess(pose, size);
        EntityEvent.EyeHeight evt = new EntityEvent.EyeHeight(self(), pose, size, eyeHeight);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.getNewEyeHeight();
    }

    /**
     * When {@code false}, the fluid will no longer update its height value while
     * within a boat while it is not within a fluid ({@link Boat#isUnderWater()}.
     *
     * @param state the state of the fluid the rider is within
     * @param boat the boat the rider is within that is not inside a fluid
     * @return {@code true} if the fluid height should be updated, {@code false} otherwise
     */
    default boolean shouldUpdateFluidWhileBoating(FluidState state, Boat boat)
    {
        return boat.shouldUpdateFluidWhileRiding(state, self());
    }
}
