/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fmlclient.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class RenderingRegistry
{
    private static final RenderingRegistry INSTANCE = new RenderingRegistry();

    private final Map<EntityType<? extends Entity>, EntityRendererProvider<? extends Entity>> entityRenderers = new ConcurrentHashMap<>();
    private final Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions = new ConcurrentHashMap<>();

    /**
     * Register an entity rendering handler. This will, after mod initialization, be inserted into the main
     * render map for entities.
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     */
    public static <T extends Entity> void registerEntityRenderingHandler(EntityType<T> entityClass, EntityRendererProvider<? super T> renderProvider)
    {
        INSTANCE.entityRenderers.put(entityClass, renderProvider);
    }

    /**
     * Registers a layer definition supplier with the given {@link ModelLayerLocation}. This will, after mod
     * initialization, be inserted into the main layer definition map for entity model layers.
     * <p>
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     * @param layerLocation The model layer location, which should be used in conjunction with {@link EntityRendererProvider.Context#bakeLayer(ModelLayerLocation)}
     * @param supplier A supplier to create a {@link LayerDefinition}, generally a static method reference in the entity model class
     */
    public static void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier)
    {
        INSTANCE.layerDefinitions.put(layerLocation, supplier);
    }

    public static void loadEntityRenderers()
    {
        INSTANCE.entityRenderers.forEach(RenderingRegistry::register);
        EntityRenderers.validateRegistrations();
    }

    public static void loadLayerDefinitions(ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder) {
        INSTANCE.layerDefinitions.forEach((k, v) -> builder.put(k, v.get()));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> void register(EntityType<T> entityType, EntityRendererProvider<?> renderProvider)
    {
        EntityRenderers.register(entityType, ((EntityRendererProvider<T>)renderProvider));
    }

    private static ModelLayerLocation registerModelLayer(ResourceLocation model, String layer)
    {
        return new ModelLayerLocation(model, layer);
    }

    /**
     * Registers a model layer location for the main layer definition of an entity.
     *
     * @param model The model {@link ResourceLocation}
     * @return A main model layer location
     */
    public static ModelLayerLocation registerMainModelLayer(ResourceLocation model)
    {
        return registerModelLayer(model, "main");
    }

    /**
     * Registers a model layer location for the inner armor layer definition of an entity.
     *
     * @param model The model {@link ResourceLocation}
     * @return An inner armor model layer location
     */
    public static ModelLayerLocation registerInnerArmorModelLayer(ResourceLocation model)
    {
        return registerModelLayer(model, "inner_armor");
    }

    /**
     * Registers a model layer location for the outer armor layer definition of an entity.
     *
     * @param model The model {@link ResourceLocation}
     * @return An outer armor model layer location
     */
    public static ModelLayerLocation registerOuterArmorModelLayer(ResourceLocation model)
    {
        return registerModelLayer(model, "outer_armor");
    }
}
