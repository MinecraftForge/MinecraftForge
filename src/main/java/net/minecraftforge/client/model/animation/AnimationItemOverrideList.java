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

package net.minecraftforge.client.model.animation;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.CompositeModelState;
import net.minecraftforge.common.model.animation.CapabilityAnimation;

import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;

public final class AnimationItemOverrideList extends ItemOverrides
{
    private final ModelBakery bakery;
    private final UnbakedModel model;
    private final ResourceLocation modelLoc;
    private final ModelState state;


    private final Function<Material, TextureAtlasSprite> bakedTextureGetter;

    //TODO, Mojang now bakes overrides, figure out how to reverse them.
/*
    public AnimationItemOverrideList(ModelBakery bakery, UnbakedModel model, ResourceLocation modelLoc, ModelState state, Function<Material, TextureAtlasSprite> bakedTextureGetter, ItemOverrides overrides)
    {
        this(bakery, model, modelLoc, state, bakedTextureGetter, overrides.getOverrides().reverse());
    }
*/

    public AnimationItemOverrideList(ModelBakery bakery, UnbakedModel model, ResourceLocation modelLoc, ModelState state, Function<Material, TextureAtlasSprite> bakedTextureGetter, List<ItemOverride> overrides)
    {
        super(bakery, model, ModelLoader.defaultModelGetter(), bakedTextureGetter, overrides);
        this.bakery = bakery;
        this.model = model;
        this.modelLoc = modelLoc;
        this.state = state;
        this.bakedTextureGetter = bakedTextureGetter;
    }

    @SuppressWarnings("resource")
    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed)
    {
        return stack.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null)
            .map(asm ->
            {
                Level w = world;
                // TODO caching?
                if(w == null && entity != null)
                {
                    w = entity.level;
                }
                if(world == null)
                {
                    w = Minecraft.getInstance().level;
                }
                return asm.apply(Animation.getWorldTime(world, Animation.getPartialTickTime())).getLeft();
            })
            // TODO where should uvlock data come from?
            .map(state -> model.bake(bakery, bakedTextureGetter, new CompositeModelState(state, this.state), modelLoc))
            .orElseGet(() -> super.resolve(originalModel, stack, world, entity, seed));
    }
}
