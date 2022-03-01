/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import java.util.EnumMap;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import com.mojang.math.Transformation;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;

public class PerspectiveMapWrapper implements IDynamicBakedModel
{
    private final BakedModel parent;
    private final ImmutableMap<ItemTransforms.TransformType, Transformation> transforms;
    private final OverrideListWrapper overrides = new OverrideListWrapper();

    public PerspectiveMapWrapper(BakedModel parent, ImmutableMap<ItemTransforms.TransformType, Transformation> transforms)
    {
        this.parent = parent;
        this.transforms = transforms;
    }

    public PerspectiveMapWrapper(BakedModel parent, ModelState state)
    {
        this(parent, getTransforms(state));
    }

    public static ImmutableMap<ItemTransforms.TransformType, Transformation> getTransforms(ModelState state)
    {
        EnumMap<ItemTransforms.TransformType, Transformation> map = new EnumMap<>(ItemTransforms.TransformType.class);
        for(ItemTransforms.TransformType type : ItemTransforms.TransformType.values())
        {
            Transformation tr = state.getPartTransformation(type);
            if(!tr.isIdentity())
            {
                map.put(type, tr);
            }
        }
        return ImmutableMap.copyOf(map);
    }

    @SuppressWarnings("deprecation")
    public static ImmutableMap<ItemTransforms.TransformType, Transformation> getTransforms(ItemTransforms transforms)
    {
        EnumMap<ItemTransforms.TransformType, Transformation> map = new EnumMap<>(ItemTransforms.TransformType.class);
        for(ItemTransforms.TransformType type : ItemTransforms.TransformType.values())
        {
            if (transforms.hasTransform(type))
            {
                map.put(type, TransformationHelper.toTransformation(transforms.getTransform(type)));
            }
        }
        return ImmutableMap.copyOf(map);
    }

    @SuppressWarnings("deprecation")
    public static ImmutableMap<ItemTransforms.TransformType, Transformation> getTransformsWithFallback(ModelState state, ItemTransforms transforms)
    {
        EnumMap<ItemTransforms.TransformType, Transformation> map = new EnumMap<>(ItemTransforms.TransformType.class);
        for(ItemTransforms.TransformType type : ItemTransforms.TransformType.values())
        {
            Transformation tr = state.getPartTransformation(type);
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

    public static BakedModel handlePerspective(BakedModel model, ImmutableMap<ItemTransforms.TransformType, Transformation> transforms, ItemTransforms.TransformType cameraTransformType, PoseStack mat)
    {
        Transformation tr = transforms.getOrDefault(cameraTransformType, Transformation.identity());
        if (!tr.isIdentity())
        {
            tr.push(mat);
        }
        return model;
    }

    public static BakedModel handlePerspective(BakedModel model, ModelState state, ItemTransforms.TransformType cameraTransformType, PoseStack mat)
    {
        Transformation tr = state.getPartTransformation(cameraTransformType);
        if (!tr.isIdentity())
        {
            tr.push(mat);
        }
        return model;
    }

    @Override public boolean useAmbientOcclusion() { return parent.useAmbientOcclusion(); }
    @Override public boolean useAmbientOcclusion(BlockState state) { return parent.useAmbientOcclusion(state); }
    @Override public boolean isGui3d() { return parent.isGui3d(); }
    @Override public boolean usesBlockLight() { return parent.usesBlockLight(); }
    @Override public boolean isCustomRenderer() { return parent.isCustomRenderer(); }
    @Override public TextureAtlasSprite getParticleIcon() { return parent.getParticleIcon(); }
    @SuppressWarnings("deprecation")
    @Override public ItemTransforms getTransforms() { return parent.getTransforms(); }
    @Override public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData)
    {
        return parent.getQuads(state, side, rand, extraData);
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return overrides;
    }

    @Override
    public boolean doesHandlePerspectives()
    {
        return true;
    }

    @Override
    public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack)
    {
        return handlePerspective(this, transforms, cameraTransformType, poseStack);
    }

    private class OverrideListWrapper extends ItemOverrides
    {
        public OverrideListWrapper()
        {
            super();
        }

        @Nullable
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed)
        {
            model = parent.getOverrides().resolve(parent, stack, level, entity, seed);
            return new PerspectiveMapWrapper(model, transforms);
        }

        @Override
        public ImmutableList<BakedOverride> getOverrides()
        {
            return parent.getOverrides().getOverrides();
        }
    }
}
