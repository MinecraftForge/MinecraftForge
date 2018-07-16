/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
     * TODO Will be removed in 1.11.
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
