/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

/**
 * Allows users to register custom shaders to be used when the player spectates a certain kind of entity.
 * Vanilla examples of this are the green effect for creepers and the invert effect for endermen.
 *
 * <p>This event is fired on only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
@NullMarked
public final class RegisterEntitySpectatorShadersEvent extends MutableEvent implements SelfDestructing {
    public static final EventBus<RegisterEntitySpectatorShadersEvent> BUS = EventBus.create(RegisterEntitySpectatorShadersEvent.class);

    private final Map<EntityType<?>, Identifier> shaders;

    @ApiStatus.Internal
    public RegisterEntitySpectatorShadersEvent(Map<EntityType<?>, Identifier> shaders) {
        this.shaders = shaders;
    }

    /**
     * Registers a spectator shader for a given entity type.
     */
    public void register(EntityType<?> entityType, Identifier shader) {
        shaders.put(entityType, shader);
    }
}
