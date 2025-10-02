/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.RegisterEntitySpectatorShadersEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for entity spectator mode shaders.
 * <p>
 * Provides a lookup.
 */
@NullMarked
public final class EntitySpectatorShaderManager {
    private EntitySpectatorShaderManager() {}

    private static @Nullable Map<EntityType<?>, ResourceLocation> SHADERS;

    /**
     * Finds the path to the spectator mode shader used for the specified entity type, or null if none is registered.
     */
    public static @Nullable ResourceLocation get(EntityType<?> entityType) {
        assert SHADERS != null;
        return SHADERS.get(entityType);
    }

    @ApiStatus.Internal
    public static void init() {
        var shaders = new HashMap<EntityType<?>, ResourceLocation>();
        RegisterEntitySpectatorShadersEvent.BUS.post(new RegisterEntitySpectatorShadersEvent(shaders));
        SHADERS = Map.copyOf(shaders);
    }
}
