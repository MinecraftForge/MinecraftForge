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

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.animation.IEventHandler;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Generic {@link TileGameRenderer} that works with the Forge model system and animations.
 */
public class AnimationBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T>, IEventHandler<T>
{
    protected static BlockRenderDispatcher blockRenderer;

    @Override
    public void render(T te, float partialTick, PoseStack mat, MultiBufferSource renderer, int light, int otherlight)
    {
        LazyOptional<IAnimationStateMachine> cap = te.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY);
        if(!cap.isPresent())
        {
            return;
        }
        if(blockRenderer == null) blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BlockPos pos = te.getBlockPos();
        BlockAndTintGetter world = MinecraftForgeClient.getRegionRenderCacheOptional(te.getLevel(), pos)
            .map(BlockAndTintGetter.class::cast).orElseGet(() -> te.getLevel());
        BlockState state = world.getBlockState(pos);
        BakedModel model = blockRenderer.getBlockModelShaper().getBlockModel(state);
        IModelData data = model.getModelData(world, pos, state, ModelDataManager.getModelData(te.getLevel(), pos));
        if (data.hasProperty(Properties.AnimationProperty))
        {
            @SuppressWarnings("resource")
            float time = Animation.getWorldTime(Minecraft.getInstance().level, partialTick);
            cap
                .map(asm -> asm.apply(time))
                .ifPresent(pair -> {
                    handleEvents(te, time, pair.getRight());

                    // TODO: caching?
                    data.setData(Properties.AnimationProperty, pair.getLeft());
                    blockRenderer.getModelRenderer().tesselateBlock(world, model, state, pos, mat, renderer.getBuffer(Sheets.solidBlockSheet()), false, new Random(), 42, light, data);
                });
        }
    }

    @Override
    public void handleEvents(T te, float time, Iterable<Event> pastEvents) {}
}
