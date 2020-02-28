/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.model;

import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class BlockModelConfiguration implements IModelConfiguration
{
    public final BlockModel owner;
    public final VisibilityData visibilityData = new VisibilityData();
    @Nullable
    private IModelGeometry<?> customGeometry;
    @Nullable
    private IModelState customModelState;

    public BlockModelConfiguration(BlockModel owner)
    {
        this.owner = owner;
    }

    @Nullable
    @Override
    public IUnbakedModel getOwnerModel()
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
    public IModelState getCustomModelState()
    {
        return owner.parent != null && customModelState == null ? owner.parent.customData.getCustomModelState() : customModelState;
    }

    public void setCustomModelState(IModelState modelState)
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
        return owner.isTexturePresent(name);
    }

    @Override
    public String resolveTexture(String name)
    {
        return owner.resolveTextureName(name);
    }

    @Override
    public boolean isShadedInGui() {
        return owner.isGui3d();
    }

    @Override
    public boolean useSmoothLighting()
    {
        return owner.isAmbientOcclusion();
    }

    @Override
    public ItemCameraTransforms getCameraTransforms()
    {
        return owner.getAllTransforms();
    }

    @Override
    public IModelState getCombinedState()
    {
        IModelState state = getCustomModelState();

        return state != null
                ? new SimpleModelState(PerspectiveMapWrapper.getTransformsWithFallback(state, getCameraTransforms()), state.apply(Optional.empty()))
                : new SimpleModelState(PerspectiveMapWrapper.getTransforms(getCameraTransforms()));
    }

    public void copyFrom(BlockModelConfiguration other)
    {
        this.customGeometry = other.customGeometry;
        this.customModelState = other.customModelState;
        this.visibilityData.copyFrom(other.visibilityData);
    }

    public Collection<ResourceLocation> getTextureDependencies(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
    {
        IModelGeometry<?> geometry = getCustomGeometry();
        return geometry == null ? Collections.emptySet() :
                geometry.getTextureDependencies(this, modelGetter, missingTextureErrors);
    }

    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, ISprite sprite, VertexFormat format, ItemOverrideList overrides)
    {
        IModelGeometry<?> geometry = getCustomGeometry();
        if (geometry == null)
            throw new IllegalStateException("Can not use custom baking without custom geometry");
        return geometry.bake(this, bakery, bakedTextureGetter, sprite, format, overrides);
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
