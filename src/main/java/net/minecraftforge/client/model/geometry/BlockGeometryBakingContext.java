/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@linkplain IGeometryBakingContext geometry baking context} that is bound to a {@link BlockModel}.
 * <p>
 * Users should not be instantiating this themselves.
 */
public class BlockGeometryBakingContext implements IGeometryBakingContext
{
    public final BlockModel owner;
    public final VisibilityData visibilityData = new VisibilityData();
    @Nullable
    private IUnbakedGeometry<?> customGeometry;
    @Nullable
    private Transformation rootTransform;
    @Nullable
    private ResourceLocation renderTypeHint;

    @ApiStatus.Internal
    public BlockGeometryBakingContext(BlockModel owner)
    {
        this.owner = owner;
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
    public IUnbakedGeometry<?> getCustomGeometry()
    {
        return owner.parent != null && customGeometry == null ? owner.parent.customData.getCustomGeometry() : customGeometry;
    }

    public void setCustomGeometry(IUnbakedGeometry<?> geometry)
    {
        this.customGeometry = geometry;
    }

    @Override
    public boolean isComponentVisible(String part, boolean fallback)
    {
        return owner.parent != null && !visibilityData.hasCustomVisibility(part) ?
                owner.parent.customData.isComponentVisible(part, fallback) :
                visibilityData.isVisible(part, fallback);
    }

    @Override
    public boolean hasMaterial(String name)
    {
        return owner.hasTexture(name);
    }

    @Override
    public Material getMaterial(String name)
    {
        return owner.getMaterial(name);
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    @Override
    public boolean useBlockLight()
    {
        return owner.getGuiLight().lightLikeBlock();
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return owner.hasAmbientOcclusion();
    }

    @Override
    public ItemTransforms getTransforms()
    {
        return owner.getTransforms();
    }

    @Override
    public Transformation getRootTransform()
    {
        if (rootTransform != null)
            return rootTransform;
        return owner.parent != null ? owner.parent.customData.getRootTransform() : Transformation.identity();
    }

    public void setRootTransform(Transformation rootTransform)
    {
        this.rootTransform = rootTransform;
    }

    @Nullable
    @Override
    public ResourceLocation getRenderTypeHint()
    {
        if (renderTypeHint != null)
            return renderTypeHint;
        return owner.parent != null ? owner.parent.customData.getRenderTypeHint() : null;
    }

    public void setRenderTypeHint(ResourceLocation renderTypeHint)
    {
        this.renderTypeHint = renderTypeHint;
    }

    public void copyFrom(BlockGeometryBakingContext other)
    {
        this.customGeometry = other.customGeometry;
        this.rootTransform = other.rootTransform;
        this.visibilityData.copyFrom(other.visibilityData);
        this.renderTypeHint = other.renderTypeHint;
    }

    public Collection<Material> getTextureDependencies(Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        IUnbakedGeometry<?> geometry = getCustomGeometry();
        return geometry == null ? Collections.emptySet() :
                geometry.getMaterials(this, modelGetter, missingTextureErrors);
    }

    public BakedModel bake(ModelBakery bakery, Function<Material, TextureAtlasSprite> bakedTextureGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        IUnbakedGeometry<?> geometry = getCustomGeometry();
        if (geometry == null)
            throw new IllegalStateException("Can not use custom baking without custom geometry");
        return geometry.bake(this, bakery, bakedTextureGetter, modelTransform, overrides, modelLocation);
    }

    public static class VisibilityData
    {
        private final Map<String, Boolean> data = new HashMap<>();

        public boolean hasCustomVisibility(String part)
        {
            return data.containsKey(part);
        }

        public boolean isVisible(String part, boolean fallback)
        {
            return data.getOrDefault(part, fallback);
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
