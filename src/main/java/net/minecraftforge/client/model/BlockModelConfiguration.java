/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;

public class BlockModelConfiguration implements IModelConfiguration
{
    public final BlockModel owner;
    public final VisibilityData visibilityData = new VisibilityData();
    @Nullable
    private IModelGeometry<?> customGeometry;
    @Nullable
    private ModelState customModelState;

    public BlockModelConfiguration(BlockModel owner)
    {
        this.owner = owner;
    }

    @Nullable
    @Override
    public UnbakedModel getOwnerModel()
    {
        return owner;
    }

    @Override
    public String getModelName()
    {
        return owner.name;
    }

    public boolean hasCustomGeometry()
    {
        return getCustomGeometry() != null;
    }

    @Nullable
    public IModelGeometry<?> getCustomGeometry()
    {
        return owner.parent != null && customGeometry == null ? owner.parent.customData.getCustomGeometry() : customGeometry;
    }

    public void setCustomGeometry(IModelGeometry<?> geometry)
    {
        this.customGeometry = geometry;
    }

    @Nullable
    public ModelState getCustomModelState()
    {
        return owner.parent != null && customModelState == null ? owner.parent.customData.getCustomModelState() : customModelState;
    }

    public void setCustomModelState(ModelState modelState)
    {
        this.customModelState = modelState;
    }

    @Override
    public boolean getPartVisibility(IModelGeometryPart part, boolean fallback)
    {
        return owner.parent != null && !visibilityData.hasCustomVisibility(part) ?
                owner.parent.customData.getPartVisibility(part, fallback):
                visibilityData.isVisible(part, fallback);
    }

    @Override
    public boolean isTexturePresent(String name)
    {
        return owner.hasTexture(name);
    }

    @Override
    public Material resolveTexture(String name)
    {
        return owner.getMaterial(name);
    }

    @Override
    public boolean isShadedInGui() {
        return true;
    }

    @Override
    public boolean isSideLit()
    {
        return owner.getGuiLight().lightLikeBlock();
    }

    @Override
    public boolean useSmoothLighting()
    {
        return owner.hasAmbientOcclusion();
    }

    @Override
    public ItemTransforms getCameraTransforms()
    {
        return owner.getTransforms();
    }

    @Override
    public ModelState getCombinedTransform()
    {
        ModelState state = getCustomModelState();

        return state != null
                ? new SimpleModelState(PerspectiveMapWrapper.getTransformsWithFallback(state, getCameraTransforms()), state.getRotation())
                : new SimpleModelState(PerspectiveMapWrapper.getTransforms(getCameraTransforms()));
    }

    public void copyFrom(BlockModelConfiguration other)
    {
        this.customGeometry = other.customGeometry;
        this.customModelState = other.customModelState;
        this.visibilityData.copyFrom(other.visibilityData);
    }

    public Collection<Material> getTextureDependencies(Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        IModelGeometry<?> geometry = getCustomGeometry();
        return geometry == null ? Collections.emptySet() :
                geometry.getTextures(this, modelGetter, missingTextureErrors);
    }

    public BakedModel bake(ModelBakery bakery, Function<Material, TextureAtlasSprite> bakedTextureGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        IModelGeometry<?> geometry = getCustomGeometry();
        if (geometry == null)
            throw new IllegalStateException("Can not use custom baking without custom geometry");
        return geometry.bake(this, bakery, bakedTextureGetter, modelTransform, overrides, modelLocation);
    }

    public static class VisibilityData
    {
        private final Map<String, Boolean> data = new HashMap<>();

        public boolean hasCustomVisibility(IModelGeometryPart part)
        {
            return data.containsKey(part.name());
        }

        public boolean isVisible(IModelGeometryPart part, boolean fallback)
        {
            return data.getOrDefault(part.name(), fallback);
        }

        public void setVisibilityState(String partName, boolean type)
        {
            data.put(partName, type);
        }

        public void copyFrom(VisibilityData visibilityData)
        {
            data.clear();
            data.putAll(visibilityData.data);
        }
    }
}
