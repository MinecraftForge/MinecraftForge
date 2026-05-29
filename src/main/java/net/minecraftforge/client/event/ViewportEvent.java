/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.world.level.material.FogType;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector4f;
import org.jspecify.annotations.NullMarked;

/**
 * Fired for hooking into the entity view rendering in {@link GameRenderer}.
 * These can be used for customizing the visual features visible to the player.
 * See the various subclasses for listening to different features.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see RenderFog
 * @see ComputeFogColor
 * @see ComputeCameraAngles
 * @see ComputeFov
 */
public sealed interface ViewportEvent {
    /**
     * {@return the game renderer}
     */
    GameRenderer getRenderer();

    /**
     * {@return the camera information}
     */
    Camera getCamera();

    /**
     * {@return the partial tick}
     */
    float getPartialTick();

    /**
     * Fired for <b>rendering</b> custom fog. The plane distances are based on the player's render distance.
     *
     * <p>This event is {@linkplain Cancellable cancellable}.<br>
     * The event must be cancelled for any changes to the plane distances to take effect.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record RenderFog(
            GameRenderer getRenderer,
            Camera getCamera,
            float getPartialTick,
            FogType getType,
            FogData getData,
            Vector4f getColor
    ) implements Cancellable, ViewportEvent, RecordEvent {
        public static final CancellableEventBus<RenderFog> BUS = CancellableEventBus.create(RenderFog.class);

        @SuppressWarnings("resource")
        @ApiStatus.Internal
        public RenderFog(FogType type, Camera camera, float partialTicks, FogData data, Vector4f color) {
            this(Minecraft.getInstance().gameRenderer, camera, partialTicks, type, data, color);
        }

        @ApiStatus.Internal
        public RenderFog {}

        /**
         * {@return the type of fog being rendered}
         */
        public FogType getType() {
            return getType;
        }

        /**
         * {@return the distance to the far plane where the fog ends}
         */
        public float getFarPlaneDistance() {
            return this.getData().renderDistanceEnd;
        }

        /**
         * {@return the distance to the near plane where the fog starts}
         */
        public float getNearPlaneDistance() {
            return this.getData().renderDistanceStart;
        }

        /**
         * Sets the distance to the far plane of the fog.
         *
         * @param distance the new distance to the far place
         * @see #scaleFarPlaneDistance(float)
         */
        public void setFarPlaneDistance(float distance) {
            getData().renderDistanceEnd = distance;
        }

        /**
         * Sets the distance to the near plane of the fog.
         *
         * @param distance the new distance to the near plane
         * @see #scaleNearPlaneDistance(float)
         */
        public void setNearPlaneDistance(float distance) {
            getData().renderDistanceStart = distance;
        }

        /**
         * Scales the distance to the far plane of the fog by a given factor.
         *
         * @param factor the factor to scale the far plane distance by
         */
        public void scaleFarPlaneDistance(float factor) {
            getData().renderDistanceEnd *= factor;
        }

