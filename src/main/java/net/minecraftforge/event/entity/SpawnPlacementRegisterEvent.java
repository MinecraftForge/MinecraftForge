/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.ApiStatus;

/**
 * This event allows each {@link EntityType} to have a {@link SpawnPlacements.SpawnPredicate} registered or modified.
 * Spawn Predicates are checked whenever an {@link Entity} of the given {@link EntityType} spawns in the world naturally.
 *
 * If registering your own entity's spawn placements, you should use {@link SpawnPlacementRegisterEvent#register(EntityType, SpawnPlacements.Type, Heightmap.Types, SpawnPlacements.SpawnPredicate, Operation)}
 * So that you ensure that your entity has a heightmap type and placement type registered.
 *
 * If modifying vanilla or another mod's spawn placements, you can use three operations:
 *  REPLACE: checked first, the last mod to replace the predicate wipes out all other predicates. Listen with a low {@link EventPriority} if you need to do this.
 *  OR: checked second, only one of these predicates must pass along with the original predicate
 *  AND: checked third, these predicates must all pass along with the original predicate
 *
 * <p>
 * This event is not {@linkplain Cancelable cancellable} and does not {@linkplain Event.HasResult have a result}.
 * <p>
 *
 *  Fired on the Mod bus {@link IModBusEvent}.<br>
 */
public class SpawnPlacementRegisterEvent extends Event implements IModBusEvent
{
    private final Map<EntityType<?>, MergedSpawnPredicate<?>> map;

    @ApiStatus.Internal
    public SpawnPlacementRegisterEvent(Map<EntityType<?>, MergedSpawnPredicate<?>> map)
    {
        this.map = map;
    }

    /**
     * Register an optional spawn placement {@code predicate} for a given {@code entityType}
     */
    public <T extends Entity> void register(EntityType<T> entityType, SpawnPlacements.SpawnPredicate<T> predicate)
    {
        register(entityType, null, null, predicate, Operation.OR);
    }

    /**
     * Register a {@code predicate} for a given {@code entityType} with a given {@code operation} for handling
     */
    public <T extends Entity> void register(EntityType<T> entityType, SpawnPlacements.SpawnPredicate<T> predicate, Operation operation)
    {
        register(entityType, null, null, predicate, operation);
    }

    /**
     * Register a {@code predicate} for a given {@code entityType} and {@code operation}
     * With the option of changing the {@code placementType} and {@code heightmap}. These are only applied if {@link Operation#REPLACE} is used.
     * Use {@code null} for the placement or heightmap to leave them as is (which should be done in almost every case)
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> void register(EntityType<T> entityType, @Nullable SpawnPlacements.Type placementType, @Nullable Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate, Operation operation)
    {
        if (!map.containsKey(entityType))
        {
            if (placementType == null)
            {
                throw new NullPointerException("Registering a new Spawn Predicate requires a nonnull placement type! Entity Type: " + ForgeRegistries.ENTITY_TYPES.getKey(entityType));
            }
            if (heightmap == null)
            {
                throw new NullPointerException("Registering a new Spawn Predicate requires a nonnull heightmap type! Entity Type: "+ ForgeRegistries.ENTITY_TYPES.getKey(entityType));
            }
            map.put(entityType, new MergedSpawnPredicate<>(predicate, placementType, heightmap));
        }
        else
        {
            if (operation != Operation.REPLACE && (heightmap != null || placementType != null))
            {
                throw new IllegalStateException("Nonnull heightmap types or spawn placement types may only be used with the REPLACE operation. Entity Type: "+ ForgeRegistries.ENTITY_TYPES.getKey(entityType));
            }
            ((MergedSpawnPredicate<T>) map.get(entityType)).merge(operation, predicate, placementType, heightmap);
        }
    }

    public enum Operation
    {
        /**
         * Checked third, these predicates must all pass along with the original predicate
         */
        AND,
        /**
         * Checked second, only one of these predicates must pass along with the original predicate
         */
        OR,
        /**
         * Checked first, the last mod to replace the predicate wipes out all other predicates. Listen with a low {@link EventPriority} if you need to do this.
         */
        REPLACE
    }

    public static class MergedSpawnPredicate<T extends Entity>
    {
        private final SpawnPlacements.SpawnPredicate<T> originalPredicate;
        private final List<SpawnPlacements.SpawnPredicate<T>> orPredicates;
        private final List<SpawnPlacements.SpawnPredicate<T>> andPredicates;
        @Nullable private SpawnPlacements.SpawnPredicate<T> replacementPredicate;
        private SpawnPlacements.Type spawnType;
        private Heightmap.Types heightmapType;

        public MergedSpawnPredicate(SpawnPlacements.SpawnPredicate<T> originalPredicate, SpawnPlacements.Type spawnType, Heightmap.Types heightmapType)
        {
            this.originalPredicate = originalPredicate;
            this.orPredicates = new ArrayList<>();
            this.andPredicates = new ArrayList<>();
            this.replacementPredicate = null;
            this.spawnType = spawnType;
            this.heightmapType = heightmapType;
        }

        public SpawnPlacements.Type getSpawnType()
        {
            return spawnType;
        }

        public Heightmap.Types getHeightmapType()
        {
            return heightmapType;
        }

        private void merge(Operation operation, SpawnPlacements.SpawnPredicate<T> predicate, @Nullable SpawnPlacements.Type spawnType, @Nullable Heightmap.Types heightmapType)
        {
            if (operation == Operation.AND)
            {
                andPredicates.add(predicate);
            }
            else if (operation == Operation.OR)
            {
                orPredicates.add(predicate);
            }
            else if (operation == Operation.REPLACE)
            {
                replacementPredicate = predicate;
                if (spawnType != null)
                {
                    this.spawnType = spawnType;
                }
                if (heightmapType != null)
                {
                    this.heightmapType = heightmapType;
                }
            }
        }

        @ApiStatus.Internal
        public SpawnPlacements.SpawnPredicate<T> build()
        {
            if (replacementPredicate != null)
            {
                return replacementPredicate;
            }
            final var original = originalPredicate;

            final SpawnPlacements.SpawnPredicate<T> compiledOrPredicate = (entityType, level, spawnType, pos, random) -> {
                if (original.test(entityType, level, spawnType, pos, random))
                {
                    return true;
                }
                for (SpawnPlacements.SpawnPredicate<T> predicate : orPredicates)
                {
                    if (predicate.test(entityType, level, spawnType, pos, random))
                    {
                        return true;
                    }
                }
                return false;
            };

            final SpawnPlacements.SpawnPredicate<T> compiledAndPredicate = (entityType, level, spawnType, pos, random) -> {
                if (!compiledOrPredicate.test(entityType, level, spawnType, pos, random))
                {
                    return false;
                }
                for (SpawnPlacements.SpawnPredicate<T> predicate : andPredicates)
                {
                    if (!predicate.test(entityType, level, spawnType, pos, random))
                    {
                        return false;
                    }
                }
                return true;
            };

            return compiledAndPredicate;
        }
    }

}
