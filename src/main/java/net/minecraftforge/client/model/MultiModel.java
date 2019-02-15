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

import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

// TODO: Switch to vanilla class, or to something similar
@Deprecated
public final class MultiModel implements IUnbakedModel
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final class Baked implements IBakedModel
    {
        private final ResourceLocation location;
        @Nullable
        private final IBakedModel base;
        private final ImmutableMap<String, IBakedModel> parts;

        private final IBakedModel internalBase;
        private final ImmutableMap<TransformType, Pair<Baked, TRSRTransformation>> transforms;
        private final ItemOverrideList overrides = new ItemOverrideList()
        {
            @Override
            public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
            {
                if(originalModel != Baked.this)
                {
                    return originalModel;
                }
                boolean dirty = false;
                IBakedModel newBase = null;

                if(base != null)
                {
                    newBase = base.getOverrides().getModelWithOverrides(base, stack, world, entity);
                    if(base != newBase)
                    {
                        dirty = true;
                    }
                }
                ImmutableMap.Builder<String, IBakedModel> builder = ImmutableMap.builder();
                for(Map.Entry<String, IBakedModel> entry : parts.entrySet())
                {
                     IBakedModel newPart = entry.getValue().getOverrides().getModelWithOverrides(entry.getValue(), stack, world, entity);
                     builder.put(entry.getKey(), newPart);
                     if(entry.getValue() != newPart)
                     {
                         dirty = true;
                     }
                }
                if(dirty)
                {
                    // TODO: caching?
                    return new Baked(location, true, newBase, builder.build());
                }
                return Baked.this;
            }
        };

        public Baked(ResourceLocation location, boolean perspective, @Nullable IBakedModel base, ImmutableMap<String, IBakedModel> parts)
        {
            this.location = location;
            this.base = base;
            this.parts = parts;

            if (base != null)
                internalBase = base;
            else
            {
                Iterator<IBakedModel> iter = parts.values().iterator();
                if (iter.hasNext())
                    internalBase = iter.next();
                else
                    throw new IllegalArgumentException("No base model or submodel provided for MultiModel.Baked " + location + ".");
            }

            // Only changes the base model based on perspective, may recurse for parts in the future.
            if(base != null && perspective)
            {
                EnumMap<TransformType, Pair<Baked, TRSRTransformation>> map = new EnumMap<>(TransformType.class);
                for(TransformType type : TransformType.values())
                {
                    Pair<? extends IBakedModel, Matrix4f> p = base.handlePerspective(type);
                    IBakedModel newBase = p.getLeft();
                    Matrix4f matrix = p.getRight();
                    if (newBase != base || matrix != null)
                    {
                        map.put(type, Pair.of(new Baked(location, false, newBase, parts), new TRSRTransformation(matrix)));
                    }
                }
                transforms = ImmutableMap.copyOf(map);
            }
            else
            {
                transforms = ImmutableMap.of();
            }
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return internalBase.isAmbientOcclusion();
        }

        @Override
        public boolean isAmbientOcclusion(IBlockState state)
        {
            return internalBase.isAmbientOcclusion(state);
        }

        @Override
        public boolean isGui3d()
        {
            return internalBase.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return internalBase.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return internalBase.getParticleTexture();
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return internalBase.getItemCameraTransforms();
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand)
        {
            ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
            if (base != null)
            {
                quads.addAll(base.getQuads(state, side, rand));
            }
            for (IBakedModel bakedPart : parts.values())
            {
                quads.addAll(bakedPart.getQuads(state, side, rand));
            }
            return quads.build();
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            Pair<Baked, TRSRTransformation> p = transforms.get(cameraTransformType);
            if (p == null) return Pair.of(this, null);
            return Pair.of(p.getLeft(), p.getRight().getMatrixVec());
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return overrides;
        }
    }

    private final ResourceLocation location;
    @Nullable
    private final IUnbakedModel base;
    private final Map<String, Pair<IUnbakedModel, IModelState>> parts;

    public MultiModel(ResourceLocation location, @Nullable IUnbakedModel base, ImmutableMap<String, Pair<IUnbakedModel, IModelState>> parts)
    {
        this.location = location;
        this.base = base;
        this.parts = parts;
    }

    public MultiModel(ResourceLocation location, IUnbakedModel base, Map<String, Pair<IUnbakedModel, IModelState>> parts)
    {
        this(location, base, ImmutableMap.copyOf(parts));
    }

    @Override
    public Collection<ResourceLocation> getOverrideLocations()
    {
        Set<ResourceLocation> deps = Sets.newHashSet();

        if (base != null)
            deps.addAll(base.getOverrideLocations());

        for (Pair<IUnbakedModel, IModelState> pair : parts.values())
            deps.addAll(pair.getLeft().getOverrideLocations());

        return deps;
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextures)
    {
        Set<ResourceLocation> deps = Sets.newHashSet();

        if (base != null)
            deps.addAll(base.getTextures(modelGetter, missingTextures));

        for (Pair<IUnbakedModel, IModelState> pair : parts.values())
            deps.addAll(pair.getLeft().getTextures(modelGetter, missingTextures));

        return deps;
    }

    @Override
    public IBakedModel bake(Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, IModelState state, boolean uvlock, VertexFormat format)
    {
        IBakedModel bakedBase = null;

        if (base != null)
            bakedBase = base.bake(modelGetter, bakedTextureGetter, state, uvlock, format);

        ImmutableMap.Builder<String, IBakedModel> mapBuilder = ImmutableMap.builder();

        for (Entry<String, Pair<IUnbakedModel, IModelState>> entry : parts.entrySet())
        {
            Pair<IUnbakedModel, IModelState> pair = entry.getValue();
            mapBuilder.put(entry.getKey(), pair.getLeft().bake(modelGetter, bakedTextureGetter, new ModelStateComposition(state, pair.getRight()), uvlock, format));
        }

        if(bakedBase == null && parts.isEmpty())
        {
            LOGGER.error("MultiModel {} is empty (no base model or parts were provided/resolved)", location);
            IUnbakedModel missing = ModelLoaderRegistry.getMissingModel();
            return missing.bake(modelGetter, bakedTextureGetter, missing.getDefaultState(), uvlock, format);
        }
        return new Baked(location, true, bakedBase, mapBuilder.build());
    }
}
