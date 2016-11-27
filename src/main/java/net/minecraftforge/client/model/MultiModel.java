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

package net.minecraftforge.client.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

// TODO: Switch to vanilla class, or to something similar
@Deprecated
public final class MultiModel implements IModel
{
    private static final class Baked implements IPerspectiveAwareModel
    {
        private final ResourceLocation location;
        private final IBakedModel base;
        private final ImmutableMap<String, IBakedModel> parts;

        private final IBakedModel internalBase;
        private ImmutableMap<Optional<EnumFacing>, ImmutableList<BakedQuad>> quads;
        private final ImmutableMap<TransformType, Pair<Baked, TRSRTransformation>> transforms;
        private final ItemOverrideList overrides = new ItemOverrideList(Lists.<ItemOverride>newArrayList())
        {
            @Override
            @Nonnull
            public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
            {
                if(originalModel != Baked.this)
                {
                    return originalModel;
                }
                boolean dirty = false;
                IBakedModel newBase = null;

                if(base != null)
                {
                    newBase = base.getOverrides().handleItemState(base, stack, world, entity);
                    if(base != newBase)
                    {
                        dirty = true;
                    }
                }
                ImmutableMap.Builder<String, IBakedModel> builder = ImmutableMap.builder();
                for(Map.Entry<String, IBakedModel> entry : parts.entrySet())
                {
                     IBakedModel newPart = entry.getValue().getOverrides().handleItemState(entry.getValue(), stack, world, entity);
                     builder.put(entry.getKey(), newPart);
                     if(entry.getValue() != newPart)
                     {
                         dirty = true;
                     }
                }
                if(dirty)
                {
                    // TODO: caching?
                    return new Baked(location, newBase instanceof IPerspectiveAwareModel, newBase, builder.build());
                }
                return Baked.this;
            }
        };

        public Baked(ResourceLocation location, boolean perspective, IBakedModel base, ImmutableMap<String, IBakedModel> parts)
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
            if(perspective && base instanceof IPerspectiveAwareModel)
            {
                IPerspectiveAwareModel perBase = (IPerspectiveAwareModel)base;
                ImmutableMap.Builder<TransformType, Pair<Baked, TRSRTransformation>> builder = ImmutableMap.builder();
                for(TransformType type : TransformType.values())
                {
                    Pair<? extends IBakedModel, Matrix4f> p = perBase.handlePerspective(type);
                    IBakedModel newBase = p.getLeft();
                    builder.put(type, Pair.of(new Baked(location, false, newBase, parts), new TRSRTransformation(p.getRight())));
                }
                transforms = builder.build();
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
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
        {
            if(quads == null)
            {
                ImmutableMap.Builder<Optional<EnumFacing>, ImmutableList<BakedQuad>> builder = ImmutableMap.builder();

                for (EnumFacing face : EnumFacing.values())
                {
                    ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
                    if (base != null)
                    {
                        quads.addAll(base.getQuads(state, face, 0));
                    }
                    for (IBakedModel bakedPart : parts.values())
                    {
                        quads.addAll(bakedPart.getQuads(state, face, 0));
                    }
                    builder.put(Optional.of(face), quads.build());
                }
                ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
                if (base != null)
                {
                    quads.addAll(base.getQuads(state, null, 0));
                }
                for (IBakedModel bakedPart : parts.values())
                {
                    quads.addAll(bakedPart.getQuads(state, null, 0));
                }
                builder.put(Optional.<EnumFacing>absent(), quads.build());
                this.quads = builder.build();
            }
            return quads.get(Optional.fromNullable(side));
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            if(transforms.isEmpty()) return Pair.of(this, null);
            Pair<Baked, TRSRTransformation> p = transforms.get(cameraTransformType);
            return Pair.of(p.getLeft(), p.getRight().getMatrix());
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return overrides;
        }
    }

    private final ResourceLocation location;
    private final IModel base;
    private final IModelState baseState;
    private final Map<String, Pair<IModel, IModelState>> parts;

    public MultiModel(ResourceLocation location, IModel base, IModelState baseState, ImmutableMap<String, Pair<IModel, IModelState>> parts)
    {
        this.location = location;
        this.base = base;
        this.baseState = baseState;
        this.parts = parts;
    }

    public MultiModel(ResourceLocation location, IModel base, IModelState baseState, Map<String, Pair<IModel, IModelState>> parts)
    {
        this(location, base, baseState, ImmutableMap.copyOf(parts));
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        Set<ResourceLocation> deps = Sets.newHashSet();

        if (base != null)
            deps.addAll(base.getDependencies());

        for (Pair<IModel, IModelState> pair : parts.values())
            deps.addAll(pair.getLeft().getDependencies());

        return deps;
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        Set<ResourceLocation> deps = Sets.newHashSet();

        if (base != null)
            deps.addAll(base.getTextures());

        for (Pair<IModel, IModelState> pair : parts.values())
            deps.addAll(pair.getLeft().getTextures());

        return deps;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        IBakedModel bakedBase = null;

        if (base != null)
            bakedBase = base.bake(state, format, bakedTextureGetter);

        ImmutableMap.Builder<String, IBakedModel> mapBuilder = ImmutableMap.builder();

        for (Entry<String, Pair<IModel, IModelState>> entry : parts.entrySet())
        {
            Pair<IModel, IModelState> pair = entry.getValue();
            mapBuilder.put(entry.getKey(), pair.getLeft().bake(new ModelStateComposition(state, pair.getRight()), format, bakedTextureGetter));
        }

        if(bakedBase == null && parts.isEmpty())
        {
            FMLLog.log(Level.ERROR, "MultiModel %s is empty (no base model or parts were provided/resolved)", location);
            IModel missing = ModelLoaderRegistry.getMissingModel();
            return missing.bake(missing.getDefaultState(), format, bakedTextureGetter);
        }
        return new Baked(location, true, bakedBase, mapBuilder.build());
    }

    @Override
    public IModelState getDefaultState()
    {
        return baseState;
    }
}
