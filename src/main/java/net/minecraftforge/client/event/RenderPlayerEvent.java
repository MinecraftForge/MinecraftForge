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

package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class RenderPlayerEvent extends PlayerEvent
{
    private final PlayerRenderer renderer;
    private final float partialRenderTick;
    private final MatrixStack stack;
    private final IRenderTypeBuffer buffers;
    private final int light;

    public RenderPlayerEvent(PlayerEntity player, PlayerRenderer renderer, float partialRenderTick, MatrixStack stack, IRenderTypeBuffer buffers, int light)
    {
        super(player);
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
        this.stack = stack;
        this.buffers = buffers;
        this.light = light;
    }

    public PlayerRenderer getRenderer() { return renderer; }
    public float getPartialRenderTick() { return partialRenderTick; }
    public MatrixStack getMatrixStack() { return stack; }
    public IRenderTypeBuffer getBuffers() { return buffers; }
    public int getLight() { return light; }

    @Cancelable
    public static class Pre extends RenderPlayerEvent
    {
        public Pre(PlayerEntity player, PlayerRenderer renderer, float tick, MatrixStack stack, IRenderTypeBuffer buffers, int light) {
            super(player, renderer, tick, stack, buffers, light);
        }
    }

    public static class Post extends RenderPlayerEvent
    {
        public Post(PlayerEntity player, PlayerRenderer renderer, float tick, MatrixStack stack, IRenderTypeBuffer buffers, int light) {
            super(player, renderer, tick, stack, buffers, light);
        }
    }

}
