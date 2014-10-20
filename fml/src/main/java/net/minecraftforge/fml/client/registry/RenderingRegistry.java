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

import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import com.google.common.collect.Lists;

/**
 * @author cpw
 *
 */
public class RenderingRegistry
{
    private static final RenderingRegistry INSTANCE = new RenderingRegistry();

    private List<EntityRendererInfo> entityRenderers = Lists.newArrayList();

    /**
     * Register an entity rendering handler. This will, after mod initialization, be inserted into the main
     * render map for entities
     *
     * @param entityClass
     * @param renderer
     */
    public static void registerEntityRenderingHandler(Class<? extends Entity> entityClass, Render renderer)
    {
        INSTANCE.entityRenderers.add(new EntityRendererInfo(entityClass, renderer));
    }

    private static class EntityRendererInfo
    {
        public EntityRendererInfo(Class<? extends Entity> target, Render renderer)
        {
            this.target = target;
            this.renderer = renderer;
        }
        private Class<? extends Entity> target;
        private Render renderer;
    }

    public static void loadEntityRenderers(Map<Class<? extends Entity>, Render> rendererMap)
    {
        for (EntityRendererInfo info : INSTANCE.entityRenderers)
        {
            rendererMap.put(info.target, info.renderer);
        }
    }
}
