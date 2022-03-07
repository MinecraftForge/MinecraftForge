/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.eventbus.api.Cancelable;

import com.mojang.blaze3d.shaders.FogShape;

/**
 * Event that hooks into GameRenderer, allowing any feature to customize visual attributes
 *  the player sees.
 */
public abstract class EntityViewRenderEvent extends net.minecraftforge.eventbus.api.Event
{
    private final GameRenderer renderer;
    private final Camera camera;
    private final double partialTicks;

    public EntityViewRenderEvent(GameRenderer renderer, Camera camera, double partialTicks)
    {
        this.renderer = renderer;
        this.camera = camera;
        this.partialTicks = partialTicks;
    }

    public GameRenderer getRenderer()
    {
        return renderer;
    }

    public Camera getCamera()
    {
        return camera;
    }

    public double getPartialTicks()
    {
        return partialTicks;
    }

    @Deprecated(forRemoval = true, since = "1.18.2")
    private static class FogEvent extends EntityViewRenderEvent
    {
        private final FogMode mode;
        @SuppressWarnings("resource")
        protected FogEvent(FogMode mode, Camera camera, double partialTick)
        {
            super(Minecraft.getInstance().gameRenderer, camera, partialTick);
            this.mode = mode;
        }

        public FogMode getMode() { return mode; }
    }

    /**
     * @deprecated Use RenderFogEvent. This event will be removed as the other event has better functionality.
     *
     * Event that allows any feature to customize the fog density the player sees.
     * NOTE: In order to make this event have an effect, you must cancel the event
     */
    @Cancelable
    @Deprecated(forRemoval = true, since = "1.18.2")
    public static class FogDensity extends FogEvent
    {
        private float density;

        public FogDensity(FogMode type, Camera camera, float partialTick, float density)
        {
            super(type, camera, partialTick);
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
     * This event allows for customization of parameters related to fog rendering. The plane distances are based on the player's render distance.
     * For the event to have an effect, you must cancel it.
     *
     * The FogMode is NOT customizable. It describes the type of fog being modified.
     * A FogMode of FOG_SKY is the skybox's fog.
     * A FogMode of FOG_TERRAIN is what the fog induced by render distance uses. This works best for reducing the camera's visibility.
     */
    @HasResult // TODO: remove in 1.19. Setting a result for this event has no effect.
    @Cancelable
    public static class RenderFogEvent extends FogEvent // TODO: In 1.19, change superclass to EntityViewRenderEvent
    {
        private final FogMode type;
        private float farPlaneDistance;
        private float nearPlaneDistance;
        private FogShape fogShape;

        /**
         * @deprecated Use other constructor with all the params. Will be removed in 1.19
         */
        @Deprecated(forRemoval = true, since = "1.18.2")
        public RenderFogEvent(FogMode type, Camera camera, float partialTicks, float distance)
        {
            this(type, camera, partialTicks, -8f, distance, FogShape.SPHERE);
        }

        public RenderFogEvent(FogMode type, Camera camera, float partialTicks, float nearPlaneDistance, float farPlaneDistance, FogShape fogShape)
        {
            super(type, camera, partialTicks);
            this.type = type;
            setFarPlaneDistance(farPlaneDistance);
            setNearPlaneDistance(nearPlaneDistance);
            setFogShape(fogShape);
        }

        public FogMode getMode()
        {
            return type;
        }

        public float getFarPlaneDistance()
        {
            return farPlaneDistance;
        }

        public float getNearPlaneDistance()
        {
            return nearPlaneDistance;
        }

        public FogShape getFogShape()
        {
            return fogShape;
        }

        public void setFarPlaneDistance(float distance)
        {
            farPlaneDistance = distance;
        }

        public void setNearPlaneDistance(float distance)
        {
            nearPlaneDistance = distance;
        }

        public void setFogShape(FogShape shape)
        {
            fogShape = shape;
        }

        public void scaleFarPlaneDistance(float factor)
        {
            farPlaneDistance *= factor;
        }

        public void scaleNearPlaneDistance(float factor)
        {
            nearPlaneDistance *= factor;
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
        public FogColors(Camera camera, float partialTicks, float red, float green, float blue)
        {
            super(Minecraft.getInstance().gameRenderer, camera, partialTicks);
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

        public CameraSetup(GameRenderer renderer, Camera camera, double renderPartialTicks, float yaw, float pitch, float roll)
        {
            super(renderer, camera, renderPartialTicks);
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
    public static class FieldOfView extends EntityViewRenderEvent
    {
        private double fov;

        public FieldOfView(GameRenderer renderer, Camera camera, double renderPartialTicks, double fov) {
            super(renderer, camera, renderPartialTicks);
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
