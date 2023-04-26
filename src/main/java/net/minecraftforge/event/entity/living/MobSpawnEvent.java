/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.LogicalSide;

/**
 * The subclasses of this event are fired whenever a mob performs a spawning-related action.
 */
public abstract class MobSpawnEvent extends EntityEvent
{
    private final ServerLevelAccessor level;
    private final double x;
    private final double y;
    private final double z;

    /**
     * @apiNote Do not construct directly. Access via {@link ForgeEventFactory#onFinalizeSpawn}.
     */
    @ApiStatus.Internal
    public MobSpawnEvent(Mob mob, ServerLevelAccessor level, double x, double y, double z)
    {
        super(mob);
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Mob getEntity()
    {
        return (Mob) super.getEntity();
    }

    /**
     * @return The level relating to the mob spawn action
     */
    public ServerLevelAccessor getLevel()
    {
        return this.level;
    }

    /**
     * @return The x-coordinate relating to the mob spawn action
     */
    public double getX()
    {
        return this.x;
    }

    /**
     * @return The y-coordinate relating to the mob spawn action
     */
    public double getY()
    {
        return this.y;
    }

    /**
     * @return The z-coordinate relating to the mob spawn action
     */
    public double getZ()
    {
        return this.z;
    }

    /**
     * This event is fired before {@link Mob#finalizeSpawn} is called.<br>
     * This allows mods to control mob initialization.<br>
     * In vanilla code, this event is injected by a transformer and not via patch, so calls cannot be traced via call hierarchy (it is not source-visible).
     * <p>
     * Canceling this event will result in {@link Mob#finalizeSpawn} not being called, and the returned value always being null, instead of propagating the SpawnGroupData.<br>
     * The entity will still be spawned. If you want to prevent the spawn, use {@link FinalizeSpawn#setSpawnCancelled}, which will cause Forge to prevent the spawn.
     * <p>
     * This event is fired on {@link MinecraftForge#EVENT_BUS}, and is only fired on the logical server.
     * @see ForgeEventFactory#onFinalizeSpawn
     * @apiNote Callers do not need to check if the entity's spawn was cancelled, as the spawn will be blocked by Forge.
     */
    @Cancelable
    public static class FinalizeSpawn extends MobSpawnEvent
    {
        private final MobSpawnType spawnType;
        @Nullable private final BaseSpawner spawner;
        
        private DifficultyInstance difficulty;
        @Nullable private SpawnGroupData spawnData;
        @Nullable private CompoundTag spawnTag;

        public FinalizeSpawn(Mob entity, ServerLevelAccessor level, double x, double y, double z, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag, @Nullable BaseSpawner spawner)
        {
            super(entity, level, x, y, z);
            this.difficulty = difficulty;
            this.spawnType = spawnType;
            this.spawnData = spawnData;
            this.spawnTag = spawnTag;
            this.spawner = spawner;
        }

        /**
         * Retrieves the {@link DifficultyInstance} for the chunk where the mob is about to be spawned.
         * @return The local difficulty instance
         */
        public DifficultyInstance getDifficulty()
        {
            return this.difficulty;
        }

        /**
         * Sets the difficulty instance for this event, which will be propagated to {@link Mob#finalizeSpawn} unless cancelled.
         * The difficulty instance controls how likely certain random effects are to occur, or if certain mob abilities are enabled.
         * @param inst The new difficulty instance.
         */
        public void setDifficulty(DifficultyInstance inst)
        {
            this.difficulty = inst;
        }
        
        /**
         * Retrieves the type of mob spawn that happened (the event that caused the spawn). The enum names are self-explanatory.
         * @return The mob spawn type.
         * @see MobSpawnType
         */
        public MobSpawnType getSpawnType()
        {
            return this.spawnType;
        }

        /**
         * Retrieves the {@link SpawnGroupData} for this entity. When spawning mobs in a loop, this group data is used for the entire group and impacts future spawns.
         * This is how entities like horses ensure that the whole group spawns as a single variant. How this is used varies on a per-entity basis.
         * @return The spawn group data.
         */
        @Nullable
        public SpawnGroupData getSpawnData()
        {
            return this.spawnData;
        }

        /**
         * Sets the spawn data for this entity. If this event is cancelled, this value is not used, since {@link Mob#finalizeSpawn} will not be called.
         * @param data The new spawn data
         * @see FinalizeSpawn#getSpawnData
         */
        public void setSpawnData(@Nullable SpawnGroupData data)
        {
            this.spawnData = data;
        }

        /**
         * This is the NBT data the entity was loaded from, if applicable. It is unknown if the entity has already been loaded from this data, or if it will be loaded later.
         * Callers should not modify this data. If you need to change the data, you can create a copy, modify it, and set it via {@link FinalizeSpawn#setSpawnTag}
         * @return The spawn data this entity was or will be loaded from, if any.
         */
        @Nullable
        public CompoundTag getSpawnTag()
        {
            return this.spawnTag;
        }

        /**
         * Sets the spawn data for this event, which will be propagated to {@link Mob#finalizeSpawn} unless cancelled.
         * The only vanilla mob known to use this tag for anything in finalize is tropical fish for setting the variant when spawned via bucket.
         * @param tag The new spawn tag
         */
        public void setSpawnTag(@Nullable CompoundTag tag)
        {
            this.spawnTag = tag;
        }

        /**
         * Retrieves the underlying {@link BaseSpawner} instance if this mob was created by a Mob Spawner of some form.
         * This is always null unless {@link #getSpawnType()} is {@link MobSpawnType#SPAWNER}, and may still be null even then.
         * @return The BaseSpawner responsible for triggering the spawn, or null if none is available.
         */
        @Nullable
        public BaseSpawner getSpawner()
        {
            return spawner;
        }

        /**
         * This method can be used to cancel the spawn of this mob.<p>
         * This method must be used if you want to block the spawn, as canceling the event only blocks the call to {@link Mob#finalizeSpawn}.<p>
         * Note that if the spawn is cancelled, but the event is not, then {@link Mob#finalizeSpawn} will still be called, but the entity will not be spawned.
         * Usually that has no side effects, but callers should be aware.
         * @param cancel If the spawn should be cancelled (or not).
         */
        public void setSpawnCancelled(boolean cancel) {
            this.getEntity().setSpawnCancelled(cancel);
        }

        /**
         * Returns the current spawn cancellation status, which can be changed via {@link FinalizeSpawn#setSpawnCancelled(boolean)}.
         * @return If this mob's spawn is cancelled or not.
         * @implNote This is enforced in {@link ForgeInternalHandler#builtinMobSpawnBlocker} and a patch in {@link WorldGenRegion#addEntity}
         */
        public boolean isSpawnCancelled() {
            return this.getEntity().isSpawnCancelled();
        }
    }

    /**
     * This event is fired from {@link Mob#checkDespawn()}.<br>
     * It fires once per tick per mob that is attempting to despawn.<br>
     * It is not fired if the mob is persistent (meaning it may not despawn).<br>
     * <p>
     * This event is not {@linkplain Cancelable cancellable}, but does {@linkplain HasResult have a result}.
     * {@link Result#DEFAULT} indicates that default despawn mechanics should be used.
     * {@link Result#ALLOW} indicates that the mob should forcefully despawn.
     * {@link Result#DENY} indicates that the mob should forcefully stay spawned.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#SERVER logical server}.
     *
     * @see LivingEntity#checkDespawn()
     * @author cpw
     */
    @HasResult
    public static class AllowDespawn extends MobSpawnEvent
    {
        public AllowDespawn(Mob mob, ServerLevelAccessor level)
        {
            super(mob, level, mob.getX(), mob.getY(), mob.getZ());
        }
    }
}
