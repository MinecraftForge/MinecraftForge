/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import java.util.function.Predicate;

import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

@Deprecated // Use the new model loading system and data generators instead.
public interface ICustomModelLoader extends ISelectiveResourceReloadListener
{
    @Override
    void onResourceManagerReload(IResourceManager resourceManager);

    @Override
    default void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
    {
        if (resourcePredicate.test(VanillaResourceType.MODELS))
        {
            onResourceManagerReload(resourceManager);
        }
    }

    /**
     * Checks if given model should be loaded by this loader.
     * Reading file contents is inadvisable, if possible decision should be made based on the location alone.
     * @param modelLocation The path, either to an actual file or a {@link net.minecraft.client.renderer.model.ModelResourceLocation}.
     */
    boolean accepts(ResourceLocation modelLocation);

    /**
     * @param modelLocation The model to (re)load, either path to an
     *                      actual file or a {@link net.minecraft.client.renderer.model.ModelResourceLocation}.
     */
    IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception;


    @Override
    default IResourceType getResourceType()
    {
       return VanillaResourceType.MODELS;
    }
}
