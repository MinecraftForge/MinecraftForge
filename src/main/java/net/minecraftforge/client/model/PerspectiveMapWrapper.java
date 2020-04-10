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
import java.util.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class PerspectiveMapWrapper implements IBakedModel
{
    private final IBakedModel parent;
    private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;

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

    public static Pair<? extends IBakedModel, Matrix4f> handlePerspective(IBakedModel model, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, ItemCameraTransforms.TransformType cameraTransformType)
    {
        TRSRTransformation tr = transforms.getOrDefault(cameraTransformType, TRSRTransformation.identity());
        if (!tr.isIdentity())
        {
            return Pair.of(model, TRSRTransformation.blockCornerToCenter(tr).getMatrix());
        }
        return Pair.of(model, null);
    }

    public static Pair<? extends IBakedModel, Matrix4f> handlePerspective(IBakedModel model, IModelState state, ItemCameraTransforms.TransformType cameraTransformType)
    {
        TRSRTransformation tr = state.apply(Optional.of(cameraTransformType)).orElse(TRSRTransformation.identity());
        if (!tr.isIdentity())
        {
            return Pair.of(model, TRSRTransformation.blockCornerToCenter(tr).getMatrix());
        }
        return Pair.of(model, null);
    }

    @Override public boolean isAmbientOcclusion() { return parent.isAmbientOcclusion(); }
    @Override public boolean isAmbientOcclusion(IBlockState state) { return parent.isAmbientOcclusion(state); }
    @Override public boolean isGui3d() { return parent.isGui3d(); }
    @Override public boolean isBuiltInRenderer() { return parent.isBuiltInRenderer(); }
    @Override public TextureAtlasSprite getParticleTexture() { return parent.getParticleTexture(); }
    @SuppressWarnings("deprecation")
    @Override public ItemCameraTransforms getItemCameraTransforms() { return parent.getItemCameraTransforms(); }
    @Override public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) { return parent.getQuads(state, side, rand); }
    @Override public ItemOverrideList getOverrides() { return parent.getOverrides(); }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        return handlePerspective(this, transforms, cameraTransformType);
    }
}
