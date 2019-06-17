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

package net.minecraftforge.client.model.animation;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

import java.util.function.Function;

import javax.annotation.Nullable;

public final class AnimationItemOverrideList extends ItemOverrideList
{
    private final ModelBakery bakery;
    private final IUnbakedModel model;
    private final IModelState state;
    private final VertexFormat format;
    private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

    public AnimationItemOverrideList(ModelBakery bakery, IUnbakedModel model, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, ItemOverrideList overrides)
    {
        this(bakery, model, state, format, bakedTextureGetter, overrides.getOverrides().reverse());
    }

    public AnimationItemOverrideList(ModelBakery bakery, IUnbakedModel model, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, List<ItemOverride> overrides)
    {
        super(bakery, model, ModelLoader.defaultModelGetter(), bakedTextureGetter, overrides, format);
        this.bakery = bakery;
        this.model = model;
        this.state = state;
        this.format = format;
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
            .map(state -> model.bake(bakery, bakedTextureGetter, new ModelStateComposition(state, this.state), format))
            .orElseGet(() -> super.getModelWithOverrides(originalModel, stack, world, entity));
    }
}
