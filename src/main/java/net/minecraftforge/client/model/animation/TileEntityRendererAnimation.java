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

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
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
public class TileEntityRendererAnimation<T extends TileEntity> extends TileEntityRendererFast<T> implements IEventHandler<T>
{
    protected static BlockRendererDispatcher blockRenderer;

    @Override
    public void renderTileEntityFast(T te, double x, double y, double z, float partialTick, int breakStage, BufferBuilder renderer)
    {
        LazyOptional<IAnimationStateMachine> cap = te.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY);
        if(!cap.isPresent())
        {
            return;
        }
        if(blockRenderer == null) blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
        BlockPos pos = te.getPos();
        net.minecraft.world.IEnviromentBlockReader world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
        BlockState state = world.getBlockState(pos);
        IBakedModel model = blockRenderer.getBlockModelShapes().getModel(state);
        IModelData data = model.getModelData(world, pos, state, ModelDataManager.getModelData(te.getWorld(), pos));
        if (data.hasProperty(Properties.AnimationProperty))
        {
            float time = Animation.getWorldTime(getWorld(), partialTick);
            cap
                .map(asm -> asm.apply(time))
                .ifPresent(pair -> {
                    handleEvents(te, time, pair.getRight());

                    // TODO: caching?
                    data.setData(Properties.AnimationProperty, pair.getLeft());

                    renderer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

                    blockRenderer.getBlockModelRenderer().renderModel(world, model, state, pos, renderer, false, new Random(), 42, data);
                });
        }
    }

    @Override
    public void handleEvents(T te, float time, Iterable<Event> pastEvents) {}
}
