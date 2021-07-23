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

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class RenderPlayerEvent extends PlayerEvent
{
    private final PlayerRenderer renderer;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int light;

    public RenderPlayerEvent(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light)
    {
        super(player);
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.light = light;
    }

    public PlayerRenderer getRenderer()
    {
        return renderer;
    }

    public float getPartialTick()
    {
        return partialTick;
    }

    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    public MultiBufferSource getBufferSource()
    {
        return bufferSource;
    }

    public int getLight()
    {
        return light;
    }

    @Cancelable
    public static class Pre extends RenderPlayerEvent
    {
        public Pre(Player player, PlayerRenderer renderer, float tick, PoseStack poseStack, MultiBufferSource buffers, int light)
        {
            super(player, renderer, tick, poseStack, buffers, light);
        }
    }

    public static class Post extends RenderPlayerEvent
    {
        public Post(Player player, PlayerRenderer renderer, float tick, PoseStack poseStack, MultiBufferSource buffers, int light)
        {
            super(player, renderer, tick, poseStack, buffers, light);
        }
    }
}
