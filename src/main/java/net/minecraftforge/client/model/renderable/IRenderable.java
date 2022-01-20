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

package net.minecraftforge.client.model.renderable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

import java.util.function.Function;

/**
 * Defines a standard interface for things that can be rendered.
 * @param <T> the type of context object used by the rendering logic
 */
@FunctionalInterface
public interface IRenderable<T>
{
    /**
     * Draws the renderable by adding the geometry to the provided {@link MultiBufferSource}
     * @param poseStack the pose stack
     * @param bufferSource the target buffer source to dump the data into
     * @param renderTypeFunction a function that provides a RenderType for the given texture
     * @param lightmapCoord the lightmap coordinates representing the current lighting conditions. See {@link net.minecraft.client.renderer.LightTexture}
     * @param overlayCoord the overlay coordinates representing the current overlay status. See {@link net.minecraft.client.renderer.texture.OverlayTexture}
     * @param partialTicks the current time expressed in the fraction of a tick elapsed since the last client tick.
     * @param renderValues the context data used for rendering
     */
    void render(PoseStack poseStack, MultiBufferSource bufferSource, Function<ResourceLocation, RenderType> renderTypeFunction, int lightmapCoord, int overlayCoord, float partialTicks, T renderValues);

    /**
     * Wraps the current renderable along with its configuration value.
     * Useful for keeping a list of various renderables paired with their configuration values.
     * @param renderValues the context data to be used for rendering
     * @return a renderable that accepts {@link Unit#INSTANCE} as a configuration value, but uses the provided {@code renderValue} instead
     */
    default IRenderable<Unit> withValues(T renderValues)
    {
        return (poseStack, bufferSource, renderTypeFunction, lightmapCoord, overlayCoord, partialTicks, unused) ->
                this.render(poseStack, bufferSource, renderTypeFunction, lightmapCoord, overlayCoord, partialTicks, renderValues);
    }
}
