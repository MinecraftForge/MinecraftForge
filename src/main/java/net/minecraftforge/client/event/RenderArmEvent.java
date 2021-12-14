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

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.HandSide;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This is a more targeted version of {@link RenderHandEvent} event that is fired specifically when
 * a player's arm is being rendered in first person, and should be used instead if the desired
 * outcome is just to replace the rendering of the arm, such as to make armor render on it or
 * instead of it.
 *
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * Canceling the event causes the arm to not render.
 */
@Cancelable
public class RenderArmEvent extends Event
{
    private final MatrixStack poseStack;
    private final IRenderTypeBuffer multiBufferSource;
    private final int packedLight;
    private final AbstractClientPlayerEntity player;
    private final HandSide arm;

    public RenderArmEvent(MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int packedLight, AbstractClientPlayerEntity player, HandSide arm)
    {
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.player = player;
        this.arm = arm;
    }

    /**
     * @return The arm being rendered.
     */
    public HandSide getArm()
    {
        return arm;
    }

    public MatrixStack getPoseStack()
    {
        return poseStack;
    }

    public IRenderTypeBuffer getMultiBufferSource()
    {
        return multiBufferSource;
    }

    public int getPackedLight()
    {
        return packedLight;
    }

    /**
     * @return the client player that is having their arm rendered. In general this will be the same as {@link net.minecraft.client.Minecraft#player}.
     */
    public AbstractClientPlayerEntity getPlayer()
    {
        return player;
    }
}