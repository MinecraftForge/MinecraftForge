/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nullable;
import java.util.List;

public class PerspectiveMapWrapper implements IDynamicBakedModel
{
    private final IBakedModel parent;
    private final ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms;
    private final OverrideListWrapper overrides = new OverrideListWrapper();

    public PerspectiveMapWrapper(IBakedModel parent, ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms)
    {
        this.parent = parent;
        this.transforms = transforms;
    }

    public PerspectiveMapWrapper(IBakedModel parent, IModelTransform state)
    {
        this(parent, getTransforms(state));
    }

    public static ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransforms(IModelTransform state)
    {
        EnumMap<ItemCameraTransforms.TransformType, TransformationMatrix> map = new EnumMap<>(ItemCameraTransforms.TransformType.class);
        for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values())
        {
            TransformationMatrix tr = state.getPartTransformation(type);
            if(!tr.isIdentity())
            {
                map.put(type, tr);
            }
        }
        return ImmutableMap.copyOf(map);
    }

    @SuppressWarnings("deprecation")
    public static ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransforms(ItemCameraTransforms transforms)
    {
        EnumMap<ItemCameraTransforms.TransformType, TransformationMatrix> map = new EnumMap<>(ItemCameraTransforms.TransformType.class);
        for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values())
        {
            if (transforms.hasTransform(type))
            {
                map.put(type, TransformationHelper.toTransformation(transforms.getTransform(type)));
            }
        }
        return ImmutableMap.copyOf(map);
    }

    @SuppressWarnings("deprecation")
    public static ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransformsWithFallback(IModelTransform state, ItemCameraTransforms transforms)
    {
        EnumMap<ItemCameraTransforms.TransformType, TransformationMatrix> map = new EnumMap<>(ItemCameraTransforms.TransformType.class);
        for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values())
        {
            TransformationMatrix tr = state.getPartTransformation(type);
            if(!tr.isIdentity())
            {
                map.put(type, tr);
            }
            else if (transforms.hasTransform(type))
            {
                map.put(type, TransformationHelper.toTransformation(transforms.getTransform(type)));
            }
        }
        return ImmutableMap.copyOf(map);
    }

    public static IBakedModel handlePerspective(IBakedModel model, ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms, ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {
        TransformationMatrix tr = transforms.getOrDefault(cameraTransformType, TransformationMatrix.identity());
        if (!tr.isIdentity())
        {
            tr.push(mat);
        }
        return model;
    }

    public static IBakedModel handlePerspective(IBakedModel model, IModelTransform state, ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {
        TransformationMatrix tr = state.getPartTransformation(cameraTransformType);
        if (!tr.isIdentity())
        {
            tr.push(mat);
        }
        return model;
    }

    @Override public boolean useAmbientOcclusion() { return parent.useAmbientOcclusion(); }
    @Override public boolean isAmbientOcclusion(BlockState state) { return parent.isAmbientOcclusion(state); }
    @Override public boolean isGui3d() { return parent.isGui3d(); }
    @Override public boolean usesBlockLight() { return parent.usesBlockLight(); }
    @Override public boolean isCustomRenderer() { return parent.isCustomRenderer(); }
    @Override public TextureAtlasSprite getParticleIcon() { return parent.getParticleIcon(); }
    @SuppressWarnings("deprecation")
    @Override public ItemCameraTransforms getTransforms() { return parent.getTransforms(); }
    @Override public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData)
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
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {
        return handlePerspective(this, transforms, cameraTransformType, mat);
    }

    private class OverrideListWrapper extends ItemOverrideList
    {
        public OverrideListWrapper()
        {
            super();
        }

        @Nullable
        @Override
        public IBakedModel resolve(IBakedModel model, ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn)
        {
            model = parent.getOverrides().resolve(parent, stack, worldIn, entityIn);
            return new PerspectiveMapWrapper(model, transforms);
        }

        @Override
        public ImmutableList<ItemOverride> getOverrides()
        {
            return parent.getOverrides().getOverrides();
        }
    }
}
