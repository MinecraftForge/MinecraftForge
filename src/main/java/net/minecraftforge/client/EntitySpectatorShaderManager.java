/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.RegisterEntitySpectatorShadersEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for entity spectator mode shaders.
 * <p>
 * Provides a lookup.
 */
public final class EntitySpectatorShaderManager
{
    private static Map<EntityType<?>, ResourceLocation> SHADERS;

    /**
     * Finds the path to the spectator mode shader used for the specified entity type, or null if none is registered.
     */
    @Nullable
    public static ResourceLocation get(EntityType<?> entityType)
    {
        return SHADERS.get(entityType);
    }

    @ApiStatus.Internal
    public static void init()
    {
        var shaders = new HashMap<EntityType<?>, ResourceLocation>();
        var event = new RegisterEntitySpectatorShadersEvent(shaders);
        ModLoader.get().postEventWithWrapInModOrder(event, (mc, e) -> ModLoadingContext.get().setActiveContainer(mc), (mc, e) -> ModLoadingContext.get().setActiveContainer(null));
        SHADERS = ImmutableMap.copyOf(shaders);
    }

    private EntitySpectatorShaderManager()
    {
    }
}
