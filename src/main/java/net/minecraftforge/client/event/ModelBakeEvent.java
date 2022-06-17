/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.Map;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Fired when the ModelManager is notified of the resource manager reloading.
 * Called after model registry is setup, but before it's passed to BlockModelShapes.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
 *
 * <p>These events are fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 */
// TODO: try to merge with ICustomModelLoader
public class ModelBakeEvent extends Event implements IModBusEvent
{
    private final ModelManager modelManager;
    private final Map<ResourceLocation, BakedModel> modelRegistry;
    private final ForgeModelBakery modelLoader;

    /**
     * @hidden
     * @see ForgeHooksClient#onModelBake(ModelManager, Map, ForgeModelBakery)
     */
    public ModelBakeEvent(ModelManager modelManager, Map<ResourceLocation, BakedModel> modelRegistry, ForgeModelBakery modelLoader)
    {
        this.modelManager = modelManager;
        this.modelRegistry = modelRegistry;
        this.modelLoader = modelLoader;
    }

    /**
     * {@return the model manager}
     */
    public ModelManager getModelManager()
    {
        return modelManager;
    }

    /**
     * {@return the modifiable registry map of models and their model names}
     */
    public Map<ResourceLocation, BakedModel> getModelRegistry()
    {
        return modelRegistry;
    }

    /**
     * {@return the model loader}
     */
    public ForgeModelBakery getModelLoader()
    {
        return modelLoader;
    }
}
