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

import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.FOVModifierEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired for hooking into the entity view rendering in {@link GameRenderer}.
 * These can be used for customizing the visual features visible to the player.
 * See the various subclasses for listening to different features.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see EntityViewRenderEvent.FogEvent
 * @see EntityViewRenderEvent.FogDensity
 * @see EntityViewRenderEvent.RenderFogEvent
 * @see EntityViewRenderEvent.FogColors
 * @see EntityViewRenderEvent.CameraSetup
 * @see EntityViewRenderEvent.FieldOfView
 */
public abstract class EntityViewRenderEvent extends Event
{
    private final GameRenderer gameRenderer;
    private final Camera camera;
    private final double partialTick;

    public EntityViewRenderEvent(GameRenderer gameRenderer, Camera camera, double partialTick)
    {
        this.gameRenderer = gameRenderer;
        this.camera = camera;
        this.partialTick = partialTick;
    }

    /**
     * {@return the game renderer}
     */
    public GameRenderer getGameRenderer()
    {
        return gameRenderer;
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
     * Fired for customizing the different features of the fog visible to the player.
     * See the various subclasses to listen for specific features.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and do not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see EntityViewRenderEvent.FogDensity
     * @see EntityViewRenderEvent.RenderFogEvent
     */
    private static class FogEvent extends EntityViewRenderEvent
    {
        private final FogMode mode;

        @SuppressWarnings("resource")
        protected FogEvent(FogMode mode, Camera info, double renderPartialTicks)
        {
            super(Minecraft.getInstance().gameRenderer, info, renderPartialTicks);
            this.mode = mode;
        }

        /**
         * {@return the fog mode}
         */
        public FogMode getMode()
        {
            return mode;
        }
    }

    /**
     * Fired for customizing the <b>density</b> of the fog visible to the player.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. <br/>
     * <em>The event must be cancelled for the custom fog density to take effect. </em></p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see ForgeHooksClient#getFogDensity(FogMode, Camera, float, float)
     */
    @Cancelable
    public static class FogDensity extends FogEvent
    {
        private float density;

        public FogDensity(FogMode mode, Camera camera, float partialTick, float density)
        {
            super(mode, camera, partialTick);
            this.setDensity(density);
        }

        /**
         * {@return the density of the fog}
         */
        public float getDensity()
        {
            return density;
        }

        /**
         * Sets the new density of the fog, which only takes effect if the event is cancelled.
         *
         * @param density the new fog density
         */
        public void setDensity(float density)
        {
            this.density = density;
        }
    }

    /**
     * Fired for customizing the <b>rendering</b> of the fog visible to the player.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and {@linkplain HasResult has a result}. </p>
     * TODO: review if this event needs a result.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see ForgeHooksClient#onFogRender(FogMode, Camera, float, float)
     */
    @HasResult
    public static class RenderFogEvent extends FogEvent
    {
        private final float farPlaneDistance;

        public RenderFogEvent(FogMode mode, Camera camera, float partialTick, float distance)
        {
            super(mode, camera, partialTick);
            this.farPlaneDistance = distance;
        }

        // TODO: find out what this means
        public float getFarPlaneDistance()
        {
            return farPlaneDistance;
        }
    }

    /**
     * Fired for customizing the <b>color</b> of the fog visible to the player.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see FogRenderer#setupColor(Camera, float, ClientLevel, int, float)
     */
    public static class FogColors extends EntityViewRenderEvent
    {
        private float red;
        private float green;
        private float blue;

        @SuppressWarnings("resource")
        public FogColors(Camera camera, float partialTick, float red, float green, float blue)
        {
            super(Minecraft.getInstance().gameRenderer, camera, partialTick);
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
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see ForgeHooksClient#onCameraSetup(GameRenderer, Camera, float)
     */
    public static class CameraSetup extends EntityViewRenderEvent
    {
        private float yaw;
        private float pitch;
        private float roll;

        public CameraSetup(GameRenderer gameRenderer, Camera camera, double partialTick, float yaw, float pitch, float roll)
        {
            super(gameRenderer, camera, partialTick);
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
     * Fired for altering the raw field of vision (FOV).
     * This is after the FOV settings are applied, and before modifiers such as the Nausea effect.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see ForgeHooksClient#getFieldOfView(GameRenderer, Camera, double, double)
     * @see FOVModifierEvent
     */
    public static class FieldOfView extends EntityViewRenderEvent
    {
        private double fov;

        public FieldOfView(GameRenderer gameRenderer, Camera camera, double partialTick, double fov)
        {
            super(gameRenderer, camera, partialTick);
            this.setFOV(fov);
        }

        /**
         * {@return the raw field of vision (FOV) value}
         */
        public double getFOV()
        {
            return fov;
        }

        /**
         * Sets the field of vision (FOV) value.
         *
         * @param fov the new FOV value
         */
        public void setFOV(double fov)
        {
            this.fov = fov;
        }
    }
}
