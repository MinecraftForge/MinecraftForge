/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

import org.jetbrains.annotations.ApiStatus;

/**
 * This event is fired when a {@link EntityType} has a {@link SpawnPlacements.SpawnPredicate} registered.
 * Spawn Predicates are checked whenever an {@link Entity} of the given {@link EntityType} spawns in the world naturally.
 *
 * This event is not for registering spawn placements. It is for modifying the placements of entities from vanilla or other mods.
 * For any {@link EntityType} that you construct yourself, instead call {@link SpawnPlacements#register(EntityType, SpawnPlacements.Type, Heightmap.Types, SpawnPlacements.SpawnPredicate)} in {@link FMLCommonSetupEvent} using {@link ParallelDispatchEvent#enqueueWork(Runnable)}
 *
 * <p>
 * This event is {@linkplain Cancelable cancellable} and does not {@linkplain Event.HasResult have a result}.
 * Cancelling this event causes the predicate and data specified in the event to be registered in place of the original data.
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
 * only on the {@linkplain LogicalSide#SERVER logical server}.
 */
@Cancelable
public class SpawnPlacementRegisterEvent extends Event
{
    private final EntityType<?> entityType;
    private SpawnPlacements.Type placementType;
    private Heightmap.Types heightmap;
    private SpawnPlacements.SpawnPredicate<Entity> predicate;

    @ApiStatus.Internal
    public SpawnPlacementRegisterEvent(EntityType<?> entityType, SpawnPlacements.Type placementType, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<Entity> predicate)
    {
        this.entityType = entityType;
        this.placementType = placementType;
        this.heightmap = heightmap;
        this.predicate = predicate;
    }

    public EntityType<?> getEntityType()
    {
        return entityType;
    }

    /**
     * @return The placement type being used. This broadly restricts what environment the {@link EntityType} spawns in.
     */
    public SpawnPlacements.Type getPlacementType()
    {
        return placementType;
    }

    /**
     * @param placementType The new placement type to be used.
     */
    public void setPlacementType(SpawnPlacements.Type placementType)
    {
        this.placementType = placementType;
    }

    /**
     * @return The heightmap the entity will use for spawning.
     */
    public Heightmap.Types getHeightmap()
    {
        return heightmap;
    }

    /**
     * @param heightmap The new heightmap to be used.
     */
    public void setHeightmap(Heightmap.Types heightmap)
    {
        this.heightmap = heightmap;
    }

    /**
     * @return The current spawn predicate being registered.
     */
    public SpawnPlacements.SpawnPredicate<Entity> getPredicate()
    {
        return predicate;
    }

    public void setPredicate(SpawnPlacements.SpawnPredicate<Entity> predicate)
    {
        this.predicate = predicate;
    }

    /**
     * Sets the {@link SpawnPlacements.SpawnPredicate} to require both the passed in predicate and the event's current predicate to pass.
     * @param predicate The second predicate to check
     */
    public void requireSecondPredicate(SpawnPlacements.SpawnPredicate<Entity> predicate)
    {
        var originalPredicate = this.predicate;
        final SpawnPlacements.SpawnPredicate<Entity> newPredicate = (entityType, level, spawnType, pos, random) -> {
            return predicate.test(entityType, level, spawnType, pos, random) && originalPredicate.test(entityType, level, spawnType, pos, random);
        };
        setPredicate(newPredicate);
    }

}
