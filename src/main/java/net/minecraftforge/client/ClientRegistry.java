/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry
{
    private static Map<Class<? extends Entity>, ResourceLocation> entityShaderMap = new ConcurrentHashMap<>();

    /**
     * Registers a KeyBinding.
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     */
    public static synchronized void registerKeyBinding(KeyMapping key)
    {
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, key);
    }

    /**
     * Register a shader for an entity. This shader gets activated when a spectator begins spectating an entity.
     * Vanilla examples of this are the green effect for creepers and the invert effect for endermen.
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     */
    public static void registerEntityShader(Class<? extends Entity> entityClass, ResourceLocation shader)
    {
        entityShaderMap.put(entityClass, shader);
    }

    public static ResourceLocation getEntityShader(Class<? extends Entity> entityClass)
    {
        return entityShaderMap.get(entityClass);
    }
}
