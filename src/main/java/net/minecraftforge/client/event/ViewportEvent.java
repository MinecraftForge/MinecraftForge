/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired for hooking into the entity view rendering in {@link GameRenderer}.
 * These can be used for customizing the visual features visible to the player.
 * See the various subclasses for listening to different features.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see RenderFog
 * @see ComputeFogColor
 * @see ComputeCameraAngles
 * @see ComputeFov
 */
public abstract class ViewportEvent extends Event
{
    private final GameRenderer renderer;
    private final Camera camera;
    private final double partialTick;

    @ApiStatus.Internal
    public ViewportEvent(GameRenderer renderer, Camera camera, double partialTick)
    {
        this.renderer = renderer;
        this.camera = camera;
        this.partialTick = partialTick;
    }

    /**
     * {@return the game renderer}
     */
    public GameRenderer getRenderer()
    {
        return renderer;
    }

    /**
     * {@return the camera information}
     */
    public Camera getCamera()
    {
        return camera;
    }

    /**
     * {@return the partial tick}
     */
    public double getPartialTick()
    {
        return partialTick;
    }

    /**
     * Fired for <b>rendering</b> custom fog. The plane distances are based on the player's render distance.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and {@linkplain HasResult has a result}. <br/>
     * The event must be cancelled for any changes to the plane distances to take effect.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @Cancelable
    public static class RenderFog extends ViewportEvent
    {
        private final FogMode mode;
        private final FogType type;
        private float farPlaneDistance;
        private float nearPlaneDistance;
        private FogShape fogShape;

        @ApiStatus.Internal
        public RenderFog(FogMode mode, FogType type, Camera camera, float partialTicks, float nearPlaneDistance, float farPlaneDistance, FogShape fogShape)
        {
            super(Minecraft.getInstance().gameRenderer, camera, partialTicks);
            this.mode = mode;
            this.type = type;
            setFarPlaneDistance(farPlaneDistance);
            setNearPlaneDistance(nearPlaneDistance);
            setFogShape(fogShape);
        }

        /**
         * {@return the mode of fog being rendered}
         */
        public FogMode getMode()
        {
            return mode;
        }

        /**
         * {@return the type of fog being rendered}
         */
        public FogType getType()
        {
            return type;
        }

        /**
         * {@return the distance to the far plane where the fog ends}
         */
        public float getFarPlaneDistance()
        {
            return farPlaneDistance;
        }

        /**
         * {@return the distance to the near plane where the fog starts}
         */
        public float getNearPlaneDistance()
        {
            return nearPlaneDistance;
        }

        /**
         * {@return the shape of the fog being rendered}
         */
        public FogShape getFogShape()
        {
            return fogShape;
        }

        /**
         * Sets the distance to the far plane of the fog.
         *
         * @param distance the new distance to the far place
         * @see #scaleFarPlaneDistance(float)
         */
        public void setFarPlaneDistance(float distance)
        {
            farPlaneDistance = distance;
        }

        /**
         * Sets the distance to the near plane of the fog.
         *
         * @param distance the new distance to the near plane
         * @see #scaleNearPlaneDistance(float)
         */
        public void setNearPlaneDistance(float distance)
        {
            nearPlaneDistance = distance;
        }

        /**
         * Sets the new shape of the fog being rendered. The new shape will only take effect if the event is cancelled.
         *
         * @param shape the new shape of the fog
         */
        public void setFogShape(FogShape shape)
        {
            fogShape = shape;
        }

        /**
         * Scales the distance to the far plane of the fog by a given factor.
         *
         * @param factor the factor to scale the far plane distance by
         */
        public void scaleFarPlaneDistance(float factor)
        {
            farPlaneDistance *= factor;
        }

