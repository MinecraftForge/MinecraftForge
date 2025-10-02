/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.LogicalSide;
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

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<RegisterEntitySpectatorShadersEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

    private final Map<EntityType<?>, ResourceLocation> shaders;

    @ApiStatus.Internal
    public RegisterEntitySpectatorShadersEvent(Map<EntityType<?>, ResourceLocation> shaders) {
        this.shaders = shaders;
    }

    /**
     * Registers a spectator shader for a given entity type.
     */
    public void register(EntityType<?> entityType, ResourceLocation shader) {
        shaders.put(entityType, shader);
    }
}
