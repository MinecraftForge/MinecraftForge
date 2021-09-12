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

package net.minecraftforge.client.event.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired before a hand is rendered in the first person view.
 *
 * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
 * If this event is cancelled, then the hand will not be rendered. </p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see net.minecraftforge.client.ForgeHooksClient#renderSpecificFirstPersonHand(InteractionHand, PoseStack, MultiBufferSource, int, float, float, float, float, ItemStack)
 */
@Cancelable
public class RenderHandEvent extends Event
{
    private final InteractionHand hand;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int light;
    private final float partialTick;
    private final float interpolatedPitch;
    private final float swingProgress;
    private final float equipProgress;
    private final ItemStack stack;

    public RenderHandEvent(InteractionHand hand, PoseStack poseStack, MultiBufferSource bufferSource, int light,
                           float partialTick, float interpolatedPitch,
                           float swingProgress, float equipProgress, ItemStack stack)
    {
        this.hand = hand;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.light = light;
        this.partialTick = partialTick;
        this.interpolatedPitch = interpolatedPitch;
        this.swingProgress = swingProgress;
        this.equipProgress = equipProgress;
        this.stack = stack;
    }

    /**
     * {@return the hand being rendered}
     */
    public InteractionHand getHand()
    {
        return hand;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    /**
     * {@return the source of rendering buffers}
     */
    public MultiBufferSource getBufferSource()
    {
        return bufferSource;
    }

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getLight()
    {
        return light;
    }

    /**
     * {@return the partial tick}
     */
    public float getPartialTick()
    {
        return partialTick;
    }

    /**
     * {@return the interpolated pitch of the player entity}
     */
    public float getInterpolatedPitch()
    {
        return interpolatedPitch;
    }

    /**
     * {@return the swing progress of the hand being rendered}
     */
    public float getSwingProgress()
    {
        return swingProgress;
    }

    /**
     * {@return the progress of the equip animation, from {@code 0.0} to {@code 1.0}}
     */
    public float getEquipProgress()
    {
        return equipProgress;
    }

    /**
     * {@return the item stack to be rendered}
     */
    public ItemStack getItemStack()
    {
        return stack;
    }
}