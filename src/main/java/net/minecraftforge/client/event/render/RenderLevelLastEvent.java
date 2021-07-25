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

import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.math.Matrix4f;
import net.minecraftforge.eventbus.api.Event;

public class RenderLevelLastEvent extends Event
{
    private final LevelRenderer levelRenderer;
    private final PoseStack poseStack;
    private final float partialTick;
    private final Matrix4f projectionMatrix;
    private final long startTimeNanos;

    public RenderLevelLastEvent(LevelRenderer levelRenderer, PoseStack matrixStack, float partialTick, Matrix4f projectionMatrix, long startTimeNanos)
    {
        this.levelRenderer = levelRenderer;
        this.poseStack = matrixStack;
        this.partialTick = partialTick;
        this.projectionMatrix = projectionMatrix;
        this.startTimeNanos = startTimeNanos;
    }

    public LevelRenderer getLevelRenderer()
    {
        return levelRenderer;
    }

    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    public float getPartialTick()
    {
        return partialTick;
    }

    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }

    public long getStartTimeNanos()
    {
        return startTimeNanos;
    }
}
