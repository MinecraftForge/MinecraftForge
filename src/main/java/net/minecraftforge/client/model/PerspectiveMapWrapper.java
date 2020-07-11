/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
            if (transforms.hasCustomTransform(type))
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
            else if (transforms.hasCustomTransform(type))
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

    @Override public boolean isAmbientOcclusion() { return parent.isAmbientOcclusion(); }
    @Override public boolean isAmbientOcclusion(BlockState state) { return parent.isAmbientOcclusion(state); }
    @Override public boolean isGui3d() { return parent.isGui3d(); }
    @Override public boolean func_230044_c_() { return parent.func_230044_c_(); }
    @Override public boolean isBuiltInRenderer() { return parent.isBuiltInRenderer(); }
    @Override public TextureAtlasSprite getParticleTexture() { return parent.getParticleTexture(); }
    @SuppressWarnings("deprecation")
    @Override public ItemCameraTransforms getItemCameraTransforms() { return parent.getItemCameraTransforms(); }
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
        public IBakedModel func_239290_a_(IBakedModel model, ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn)
        {
            model = parent.getOverrides().func_239290_a_(parent, stack, worldIn, entityIn);
            return new PerspectiveMapWrapper(model, transforms);
        }

        @Override
        public ImmutableList<ItemOverride> getOverrides()
        {
            return parent.getOverrides().getOverrides();
        }
    }
}
