/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.Map;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Fired when the ModelManager is notified of the resource manager reloading.
 * Called after model registry is setup, but before it's passed to BlockModelShapes.
 */
// TODO: try to merge with ICustomModelLoader
public class ModelBakeEvent extends Event implements IModBusEvent
{
    private final ModelManager modelManager;
    private final Map<ResourceLocation, BakedModel> modelRegistry;
    private final ForgeModelBakery modelLoader;

    public ModelBakeEvent(ModelManager modelManager, Map<ResourceLocation, BakedModel> modelRegistry, ForgeModelBakery modelLoader)
    {
        this.modelManager = modelManager;
        this.modelRegistry = modelRegistry;
        this.modelLoader = modelLoader;
    }

    public ModelManager getModelManager()
    {
        return modelManager;
    }

    public Map<ResourceLocation, BakedModel> getModelRegistry()
    {
        return modelRegistry;
    }

    public ForgeModelBakery getModelLoader()
    {
        return modelLoader;
    }
}
