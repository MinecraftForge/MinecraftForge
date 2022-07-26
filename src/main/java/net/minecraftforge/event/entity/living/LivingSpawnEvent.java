/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired whenever a {@link Mob} should do something spawn-related.
 * <p>
 * This event is not {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
 * only on the {@linkplain LogicalSide#SERVER logical server}.
 */
public class LivingSpawnEvent extends LivingEvent
{
    private final LevelAccessor level;
    private final Mob mob;
    private final double x;
    private final double y;
    private final double z;

    public LivingSpawnEvent(Mob mob, LevelAccessor level, double x, double y, double z)
    {
        super(mob);
        this.mob = mob;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * {@return the mob that is causing a spawn action}
     */
    @Override
    public Mob getEntity()
    {
        return this.mob;
    }

    /**
     * {@return the level relating to the mob spawn action}
     */
    public LevelAccessor getLevel()
    {
        return this.level;
    }

    /**
     * {@return the x-coordinate relating to the mob spawn action}
     */
    public double getX()
    {
        return this.x;
    }

    /**
     * {@return the y-coordinate relating to the mob spawn action}
     */
    public double getY()
    {
        return this.y;
    }

    /**
     * {@return the z-coordinate relating to the mob spawn action}
     */
    public double getZ()
    {
        return this.z;
    }

    /**
     * This event is fired before a {@link Mob} spawns.
     * <p>
     * This event is not {@linkplain Cancelable cancellable}, but does {@linkplain HasResult have a result}.
     * {@linkplain Result#DEFAULT DEFAULT} indicates that default spawn rules should be used.
     * {@linkplain Result#ALLOW ALLOW} indicates that the mob should spawn regardless of default spawn rules.
     * {@linkplain Result#DENY DENY} indicates that the mob should not spawn.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#SERVER logical server}.
     */
    @HasResult
    public static class CheckSpawn extends LivingSpawnEvent
    {
        @Nullable
        private final BaseSpawner spawner;
        private final MobSpawnType spawnReason;

        public CheckSpawn(Mob mob, LevelAccessor level, double x, double y, double z, @Nullable BaseSpawner spawner, MobSpawnType spawnReason)
        {
            super(mob, level, x, y, z);
            this.spawner = spawner;
            this.spawnReason = spawnReason;
        }

        public boolean isSpawner()
        {
            return spawner != null;
        }

        @Nullable
        public BaseSpawner getSpawner()
        {
            return spawner;
        }

        public MobSpawnType getSpawnReason()
        {
            return spawnReason;
        }
    }

    /**
     * This event is fired whenever a {@link Mob} is set to be spawned, to allow for mod-specific initializers.
     * <p>
     * This event is {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
     * If the event is canceled, the mob will not spawn.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#SERVER logical server}.
     */
    @Cancelable
    public static class SpecialSpawn extends LivingSpawnEvent
    {
        @Nullable
        private final BaseSpawner spawner;
        private final MobSpawnType spawnReason;

        public SpecialSpawn(Mob entity, LevelAccessor level, double x, double y, double z, @Nullable BaseSpawner spawner, MobSpawnType spawnReason)
        {
            super(entity, level, x, y, z);
            this.spawner = spawner;
            this.spawnReason = spawnReason;
        }

        @Nullable
        public BaseSpawner getSpawner()
        {
            return spawner;
        }

        public MobSpawnType getSpawnReason()
        {
            return spawnReason;
        }
    }

    /**
     * This event is fired for a {@link Mob} that can despawn <i>each mob tick</i>.
     * This event only fires if a mob can be allowed to despawn and will not
     * otherwise fire if a despawn is certain.
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
    public static class AllowDespawn extends LivingSpawnEvent
    {
        public AllowDespawn(Mob mob)
        {
            super(mob, mob.level, mob.getX(), mob.getY(), mob.getZ());
        }
    }
}
