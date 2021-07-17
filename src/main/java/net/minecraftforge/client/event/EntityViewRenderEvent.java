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

import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.eventbus.api.Cancelable;

import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * Event that hooks into GameRenderer, allowing any feature to customize visual attributes
 *  the player sees.
 */
public abstract class EntityViewRenderEvent extends net.minecraftforge.eventbus.api.Event
{
    private final GameRenderer renderer;
    private final Camera info;
    private final double renderPartialTicks;

    public EntityViewRenderEvent(GameRenderer renderer, Camera info, double renderPartialTicks)
    {
        this.renderer = renderer;
        this.info = info;
        this.renderPartialTicks = renderPartialTicks;
    }

    public GameRenderer getRenderer()
    {
        return renderer;
    }

    public Camera getInfo()
    {
        return info;
    }

    public double getRenderPartialTicks()
    {
        return renderPartialTicks;
    }

    private static class FogEvent extends EntityViewRenderEvent
    {
        private final FogMode type;
        @SuppressWarnings("resource")
        protected FogEvent(FogMode type, Camera info, double renderPartialTicks)
        {
            super(Minecraft.getInstance().gameRenderer, info, renderPartialTicks);
            this.type = type;
        }

        public FogMode getType() { return type; }
    }

    /**
     * Event that allows any feature to customize the fog density the player sees.
     * NOTE: In order to make this event have an effect, you must cancel the event
     */
    @Cancelable
    public static class FogDensity extends FogEvent
    {
        private float density;

        public FogDensity(FogMode type, Camera info, float partialTicks, float density)
        {
            super(type, info, partialTicks);
            this.setDensity(density);
        }

        public float getDensity()
        {
            return density;
        }

        public void setDensity(float density)
        {
            this.density = density;
        }
    }

    /**
     * Event that allows any feature to customize the rendering of fog.
     */
    @HasResult
    public static class RenderFogEvent extends FogEvent
    {
        private final float farPlaneDistance;

        public RenderFogEvent(FogMode type, Camera info, float partialTicks, float distance)
        {
            super(type, info, partialTicks);
            this.farPlaneDistance = distance;
        }

        public float getFarPlaneDistance()
        {
            return farPlaneDistance;
        }
    }

    /**
     * Event that allows any feature to customize the color of fog the player sees.
     * NOTE: Any change made to one of the color variables will affect the result seen in-game.
     */
    public static class FogColors extends EntityViewRenderEvent
    {
        private float red;
        private float green;
        private float blue;

        @SuppressWarnings("resource")
        public FogColors(Camera info, float partialTicks, float red, float green, float blue)
        {
            super(Minecraft.getInstance().gameRenderer, info, partialTicks);
            this.setRed(red);
            this.setGreen(green);
            this.setBlue(blue);
        }

        public float getRed() { return red; }
        public void setRed(float red) { this.red = red; }
        public float getGreen() { return green; }
        public void setGreen(float green) { this.green = green; }
        public float getBlue() { return blue; }
        public void setBlue(float blue) { this.blue = blue; }
    }

    /**
     * Event that allows mods to alter the angles of the player's camera. Mainly useful for applying roll.
     */
    public static class CameraSetup extends EntityViewRenderEvent
    {
        private float yaw;
        private float pitch;
        private float roll;

        public CameraSetup(GameRenderer renderer, Camera info, double renderPartialTicks, float yaw, float pitch, float roll)
        {
            super(renderer, info, renderPartialTicks);
            this.setYaw(yaw);
            this.setPitch(pitch);
            this.setRoll(roll);
        }

        public float getYaw() { return yaw; }
        public void setYaw(float yaw) { this.yaw = yaw; }
        public float getPitch() { return pitch; }
        public void setPitch(float pitch) { this.pitch = pitch; }
        public float getRoll() { return roll; }
        public void setRoll(float roll) { this.roll = roll; }
    }

    /**
     * Event that allows mods to alter the raw FOV itself.
     * This directly affects to the FOV without being modified.
     * */
    public static class FOVModifier extends EntityViewRenderEvent
    {
        private double fov;

        public FOVModifier(GameRenderer renderer, Camera info, double renderPartialTicks, double fov) {
            super(renderer, info, renderPartialTicks);
            this.setFOV(fov);
        }

        public double getFOV() {
            return fov;
        }

        public void setFOV(double fov) {
            this.fov = fov;
        }
    }
}
