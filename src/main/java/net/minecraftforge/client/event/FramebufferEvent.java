/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.client.shader.ShaderGroup;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

/**
 * Subclasses of this event will be fired surrounding the Vanilla framebuffer rendering.
 * All events are only fired if shaders are supported by the client.
 * During all of these events, you might have to setup overlay/GUI rendering yourself,
 * unless your're drawing a Vanilla Framebuffer. See {@link net.minecraft.client.renderer.EntityRenderer#setupOverlayRendering}.
 *
 * The events generally are useful for things that go in between world and overlay/GUI rendering.
 * You can use them for rendering your own framebuffers or for things that should render like GUIs but should not be effected by the HUD-hiding toggle (F1).
 */
public abstract class FramebufferEvent extends Event
{
    private final float partialTicks;

    protected FramebufferEvent(float partialTicks)
    {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    /**
     * This event is fired when Vanilla tries to draw the framebuffer for the entity outlines (spectral arrows in Vanilla).
     * The main Vanilla buffer *will* be bound during this event's execution.
     * Use this event to control the outline rendering and to apply your own effects that might need to come before the outlines.
     *
     * Cancelling it will cause {@link net.minecraft.client.renderer.RenderGlobal#renderEntityOutlineFramebuffer} to not be called.
     * This results in no outline being drawn.
     */
    @Cancelable
    public static class RenderEntityOutline extends FramebufferEvent
    {
        public RenderEntityOutline(float partialTicks)
        {
            super(partialTicks);
        }
    }

    /**
     * This event is fired before Vanilla tries to render its post-processing shaders.
     * The main Vanilla buffer might *not* be bound during this event's execution.
     * Use this event to render anything that's still supposed to be affected by the shaders.
     * Note that the event will be fired even if there's no active shader in Vanilla or they're toggled off.
     * If you want to render only if Vanilla will apply shaders, use {@link #isVanillaEnabled()}.
     *
     * Cancelling it will cause the shaders not to be applied at all.
     */
    @Cancelable
    public static class RenderShaders extends FramebufferEvent
    {
        private final ShaderGroup shaderGroup;
        private final boolean useShader;

        public RenderShaders(float partialTicks, ShaderGroup shaderGroup, boolean useShader)
        {
            super(partialTicks);
            this.shaderGroup = shaderGroup;
            this.useShader = useShader;
        }

        /**
         * @return the group of shaders to be applied to the main framebuffer and then rendered back to it or {@code null} if none is selected
         */
        @Nullable
        public ShaderGroup getShaderGroup()
        {
            return shaderGroup;
        }

        /**
         * @return {@code true} if Vanilla is configured to render the shader, {@code false} otherwise (may be toggled via F4, for instance)
         */
        public boolean shouldUseShader()
        {
            return useShader;
        }

        /**
         * @return {@code true} if Vanilla will render the shader when the event is not cancelled, {@code false} otherwise
         */
        public boolean isVanillaEnabled()
        {
            return getShaderGroup() != null && shouldUseShader();
        }
    }

    /**
     * This event is fired after both entity outlines and shaders were drawn.
     * The main Vanilla buffer might *not* be bound during this event's execution, since shaders will unbind it by default.
     * Use this event for custom rendering that shouldn't be affected by the postprocessing shaders.
     */
    public static class RenderBuffers extends FramebufferEvent
    {
        public RenderBuffers(float partialTicks)
        {
            super(partialTicks);
        }
    }
}
