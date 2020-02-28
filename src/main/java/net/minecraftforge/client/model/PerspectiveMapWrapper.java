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

import java.util.EnumMap;
import java.util.Optional;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class PerspectiveMapWrapper implements IDynamicBakedModel
{
    private final IBakedModel parent;
    private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;
    private final OverrideListWrapper overrides = new OverrideListWrapper();

    public PerspectiveMapWrapper(IBakedModel parent, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms)
    {
        this.parent = parent;
        this.transforms = transforms;
    }

    public PerspectiveMapWrapper(IBakedModel parent, IModelState state)
    {
        this(parent, getTransforms(state));
    }

    public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getTransforms(IModelState state)
    {
        EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> map = new EnumMap<>(ItemCameraTransforms.TransformType.class);
        for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values())
        {
            Optional<TRSRTransformation> tr = state.apply(Optional.of(type));
            if(tr.isPresent())
            {
                map.put(type, tr.get());
            }
        }
        return ImmutableMap.copyOf(map);
    }

    @SuppressWarnings("deprecation")
    public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getTransforms(ItemCameraTransforms transforms)
    {
        EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> map = new EnumMap<>(ItemCameraTransforms.TransformType.class);
        for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values())
        {
            if (transforms.hasCustomTransform(type))
            {
                map.put(type, TRSRTransformation.blockCenterToCorner(TRSRTransformation.from(transforms.getTransform(type))));
            }
        }
        return ImmutableMap.copyOf(map);
    }

    @SuppressWarnings("deprecation")
    public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getTransformsWithFallback(IModelState state, ItemCameraTransforms transforms)
    {
        EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> map = new EnumMap<>(ItemCameraTransforms.TransformType.class);
        for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values())
        {
            Optional<TRSRTransformation> tr = state.apply(Optional.of(type));
            if(tr.isPresent())
            {
                map.put(type, tr.get());
            }
            else if (transforms.hasCustomTransform(type))
            {
                map.put(type, TRSRTransformation.blockCenterToCorner(TRSRTransformation.from(transforms.getTransform(type))));
            }
        }
        return ImmutableMap.copyOf(map);
    }

    public static Pair<? extends IBakedModel, Matrix4f> handlePerspective(IBakedModel model, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, ItemCameraTransforms.TransformType cameraTransformType)
    {
        TRSRTransformation tr = transforms.getOrDefault(cameraTransformType, TRSRTransformation.identity());
        if (!tr.isIdentity())
        {
            return Pair.of(model, TRSRTransformation.blockCornerToCenter(tr).getMatrixVec());
        }
        return Pair.of(model, null);
    }

    public static Pair<? extends IBakedModel, Matrix4f> handlePerspective(IBakedModel model, IModelState state, ItemCameraTransforms.TransformType cameraTransformType)
    {
        TRSRTransformation tr = state.apply(Optional.of(cameraTransformType)).orElse(TRSRTransformation.identity());
        if (!tr.isIdentity())
        {
            return Pair.of(model, TRSRTransformation.blockCornerToCenter(tr).getMatrixVec());
        }
        return Pair.of(model, null);
    }

    @Override public boolean isAmbientOcclusion() { return parent.isAmbientOcclusion(); }
    @Override public boolean isAmbientOcclusion(BlockState state) { return parent.isAmbientOcclusion(state); }
    @Override public boolean isGui3d() { return parent.isGui3d(); }
    @Override public boolean isBuiltInRenderer() { return parent.isBuiltInRenderer(); }
    @Override public TextureAtlasSprite getParticleTexture() { return parent.getParticleTexture(); }
    @SuppressWarnings("deprecation")
    @Override public ItemCameraTransforms getItemCameraTransforms() { return parent.getItemCameraTransforms(); }
    @Override public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        return parent.getQuads(state, side, rand, extraData);
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return overrides;
    }

    @Override
    public boolean doesHandlePerspectives()
    {
        return true;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        return handlePerspective(this, transforms, cameraTransformType);
    }

    private class OverrideListWrapper extends ItemOverrideList
    {
        public OverrideListWrapper()
        {
            super();
        }

        @Nullable
        @Override
        public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn)
        {
            model = parent.getOverrides().getModelWithOverrides(parent, stack, worldIn, entityIn);
            return new PerspectiveMapWrapper(model, transforms);
        }

        @Override
        public ImmutableList<ItemOverride> getOverrides()
        {
            return parent.getOverrides().getOverrides();
        }
    }
}
