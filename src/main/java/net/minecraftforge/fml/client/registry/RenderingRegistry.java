/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */
package net.minecraftforge.fml.client.registry;

import java.util.Map;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import com.google.common.collect.Maps;

public class RenderingRegistry
{
    private static final RenderingRegistry INSTANCE = new RenderingRegistry();

    private Map<Class<? extends Entity>, IRenderFactory<? extends Entity>> entityRenderers = Maps.newHashMap();
    private Map<Class<? extends Entity>, Render<? extends Entity>> entityRenderersOld = Maps.newHashMap();

    /**
     * Register an entity rendering handler. This will, after mod initialization, be inserted into the main
     * render map for entities.
     * Call this during Initialization phase.
     *
     * @deprecated use the factory version during Preinitialization.
     * Will be removed in 1.9.
     */
    @Deprecated
    public static void registerEntityRenderingHandler(Class<? extends Entity> entityClass, Render<? extends Entity> renderer)
    {
        INSTANCE.entityRenderersOld.put(entityClass, renderer);
    }

    public static void loadEntityRenderers(Map<Class<? extends Entity>, Render<? extends Entity>> entityRenderMap)
    {
        entityRenderMap.putAll(INSTANCE.entityRenderersOld);
    }

    /**
     * Register an entity rendering handler. This will, after mod initialization, be inserted into the main
     * render map for entities.
     * Call this during Preinitialization phase.
     *
     * @param entityClass
     * @param renderer
     */
    public static <T extends Entity> void registerEntityRenderingHandler(Class<T> entityClass, IRenderFactory<? super T> renderFactory)
    {
        INSTANCE.entityRenderers.put(entityClass, renderFactory);
    }

    public static void loadEntityRenderers(RenderManager manager, Map<Class<? extends Entity> , Render<? extends Entity>> renderMap)
    {
        for (Map.Entry<Class<? extends Entity>, IRenderFactory<? extends Entity>> entry : INSTANCE.entityRenderers.entrySet())
        {
            register(manager, renderMap, entry.getKey(), entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> void register(RenderManager manager, Map<Class<? extends Entity> , Render<? extends Entity>> renderMap, Class<T> entityClass, IRenderFactory<?> renderFactory)
    {
        renderMap.put(entityClass, ((IRenderFactory<T>)renderFactory).createRenderFor(manager));
    }
}