        /**
         * Scales the distance to the near plane of the fog by a given factor.
         *
         * @param factor the factor to scale the near plane distance by
         */
        public void scaleNearPlaneDistance(float factor) {
            getData().renderDistanceStart *= factor;
        }
    }

    /**
     * Fired for customizing the <b>color</b> of the fog visible to the player.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    final class ComputeFogColor extends MutableEvent implements ViewportEvent {
        public static final EventBus<ComputeFogColor> BUS = EventBus.create(ComputeFogColor.class);

        private final GameRenderer renderer;
        private final Camera camera;
        private final float partialTicks;
        private float red;
        private float green;
        private float blue;

        @SuppressWarnings("resource")
        @ApiStatus.Internal
        public ComputeFogColor(Camera camera, float partialTicks, float red, float green, float blue) {
            this.renderer = Minecraft.getInstance().gameRenderer;
            this.camera = camera;
            this.partialTicks = partialTicks;
            this.setRed(red);
            this.setGreen(green);
            this.setBlue(blue);
        }

        @Override
        public GameRenderer getRenderer() {
            return renderer;
        }

        @Override
        public Camera getCamera() {
            return camera;
        }

        @Override
        public float getPartialTick() {
            return partialTicks;
        }

        /**
         * {@return the red color value of the fog}
         */
        public float getRed() {
            return red;
        }

        /**
         * Sets the new red color value of the fog.
         *
         * @param red the new red color value
         */
        public void setRed(float red) {
            this.red = red;
        }

        /**
         * {@return the green color value of the fog}
         */
        public float getGreen() {
            return green;
        }

        /**
         * Sets the new green color value of the fog.
         *
         * @param green the new blue color value
         */
        public void setGreen(float green) {
            this.green = green;
        }

        /**
         * {@return the blue color value of the fog}
         */
        public float getBlue() {
            return blue;
        }

        /**
         * Sets the new blue color value of the fog.
         *
         * @param blue the new blue color value
         */
        public void setBlue(float blue) {
            this.blue = blue;
        }
    }

    /**
     * Fired to allow altering the angles of the player's camera.
     * This can be used to alter the player's view for different effects, such as applying roll.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @NullMarked
    final class ComputeCameraAngles extends MutableEvent implements ViewportEvent {
        public static final EventBus<ComputeCameraAngles> BUS = EventBus.create(ComputeCameraAngles.class);

        private final GameRenderer renderer;
        private final Camera camera;
        private final float partialTicks;
        private float yaw;
        private float pitch;
        private float roll;

        @ApiStatus.Internal
        public ComputeCameraAngles(GameRenderer renderer, Camera camera, float renderPartialTicks, float yaw, float pitch, float roll) {
            this.renderer = renderer;
            this.camera = camera;
            this.partialTicks = renderPartialTicks;
            this.setYaw(yaw);
            this.setPitch(pitch);
            this.setRoll(roll);
        }

        @Override
        public GameRenderer getRenderer() {
            return renderer;
        }

        @Override
        public Camera getCamera() {
            return camera;
        }

        @Override
        public float getPartialTick() {
            return partialTicks;
        }

        /**
         * {@return the yaw of the player's camera}
         */
        public float getYaw() {
            return yaw;
        }

        /**
         * Sets the yaw of the player's camera.
         *
         * @param yaw the new yaw
         */
        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        /**
         * {@return the pitch of the player's camera}
         */
        public float getPitch() {
            return pitch;
        }

        /**
         * Sets the pitch of the player's camera.
         *
         * @param pitch the new pitch
         */
        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        /**
         * {@return the roll of the player's camera}
         */
        public float getRoll() {
            return roll;
        }

        /**
         * Sets the roll of the player's camera.
         *
         * @param roll the new roll
         */
        public void setRoll(float roll) {
            this.roll = roll;
        }
    }

    /**
     * Fired for altering the raw field of view (FOV).
     * This is after the FOV settings are applied, and before modifiers such as the Nausea effect.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see ComputeFovModifierEvent
     */
    @NullMarked
    final class ComputeFov extends MutableEvent implements ViewportEvent {
        public static final EventBus<ComputeFov> BUS = EventBus.create(ComputeFov.class);

        private final GameRenderer renderer;
        private final Camera camera;
        private final float partialTicks;
        private final boolean usedConfiguredFov;
        private float fov;

        @ApiStatus.Internal
        public ComputeFov(GameRenderer renderer, Camera camera, float renderPartialTicks, float fov, boolean usedConfiguredFov) {
            this.renderer = renderer;
            this.camera = camera;
            this.partialTicks = renderPartialTicks;
            this.usedConfiguredFov = usedConfiguredFov;
            this.setFOV(fov);
        }

        @Override
        public GameRenderer getRenderer() {
            return renderer;
        }

        @Override
        public Camera getCamera() {
            return camera;
        }

        @Override
        public float getPartialTick() {
            return partialTicks;
        }

        /**
         * {@return the raw field of view value}
         */
        public float getFOV() {
            return fov;
        }

        /**
         * Sets the field of view value.
         *
         * @param fov the new FOV value
         */
        public void setFOV(float fov) {
            this.fov = fov;
        }

        /**
         * {@return whether the base fov value started with a constant or was sourced from the fov set in the options}
         */
        public boolean usedConfiguredFov() {
            return usedConfiguredFov;
        }
    }
}
