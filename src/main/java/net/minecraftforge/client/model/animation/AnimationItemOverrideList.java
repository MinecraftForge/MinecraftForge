/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

import java.util.function.Function;

import javax.annotation.Nullable;

public final class AnimationItemOverrideList extends ItemOverrideList
{
    private final IModel model;
    private final IModelState state;
    private final VertexFormat format;
    private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

    public AnimationItemOverrideList(IModel model, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, ItemOverrideList overrides)
    {
        this(model, state, format, bakedTextureGetter, overrides.getOverrides().reverse());
    }

    public AnimationItemOverrideList(IModel model, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, List<ItemOverride> overrides)
    {
        super(overrides);
        this.model = model;
        this.state = state;
        this.format = format;
        this.bakedTextureGetter = bakedTextureGetter;
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
    {
        IAnimationStateMachine asm = stack.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null);
        if (asm != null)
        {
            // TODO: caching?
            if(world == null && entity != null)
            {
                world = entity.world;
            }
            if(world == null)
            {
                world = Minecraft.getMinecraft().world;
            }
            IModelState state = asm.apply(Animation.getWorldTime(world, Animation.getPartialTickTime())).getLeft();
            return model.bake(new ModelStateComposition(state, this.state), format, bakedTextureGetter);
        }
        return super.handleItemState(originalModel, stack, world, entity);
    }
}
