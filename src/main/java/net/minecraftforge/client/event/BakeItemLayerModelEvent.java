/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.client.event;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Optional;
import java.util.function.Function;

/**
 * Events fired when bake methods are called on vanilla json model hierachy item model element.
 * Use in conjunction with capabilities to add new functionality to vanila jsons. 
 */
public class BakeItemLayerModelEvent extends Event
{
    private final ItemLayerModel itemLayerModel;
    private final IModelState state;
    private final VertexFormat format;
    private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
    private ImmutableList.Builder<BakedQuad> builder;
    protected Optional<TRSRTransformation> transform;

    public BakeItemLayerModelEvent(ItemLayerModel itemLayerModel, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, Builder<BakedQuad> builder, Optional<TRSRTransformation> transform)
    {
        this.itemLayerModel = itemLayerModel;
        this.state = state;
        this.format = format;
        this.bakedTextureGetter = bakedTextureGetter;
        this.builder = builder;
        this.transform = transform;
    }

    public ItemLayerModel getItemLayerModel()
    {
        return itemLayerModel;
    }

    public IModelState getState()
    {
        return state;
    }

    public VertexFormat getFormat()
    {
        return format;
    }

    public Function<ResourceLocation, TextureAtlasSprite> getBakedTextureGetter()
    {
        return bakedTextureGetter;
    }

    public ImmutableList.Builder<BakedQuad> getBuilder()
    {
        return builder;
    }

    public Optional<TRSRTransformation> getTransform()
    {
        return transform;
    }

    public void setBuilder(ImmutableList.Builder<BakedQuad> builder)
    {
        this.builder = builder;
    }

    public static class Pre extends BakeItemLayerModelEvent {

        public Pre(ItemLayerModel itemLayerModel, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, Builder<BakedQuad> builder, Optional<TRSRTransformation> transform)
        {
            super(itemLayerModel, state, format, bakedTextureGetter, builder, transform);
        }

        public void setTransform(Optional<TRSRTransformation> transform)
        {
            this.transform = transform;
        }

    }

    public static class Post extends BakeItemLayerModelEvent {

        private TextureAtlasSprite particle;
        private ImmutableMap<TransformType, TRSRTransformation> map;

        public Post(ItemLayerModel itemLayerModel, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, Builder<BakedQuad> builder, Optional<TRSRTransformation> transform, TextureAtlasSprite particle, ImmutableMap<TransformType, TRSRTransformation> map)
        {
            super(itemLayerModel, state, format, bakedTextureGetter, builder, transform);
            this.particle = particle;
            this.map = map;
        }

        public TextureAtlasSprite getParticle()
        {
            return particle;
        }

        public ImmutableMap<TransformType, TRSRTransformation> getMap()
        {
            return map;
        }

        public void setParticle(TextureAtlasSprite particle)
        {
            this.particle = particle;
        }

        public void setMap(ImmutableMap<TransformType, TRSRTransformation> map)
        {
            this.map = map;
        }

    }

}
