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

package net.minecraftforge.debug.client.rendering;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GatherGeometryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod(GeometryGatheringTest.MOD_ID)
public class GeometryGatheringTest
{
    private static final boolean ENABLED = true;
    static final String MOD_ID = "geometry_gathering_test";

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class ClientHandler
    {
        private static final Supplier<BlockState> STATIC_STATE = Suppliers.memoize(() -> Blocks.OBSIDIAN.defaultBlockState());
        private static final Supplier<BlockState> DYNAMIC_STATE = Suppliers.memoize(() -> Blocks.STONE.defaultBlockState());

        @SubscribeEvent
        public static void onGatherStaticChunkGeometry(final GatherGeometryEvent.ChunkSectionStatic event)
        {
            if (!ENABLED) return;
            if (event.getOrigin().getY() >> 4 != 80 >> 4) return;

            event.getVisibilityGraph().setOpaque(event.getOrigin().above(80 & 0xF));

            var bufferSource = event.getBufferSource();
            var poseStack = new PoseStack();
            poseStack.translate(0, 80 & 0xF, 0);

            var blockRenderer = Minecraft.getInstance().getBlockRenderer();
            blockRenderer.getModelRenderer().renderModel(
                    poseStack.last(), bufferSource.getBuffer(RenderType.solid()),
                    STATIC_STATE.get(), blockRenderer.getBlockModel(STATIC_STATE.get()),
                    1f, 1f, 1f, 0x0E00E0, OverlayTexture.NO_OVERLAY
            );
        }

        @SubscribeEvent
        public static void onGatherDynamicLevelGeometry(final GatherGeometryEvent.LevelDynamic event)
        {
            if (!ENABLED) return;

            var bufferSource = event.getBufferSource();
            var poseStack = event.getPoseStack();

            poseStack.pushPose();
            poseStack.translate(0, 5, 0);

            var blockRenderer = Minecraft.getInstance().getBlockRenderer();
            blockRenderer.getModelRenderer().renderModel(
                    poseStack.last(), bufferSource.getBuffer(RenderType.solid()),
                    DYNAMIC_STATE.get(), blockRenderer.getBlockModel(DYNAMIC_STATE.get()),
                    1f, 1f, 1f, 0x0E00E0, OverlayTexture.NO_OVERLAY
            );

            poseStack.popPose();
        }
    }

}