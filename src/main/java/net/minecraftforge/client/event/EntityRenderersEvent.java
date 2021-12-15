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

package net.minecraftforge.client.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.animation.EntityAnimation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityRenderersEvent extends Event implements IModBusEvent
{
    public static class RegisterLayerDefinitions extends EntityRenderersEvent
    {
        /**
         * Registers a layer definition supplier with the given {@link ModelLayerLocation}.
         * These will be inserted into the main layer definition map for entity model layers at the appropriate time.
         *
         * @param layerLocation The model layer location, which should be used in conjunction with {@link EntityRendererProvider.Context#bakeLayer(ModelLayerLocation)}
         * @param supplier A supplier to create a {@link LayerDefinition}, generally a static method reference in the entity model class
         */
        public void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier)
        {
            ForgeHooksClient.registerLayerDefinition(layerLocation, supplier);
        }
    }

    /**
     * Provides a safe place to register entity renderers and layer renderers.
     */
    public static class RegisterRenderers extends EntityRenderersEvent
    {
        /**
         * Registers an entity renderer.
         *
         * @param entityType The EntityType to register a renderer for
         * @param entityRendererProvider The renderer provider, can be a lambda like MyRenderer::new
         */
        public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider)
        {
            EntityRenderers.register(entityType, entityRendererProvider);
        }

        /**
         * Registers a block entity renderer.
         *
         * @param blockEntityType The BlockEntityType to register a renderer for
         * @param blockEntityRendererProvider The renderer provider, can be a lambda like MyRenderer::new
         */
        public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider)
        {
            BlockEntityRenderers.register(blockEntityType, blockEntityRendererProvider);
        }
    }

    /**
     * Provides a safe place to register entity renderer layers.
     */
    public static class AddLayers extends EntityRenderersEvent
    {
        private final Map<EntityType<?>, EntityRenderer<?>> renderers;
        private final Map<String, EntityRenderer<? extends Player>> skinMap;
        private final EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();

        public AddLayers(Map<EntityType<?>, EntityRenderer<?>> renderers, Map<String, EntityRenderer<? extends Player>> playerRenderers)
        {
            this.renderers = renderers;
            this.skinMap = playerRenderers;
        }

        /**
         * Returns the list of skins.
         */
        public Set<String> getSkins()
        {
            return skinMap.keySet();
        }

        /**
         * Returns a player skin renderer. Vanilla skins are "default" and "slim".
         * @param skinName The name of the skin renderer to return
         * @param <R> The type of the skin renderer, usually PlayerRenderer
         * @return The skin renderer, or null if not found
         */
        @Nullable
        @SuppressWarnings("unchecked")
        public <R extends LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>>> R getSkin(String skinName)
        {
            return (R) skinMap.get(skinName);
        }

        /**
         * Returns an entity renderer for the given entity type.
         * @param entityType The entity to return a renderer for
         * @param <T> The type of entity the renderer is for
         * @param <R> The renderer type
         * @return The renderer
         */
        @Nullable
        @SuppressWarnings("unchecked")
        public <T extends LivingEntity, R extends LivingEntityRenderer<T, ? extends EntityModel<T>>> R getRenderer(EntityType<? extends T> entityType) {
            return (R) renderers.get(entityType);
        }

        public EntityModelSet getEntityModels()
        {
            return entityModels;
        }
    }

    /**
     * An event to provide a safe place to register entity animations
     */
    public static class AddAnimations extends EntityRenderersEvent
    {
        private final Multimap<EntityType<?>, Consumer<LivingEntityRenderer<?, ?>>> animations = ArrayListMultimap.create();

        /**
         * Prepares an animation to be added to the given entity type. Animations are registered when
         * the resource manager is reloading.
         *
         * @param type the entity type
         * @param animation an animation implementation that matches the entity type
         * @param <T> an entity that extends living entity
         */
        @SuppressWarnings("unchecked")
        public <T extends LivingEntity> void addAnimation(EntityType<T> type, EntityAnimation<T> animation)
        {
            this.animations.put(type, renderer -> ((LivingEntityRenderer<T, ?>) renderer).getAnimator().addAnimation(animation));
        }

        /**
         * Gets a map of the registered animations. Used internally.
         */
        public Multimap<EntityType<?>, Consumer<LivingEntityRenderer<?, ?>>> getAnimations()
        {
            return ImmutableListMultimap.copyOf(animations);
        }
    }
}
