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

package net.minecraftforge.client.model.animation;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.animation.IEventHandler;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Generic TileEntitySpecialRenderer that works with the Forge model system and animations.
 */
public class AnimationTESR<T extends TileEntity> extends FastTESR<T> implements IEventHandler<T>
{
    protected static BlockRendererDispatcher blockRenderer;

    @Override
    public void renderTileEntityFast(T te, double x, double y, double z, float partialTick, int breakStage, float partial, BufferBuilder renderer)
   {
        if(!te.hasCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null))
        {
            return;
        }
        if(blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        BlockPos pos = te.getPos();
        IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
        IBlockState state = world.getBlockState(pos);
        if(state.getPropertyKeys().contains(Properties.StaticProperty))
        {
            state = state.withProperty(Properties.StaticProperty, false);
        }
        if(state instanceof IExtendedBlockState)
        {
            IExtendedBlockState exState = (IExtendedBlockState)state;
            if(exState.getUnlistedNames().contains(Properties.AnimationProperty))
            {
                float time = Animation.getWorldTime(getWorld(), partialTick);
                IAnimationStateMachine capability = te.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null);
                if (capability != null)
                {
                    Pair<IModelState, Iterable<Event>> pair = capability.apply(time);
                    handleEvents(te, time, pair.getRight());

                    // TODO: caching?
                    IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(exState.getClean());
                    exState = exState.withProperty(Properties.AnimationProperty, pair.getLeft());

                    renderer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

                    blockRenderer.getBlockModelRenderer().renderModel(world, model, exState, pos, renderer, false);
                }
            }
        }
    }

    @Override
    public void handleEvents(T te, float time, Iterable<Event> pastEvents) {}
}
