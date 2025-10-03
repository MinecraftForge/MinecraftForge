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
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Houses events related to models.
 */
public sealed interface ModelEvent {
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
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @param getModelBakery the model loader
     * @param getResults the modifiable registry map of models and their model names
     */
    record ModifyBakingResult(ModelBakery getModelBakery, ModelBakery.BakingResult getResults)
            implements RecordEvent, ModelEvent {
        public static final EventBus<ModifyBakingResult> BUS = EventBus.create(ModifyBakingResult.class);

        @Deprecated(forRemoval = true, since = "1.21.9")
        public static EventBus<ModifyBakingResult> getBus(BusGroup modBusGroup) {
            return BUS;
        }

        @ApiStatus.Internal
        public ModifyBakingResult {}
    }

    /**
     * Fired when the {@link ModelManager} is notified of the resource manager reloading.
     * Called after the model registry is set up and cached in the {@link net.minecraft.client.renderer.block.BlockModelShaper}.<br>
     * The model registry given by this event is unmodifiable. To modify the model registry, use
     * {@link ModelEvent.ModifyBakingResult} instead.
     *
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @param getModelManager the model manager
     * @param getModelBakery the model loader
     */
    record BakingCompleted(ModelManager getModelManager, ModelBakery getModelBakery)
            implements RecordEvent, ModelEvent {
        public static final EventBus<BakingCompleted> BUS = EventBus.create(BakingCompleted.class);

        @Deprecated(forRemoval = true, since = "1.21.9")
        public static EventBus<BakingCompleted> getBus(BusGroup modBusGroup) {
            return BUS;
        }

        @ApiStatus.Internal
        public BakingCompleted {}
    }

    /**
     * Fired when the {@link net.minecraft.client.resources.model.BlockStateModelLoader BlockStateModelLoader} is notified of the resource manager reloading.
     * Allows developers to register {@link net.minecraft.world.level.block.state.StateDefinition StateDefinitons} for blocks that are not in the normal Block registry.
     * This is designed to allow for extra models to be loaded in connection with a blockstates json file. This is not intended to allow
     * overriding or modification of StateDefinitions from registered Blocks
     *
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    final class RegisterModelStateDefinitions extends MutableEvent implements ModelEvent {
        public static final EventBus<RegisterModelStateDefinitions> BUS = EventBus.create(RegisterModelStateDefinitions.class);

        @Deprecated(forRemoval = true, since = "1.21.9")
        public static EventBus<RegisterModelStateDefinitions> getBus(BusGroup modBusGroup) {
            return BUS;
        }

        private final Map<ResourceLocation, StateDefinition<Block, BlockState>> states = new HashMap<>();
        private final Map<ResourceLocation, StateDefinition<Block, BlockState>> view = Collections.unmodifiableMap(states);

        @ApiStatus.Internal
        public RegisterModelStateDefinitions() {}

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
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @NullMarked
    final class RegisterGeometryLoaders extends MutableEvent implements ModelEvent {
        public static final EventBus<RegisterGeometryLoaders> BUS = EventBus.create(RegisterGeometryLoaders.class);

        @Deprecated(forRemoval = true, since = "1.21.9")
        public static EventBus<RegisterGeometryLoaders> getBus(BusGroup modBusGroup) {
            return BUS;
        }

        private final Map<ResourceLocation, IGeometryLoader> loaders;

        @ApiStatus.Internal
        public RegisterGeometryLoaders(Map<ResourceLocation, IGeometryLoader> loaders) {
            this.loaders = loaders;
        }

        /**
         * Registers a new geometry loader.
         * @param resourceLocation The namespace should match your mod's namespace, such as your mod ID
         */
        public void register(ResourceLocation resourceLocation, IGeometryLoader loader) {
            Preconditions.checkArgument(!loaders.containsKey(resourceLocation), "Geometry loader already registered: " + resourceLocation);
            loaders.put(resourceLocation, loader);
        }
    }
}
