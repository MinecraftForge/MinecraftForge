/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.animation;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.common.model.animation.CapabilityAnimation;

public final class AnimationItemOverrideList extends ItemOverrideList
{
    private final ModelBakery bakery;
    private final IUnbakedModel model;
    private final ResourceLocation modelLoc;
    private final IModelTransform state;


    private final Function<Material, TextureAtlasSprite> bakedTextureGetter;

    public AnimationItemOverrideList(ModelBakery bakery, IUnbakedModel model, ResourceLocation modelLoc, IModelTransform state, Function<Material, TextureAtlasSprite> bakedTextureGetter, ItemOverrideList overrides)
    {
        this(bakery, model, modelLoc, state, bakedTextureGetter, overrides.getOverrides().reverse());
    }

    public AnimationItemOverrideList(ModelBakery bakery, IUnbakedModel model, ResourceLocation modelLoc, IModelTransform state, Function<Material, TextureAtlasSprite> bakedTextureGetter, List<ItemOverride> overrides)
    {
        super(bakery, model, ModelLoader.defaultModelGetter(), bakedTextureGetter, overrides);
        this.bakery = bakery;
        this.model = model;
        this.modelLoc = modelLoc;
        this.state = state;
        this.bakedTextureGetter = bakedTextureGetter;
    }

    @Override
    public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity)
    {
        return stack.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null)
            .map(asm ->
            {
                World w = world;
                // TODO caching?
                if(w == null && entity != null)
                {
                    w = entity.world;
                }
                if(world == null)
                {
                    w = Minecraft.getInstance().world;
                }
                return asm.apply(Animation.getWorldTime(world, Animation.getPartialTickTime())).getLeft();
            })
            // TODO where should uvlock data come from?
            .map(state -> model.bakeModel(bakery, bakedTextureGetter, new ModelTransformComposition(state, this.state), modelLoc))
            .orElseGet(() -> super.getModelWithOverrides(originalModel, stack, world, entity));
    }
}
