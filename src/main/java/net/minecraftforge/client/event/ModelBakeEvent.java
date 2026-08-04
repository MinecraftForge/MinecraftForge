/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.Map;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when the ModelManager is notified of the resource manager reloading.
 * Called after model registry is setup, but before it's passed to BlockModelShapes.
 */
// TODO: try to merge with ICustomModelLoader
public class ModelBakeEvent extends Event
{
    private final ModelManager modelManager;
    private final Map<ResourceLocation, IBakedModel> modelRegistry;
    private final ModelLoader modelLoader;

    public ModelBakeEvent(ModelManager modelManager, Map<ResourceLocation, IBakedModel> modelRegistry, ModelLoader modelLoader)
    {
        this.modelManager = modelManager;
        this.modelRegistry = modelRegistry;
        this.modelLoader = modelLoader;
    }

    public ModelManager getModelManager()
    {
        return modelManager;
    }

    public Map<ResourceLocation, IBakedModel> getModelRegistry()
    {
        return modelRegistry;
    }

    public ModelLoader getModelLoader()
    {
        return modelLoader;
    }
}
