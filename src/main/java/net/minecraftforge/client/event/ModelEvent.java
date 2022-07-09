/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Set;

/**
 * Houses events related to models.
 */
public abstract class ModelEvent extends Event
{
    @ApiStatus.Internal
    protected ModelEvent()
    {
    }

    /**
     * Fired when the {@link ModelManager} is notified of the resource manager reloading.
     * Called after model registry is set up, but before it's passed to {@link net.minecraft.client.renderer.block.BlockModelShaper}.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class BakingCompleted extends ModelEvent implements IModBusEvent
    {
        private final ModelManager modelManager;
        private final Map<ResourceLocation, BakedModel> models;
        private final ModelBakery modelBakery;

        @ApiStatus.Internal
        public BakingCompleted(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery)
        {
            this.modelManager = modelManager;
            this.models = models;
            this.modelBakery = modelBakery;
        }

        /**
         * @return the model manager
         */
        public ModelManager getModelManager()
        {
            return modelManager;
        }

        /**
         * @return the modifiable registry map of models and their model names
         */
        public Map<ResourceLocation, BakedModel> getModels()
        {
            return models;
        }

        /**
         * @return the model loader
         */
        public ModelBakery getModelBakery()
        {
            return modelBakery;
        }
    }

    /**
     * Fired when the {@link net.minecraft.client.resources.model.ModelBakery} is notified of the resource manager reloading.
     * Allows developers to register models to be loaded, along with their dependencies.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class RegisterAdditional extends ModelEvent implements IModBusEvent
    {
        private final Set<ResourceLocation> models;

        @ApiStatus.Internal
        public RegisterAdditional(Set<ResourceLocation> models)
        {
            this.models = models;
        }

        /**
         * Registers a model to be loaded, along with its dependencies.
         */
        public void register(ResourceLocation model)
        {
            models.add(model);
        }
    }

    /**
     * Allows users to register their own {@link IGeometryLoader geometry loaders} for use in block/item models.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class RegisterGeometryLoaders extends ModelEvent implements IModBusEvent
    {
        private final Map<ResourceLocation, IGeometryLoader<?>> loaders;

        @ApiStatus.Internal
        public RegisterGeometryLoaders(Map<ResourceLocation, IGeometryLoader<?>> loaders)
        {
            this.loaders = loaders;
        }

        /**
         * Registers a new geometry loader.
         */
        public void register(String name, IGeometryLoader<?> loader)
        {
            var key = new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name);
            Preconditions.checkArgument(!loaders.containsKey(key), "Geometry loader already registered: " + key);
            loaders.put(key, loader);
        }
    }
}
