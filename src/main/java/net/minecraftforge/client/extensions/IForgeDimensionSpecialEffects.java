/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Extension interface for {@link DimensionSpecialEffects}.
 */
public interface IForgeDimensionSpecialEffects
{
    private DimensionSpecialEffects self()
    {
        return (DimensionSpecialEffects) this;
    }

    /**
     * Renders the clouds of this dimension.
     *
     * @return true to prevent vanilla cloud rendering
     */
    default boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix)
    {
        return false;
    }

    /**
     * Renders the sky of this dimension.
     *
     * @return true to prevent vanilla sky rendering
     */
    default boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
    {
        return false;
    }

    /**
     * Renders the snow and rain effects of this dimension.
     *
     * @return true to prevent vanilla snow and rain rendering
     */
    default boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ)
    {
        return false;
    }

    /**
     * Ticks the rain of this dimension.
     *
     * @return true to prevent vanilla rain ticking
     */
    default boolean tickRain(ClientLevel level, int ticks, Camera camera)
    {
        return false;
    }

    /**
     * Allows for manipulating the coloring of the lightmap texture.
     * Will be called for each 16*16 combination of sky/block light values.
     *
     * @param level                The current level (client-side).
     * @param partialTicks         Progress between ticks.
     * @param skyDarken            Current darkness of the sky (can be used to calculate sky light).
     * @param blockLightRedFlicker Block light flicker factor (red color) (can be used to calculate block light).
     * @param skyLight             Sky light brightness (accounting for sky darkness).
     * @param pixelX               X-coordinate of the lightmap texture (block).
     * @param pixelY               Y-coordinate of the lightmap texture (sky).
     * @param colors               The color values that will be used: [r, g, b].
     * @see LightTexture#updateLightTexture(float)
     */
    default void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float blockLightRedFlicker, float skyLight, int pixelX, int pixelY, Vector3f colors) {}
}
