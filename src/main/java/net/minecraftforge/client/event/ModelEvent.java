/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Houses events related to models.
 */
public abstract class ModelEvent extends Event {
    @ApiStatus.Internal
    protected ModelEvent() { }

    /**
     * Fired while the {@link ModelManager} is reloading models, after the model registry is set up, but before it's
     * passed to the {@link net.minecraft.client.renderer.block.BlockModelShaper} for caching.
     *
     * <p>
     * This event is fired from a worker thread and it is therefore not safe to access anything outside the
     * model registry and {@link ModelBakery} provided in this event.<br>
     * The {@link ModelManager} firing this event is not fully set up with the latest data when this event fires and
     * must therefore not be accessed in this event.
     * </p>
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class ModifyBakingResult extends ModelEvent implements IModBusEvent {
        private final ModelBakery modelBakery;
        private final ModelBakery.BakingResult results;

        @ApiStatus.Internal
        public ModifyBakingResult(ModelBakery modelBakery, ModelBakery.BakingResult results) {
            this.modelBakery = modelBakery;
            this.results = results;
        }

        /**
         * @return the modifiable registry map of models and their model names
         */
        public ModelBakery.BakingResult getResults() {
            return results;
        }

        /**
         * @return the model loader
         */
        public ModelBakery getModelBakery() {
            return modelBakery;
        }
    }

    /**
     * Fired when the {@link ModelManager} is notified of the resource manager reloading.
     * Called after the model registry is set up and cached in the {@link net.minecraft.client.renderer.block.BlockModelShaper}.<br>
     * The model registry given by this event is unmodifiable. To modify the model registry, use
     * {@link ModelEvent.ModifyBakingResult} instead.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class BakingCompleted extends ModelEvent implements IModBusEvent {
        private final ModelManager modelManager;
        private final ModelBakery modelBakery;

        @ApiStatus.Internal
        public BakingCompleted(ModelManager modelManager, ModelBakery modelBakery) {
            this.modelManager = modelManager;
            this.modelBakery = modelBakery;
        }

        /**
         * @return the model manager
         */
        public ModelManager getModelManager() {
            return modelManager;
        }

        /**
         * @return the model loader
         */
        public ModelBakery getModelBakery() {
            return modelBakery;
        }
    }

    /**
     * Fired when the {@link net.minecraft.client.resources.model.BlockStateModelLoader BlockStateModelLoader} is notified of the resource manager reloading.
     * Allows developers to register {@link net.minecraft.world.level.block.state.StateDefinition StateDefinitons} for blocks that are not in the normal Block registry.
     * This is designed to allow for extra models to be loaded in connection with a blockstates json file. This is not intended to allow
     * overriding or modification of StateDefinitions from registered Blocks
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class RegisterModelStateDefinitions extends ModelEvent implements IModBusEvent {
        private final Map<ResourceLocation, StateDefinition<Block, BlockState>> states = new HashMap<>();
        private final Map<ResourceLocation, StateDefinition<Block, BlockState>> view = Collections.unmodifiableMap(states);

        @ApiStatus.Internal
        public RegisterModelStateDefinitions() { }

        /**
         * Returns a read only view of the extra registered models
         */
        public Map<ResourceLocation, StateDefinition<Block, BlockState>> getStates() {
            return this.view;
        }

        /**
         * Registers a StateDefinition for a synthetic block.
         */
        public void register(ResourceLocation key, StateDefinition<Block, BlockState> value) {
            states.put(key, value);
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
    public static class RegisterGeometryLoaders extends ModelEvent implements IModBusEvent {
        private final Map<ResourceLocation, IGeometryLoader<?>> loaders;

        @ApiStatus.Internal
        public RegisterGeometryLoaders(Map<ResourceLocation, IGeometryLoader<?>> loaders) {
            this.loaders = loaders;
        }

        /**
         * Registers a new geometry loader.
         */
        public void register(String name, IGeometryLoader<?> loader) {
            @SuppressWarnings("removal")
            var namespace = ModLoadingContext.get().getActiveNamespace();
            var key = ResourceLocation.fromNamespaceAndPath(namespace, name);
            Preconditions.checkArgument(!loaders.containsKey(key), "Geometry loader already registered: " + key);
            loaders.put(key, loader);
        }
    }
}
