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

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * whenever a hand is rendered in first person.
 * Canceling the event causes the hand to not render.
 */
@Cancelable
public class RenderHandEvent extends Event
{
    private final InteractionHand hand;
    private final PoseStack mat;
    private final MultiBufferSource buffers;
    private final int light;
    private final float partialTicks;
    private final float interpolatedPitch;
    private final float swingProgress;
    private final float equipProgress;
    @Nonnull
    private final ItemStack stack;

    public RenderHandEvent(InteractionHand hand, PoseStack mat, MultiBufferSource buffers, int light,
                           float partialTicks, float interpolatedPitch,
                           float swingProgress, float equipProgress, @Nonnull ItemStack stack)
    {
        this.hand = hand;
        this.mat = mat;
        this.buffers = buffers;
        this.light = light;
        this.partialTicks = partialTicks;
        this.interpolatedPitch = interpolatedPitch;
        this.swingProgress = swingProgress;
        this.equipProgress = equipProgress;
        this.stack = stack;
    }

    public InteractionHand getHand()
    {
        return hand;
    }

    public PoseStack getMatrixStack()
    {
        return mat;
    }

    public MultiBufferSource getBuffers() {
        return buffers;
    }

    public int getLight() {
        return light;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    /**
     * @return The interpolated pitch of the player entity
     */
    public float getInterpolatedPitch()
    {
        return interpolatedPitch;
    }

    /**
     * @return The swing progress of the hand being rendered
     */
    public float getSwingProgress()
    {
        return swingProgress;
    }

    /**
     * @return The progress of the equip animation. 1.0 is fully equipped.
     */
    public float getEquipProgress()
    {
        return equipProgress;
    }

    /**
     * @return The ItemStack to be rendered
     */
    @Nonnull
    public ItemStack getItemStack()
    {
        return stack;
    }
}
