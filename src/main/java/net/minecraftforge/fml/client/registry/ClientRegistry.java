/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.client.registry;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.tileentity.TileEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ClientRegistry
{
    private static Map<Class<? extends Entity>, ResourceLocation> entityShaderMap = new ConcurrentHashMap<>();

    /**
     * Registers a Tile Entity renderer.
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     */
    public static synchronized <T extends TileEntity> void bindTileEntityRenderer(TileEntityType<T> tileEntityType,
            Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>> rendererFactory)
    {
        TileEntityRendererDispatcher.instance.setSpecialRendererInternal(tileEntityType, rendererFactory.apply(TileEntityRendererDispatcher.instance));
    }

    /**
     * Registers a KeyBinding.
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     */
    public static synchronized void registerKeyBinding(KeyBinding key)
    {
        Minecraft.getInstance().gameSettings.keyBindings = ArrayUtils.add(Minecraft.getInstance().gameSettings.keyBindings, key);
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
