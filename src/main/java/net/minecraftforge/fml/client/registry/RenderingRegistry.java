/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.client.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public class RenderingRegistry
{
    private static final RenderingRegistry INSTANCE = new RenderingRegistry();

    private final Map<EntityType<? extends Entity>, IRenderFactory<? extends Entity>> entityRenderers = new ConcurrentHashMap<>();

    /**
     * Register an entity rendering handler. This will, after mod initialization, be inserted into the main
     * render map for entities.
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     */
    public static <T extends Entity> void registerEntityRenderingHandler(EntityType<T> entityClass, IRenderFactory<? super T> renderFactory)
    {
        INSTANCE.entityRenderers.put(entityClass, renderFactory);
    }

    public static void loadEntityRenderers(EntityRendererManager manager)
    {
        INSTANCE.entityRenderers.forEach((key, value) -> register(manager, key, value));
        manager.validateRendererExistence();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> void register(EntityRendererManager manager, EntityType<T> entityType, IRenderFactory<?> renderFactory)
    {
        manager.register(entityType, ((IRenderFactory<T>)renderFactory).createRenderFor(manager));
    }
}
