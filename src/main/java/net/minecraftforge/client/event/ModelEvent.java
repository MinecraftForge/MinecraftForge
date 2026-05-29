/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import com.google.common.base.Preconditions;

import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraftsingularity.client.model.geometry.IGeometryLoader;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.fml.LogicalSide;
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

        private final Map<Identifier, StateDefinition<Block, BlockState>> states = new HashMap<>();
        private final Map<Identifier, StateDefinition<Block, BlockState>> view = Collections.unmodifiableMap(states);

        @ApiStatus.Internal
        public RegisterModelStateDefinitions() {}

        /**
         * Returns a read only view of the extra registered models
         */
        public Map<Identifier, StateDefinition<Block, BlockState>> getStates() {
            return this.view;
        }

        /**
         * Registers a StateDefinition for a synthetic block.
         */
        public void register(Identifier key, StateDefinition<Block, BlockState> value) {
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

        private final Map<Identifier, IGeometryLoader> loaders;

        @ApiStatus.Internal
        public RegisterGeometryLoaders(Map<Identifier, IGeometryLoader> loaders) {
            this.loaders = loaders;
        }

        /**
         * Registers a new geometry loader.
         * @param Identifier The namespace should match your mod's namespace, such as your mod ID
         */
        public void register(Identifier Identifier, IGeometryLoader loader) {
            Preconditions.checkArgument(!loaders.containsKey(Identifier), "Geometry loader already registered: " + Identifier);
            loaders.put(Identifier, loader);
        }
    }

    /**
     * Allows users to register their own {@link FluidModel fluid models}.
     *
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @NullMarked
    final class BakeFluidModels extends MutableEvent implements ModelEvent {
        public static final EventBus<BakeFluidModels> BUS = EventBus.create(BakeFluidModels.class);

        private final ModelBakery bakery;
        private final MaterialBaker materials;
        private final Map<Fluid, FluidModel> models;

        @ApiStatus.Internal
        public BakeFluidModels(ModelBakery bakery, MaterialBaker materials, Map<Fluid, FluidModel> models) {
            this.bakery = bakery;
            this.materials = materials;
            this.models = models;
        }

        public ModelBakery bakery() {
            return this.bakery;
        }

        public MaterialBaker materials() {
            return this.materials;
        }

        public void register(Fluid fluid, FluidModel model) {
            models.put(fluid, model);
        }
    }
}
