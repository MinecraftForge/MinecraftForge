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
     * @param level             The current level (client-side).
     * @param partialTicks      Progress between ticks.
     * @param skyDarken         Current darkness of the sky.
     * @param blockLightFlicker Block light flicker factor.
     * @param modifiedSkyLight  Sky light brightness factor (accounting for sky darkness).
     * @param pixelX            X-coordinate of the lightmap texture (block).
     * @param pixelY            Y-coordinate of the lightmap texture (sky).
     * @param colors            The color values that will be used: [r, g, b].
     * @see LightTexture#updateLightTexture(float)
     * @deprecated Use {@link #adjustLightmapColors(ClientLevel, float, int, int, float, float, float, float, float, float, float, Vector3f)}.
     */
    @Deprecated(forRemoval = true, since = "1.20.1")
    default void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float blockLightFlicker, float modifiedSkyLight, int pixelX, int pixelY, Vector3f colors) {}

    /**
     * Allows for manipulating the coloring of the lightmap texture.
     * Will be called for each 16*16 combination of sky/block light values.
     *
     * @param level              The current level (client-side).
     * @param partialTicks       Progress between ticks.
     * @param pixelX             X-coordinate of the lightmap texture (block).
     * @param pixelY             Y-coordinate of the lightmap texture (sky).
     * @param blockLightRed      Block light brightness factor (red color).
     * @param blockLightGreen    Block light brightness factor (green color).
     * @param blockLightBlue     Block light brightness factor (blue color).
     * @param blockLightFlicker  Block light brightness flicker factor.
     * @param unmodifiedSkyLight Sky light brightness factor (without additional sky darkness calculation).
     * @param modifiedSkyLight   Sky light brightness factor (accounting for sky darkness).
     * @param skyDarken          Current darkness of the sky.
     * @param colors             The color values that will be used: [r, g, b].
     * @see LightTexture#updateLightTexture(float)
     */
    default void adjustLightmapColors(ClientLevel level, float partialTicks, int pixelX, int pixelY, float blockLightRed, float blockLightGreen, float blockLightBlue, float blockLightFlicker, float unmodifiedSkyLight, float modifiedSkyLight, float skyDarken, Vector3f colors)
    {
        this.adjustLightmapColors(level, partialTicks, skyDarken, blockLightFlicker, modifiedSkyLight, pixelX, pixelY, colors);
    }
}