        /**
         * Scales the distance to the near plane of the fog by a given factor.
         *
         * @param factor the factor to scale the near plane distance by
         */
        public void scaleNearPlaneDistance(float factor)
        {
            nearPlaneDistance *= factor;
        }
    }

    /**
     * Fired for customizing the <b>color</b> of the fog visible to the player.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class ComputeFogColor extends ViewportEvent
    {
        private float red;
        private float green;
        private float blue;

        @ApiStatus.Internal
        public ComputeFogColor(Camera camera, float partialTicks, float red, float green, float blue)
        {
            super(Minecraft.getInstance().gameRenderer, camera, partialTicks);
            this.setRed(red);
            this.setGreen(green);
            this.setBlue(blue);
        }

        /**
         * {@return the red color value of the fog}
         */
        public float getRed()
        {
            return red;
        }

        /**
         * Sets the new red color value of the fog.
         *
         * @param red the new red color value
         */
        public void setRed(float red)
        {
            this.red = red;
        }

        /**
         * {@return the green color value of the fog}
         */
        public float getGreen()
        {
            return green;
        }

        /**
         * Sets the new green color value of the fog.
         *
         * @param green the new blue color value
         */
        public void setGreen(float green)
        {
            this.green = green;
        }

        /**
         * {@return the blue color value of the fog}
         */
        public float getBlue()
        {
            return blue;
        }

        /**
         * Sets the new blue color value of the fog.
         *
         * @param blue the new blue color value
         */
        public void setBlue(float blue)
        {
            this.blue = blue;
        }
    }

    /**
     * Fired to allow altering the angles of the player's camera.
     * This can be used to alter the player's view for different effects, such as applying roll.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class ComputeCameraAngles extends ViewportEvent
    {
        private float yaw;
        private float pitch;
        private float roll;

        @ApiStatus.Internal
        public ComputeCameraAngles(GameRenderer renderer, Camera camera, double renderPartialTicks, float yaw, float pitch, float roll)
        {
            super(renderer, camera, renderPartialTicks);
            this.setYaw(yaw);
            this.setPitch(pitch);
            this.setRoll(roll);
        }

        /**
         * {@return the yaw of the player's camera}
         */
        public float getYaw()
        {
            return yaw;
        }

        /**
         * Sets the yaw of the player's camera.
         *
         * @param yaw the new yaw
         */
        public void setYaw(float yaw)
        {
            this.yaw = yaw;
        }

        /**
         * {@return the pitch of the player's camera}
         */
        public float getPitch()
        {
            return pitch;
        }

        /**
         * Sets the pitch of the player's camera.
         *
         * @param pitch the new pitch
         */
        public void setPitch(float pitch)
        {
            this.pitch = pitch;
        }

        /**
         * {@return the roll of the player's camera}
         */
        public float getRoll()
        {
            return roll;
        }

        /**
         * Sets the roll of the player's camera.
         *
         * @param roll the new roll
         */
        public void setRoll(float roll)
        {
            this.roll = roll;
        }
    }

    /**
     * Fired for altering the raw field of view (FOV).
     * This is after the FOV settings are applied, and before modifiers such as the Nausea effect.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see ComputeFovModifierEvent
     */
    public static class ComputeFov extends ViewportEvent
    {
        private final boolean usedConfiguredFov;
        private double fov;

        @ApiStatus.Internal
        public ComputeFov(GameRenderer renderer, Camera camera, double renderPartialTicks, double fov, boolean usedConfiguredFov)
        {
            super(renderer, camera, renderPartialTicks);
            this.usedConfiguredFov = usedConfiguredFov;
            this.setFOV(fov);
        }

        /**
         * {@return the raw field of view value}
         */
        public double getFOV()
        {
            return fov;
        }

        /**
         * Sets the field of view value.
         *
         * @param fov the new FOV value
         */
        public void setFOV(double fov)
        {
            this.fov = fov;
        }

        /**
         * {@return whether the base fov value started with a constant or was sourced from the fov set in the options}
         */
        public boolean usedConfiguredFov()
        {
            return usedConfiguredFov;
        }
    }
}
