/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.model.FluidModel;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * An interface which defines properties relating to how a {@link FluidType}
 * should render.
 *
 * <p>Note: This does not define any data regarding {@link RenderType}s as those
 * are specified by the Fluid via {@link ItemBlockRenderTypes#setRenderLayer(Fluid, RenderType)}
 * in the {@link FMLClientSetupEvent}.
 */
public interface IFluidTypeRenderProperties
{
    /**
     * A dummy instance returned when no render properties are specified.
     */
    IFluidTypeRenderProperties DUMMY = new IFluidTypeRenderProperties()
    {
    };

    /* Default Accessors */

    /**
     * Returns the tint applied to the fluid's textures.
     *
     * <p>The result represents a 32-bit integer where each 8-bits represent
     * the alpha, red, green, and blue channel respectively.
     *
     * @return the tint applied to the fluid's textures in ARGB format
     */
    default int getColorTint()
    {
        return 0xFFFFFFFF;
    }

    /**
     * Returns the reference of the texture to apply to a source fluid.
     *
     * <p>This should return a reference to the texture and not the actual
     * texture itself (e.g. {@code minecraft:block/water_still} will point to
     * {@code assets/minecraft/textures/block/water_still.png}).
     *
     * <p>Important: This method should only return {@code null} for {@link Fluids#EMPTY}.
     * All other implementations must define this property.
     *
     * @return the reference of the texture to apply to a source fluid
     */
    default ResourceLocation getStillTexture()
    {
        return null;
    }

    /**
     * Returns the reference of the texture to apply to a flowing fluid.
     *
     * <p>This should return a reference to the texture and not the actual
     * texture itself (e.g. {@code minecraft:block/water_flow} will point to
     * {@code assets/minecraft/textures/block/water_flow.png}).
     *
     * <p>Important: This method should only return {@code null} for {@link Fluids#EMPTY}.
     * All other implementations must define this property.
     *
     * @return the reference of the texture to apply to a flowing fluid
     */
    default ResourceLocation getFlowingTexture()
    {
        return null;
    }

    /**
     * Returns the reference of the texture to apply to a fluid directly touching
     * a non-opaque block other than air. If no reference is specified, either
     * {@code #getStillTexture} or {@code #getFlowingTexture} will be applied
     * instead.
     *
     * <p>This should return a reference to the texture and not the actual
     * texture itself (e.g. {@code minecraft:block/water_overlay} will point to
     * {@code assets/minecraft/textures/block/water_overlay.png}).
     *
     * @return the reference of the texture to apply to a fluid directly touching
     *         a non-opaque block
     */
    @Nullable
    default ResourceLocation getOverlayTexture()
    {
        return null;
    }

    /**
     * Returns a stream of textures applied to a fluid.
     *
     * <p>This is used by the {@link FluidModel} to load in all textures that
     * can be applied on reload.
     *
     * @return a stream of textures applied to a fluid
     */
    default Stream<ResourceLocation> getTextures()
    {
        return Stream.of(this.getStillTexture(), this.getFlowingTexture(), this.getOverlayTexture())
                .filter(Objects::nonNull);
    }

    /**
     * Returns the location of the texture to apply to the camera when it is
     * within the fluid. If no location is specified, no overlay will be applied.
     *
     * <p>This should return a location to the texture and not a reference
     * (e.g. {@code minecraft:textures/misc/underwater.png} will use the texture
     * at {@code assets/minecraft/textures/misc/underwater.png}).
     *
     * @param mc the client instance
     * @return the location of the texture to apply to the camera when it is
     *         within the fluid
     */
    @Nullable
    default ResourceLocation getRenderOverlayTexture(Minecraft mc)
    {
        return null;
    }

    /**
     * Renders {@code #getRenderOverlayTexture} onto the camera when within
     * the fluid.
     *
     * @param mc the client instance
     * @param stack the transformations representing the current rendering position
     */
    default void renderOverlay(Minecraft mc, PoseStack stack)
    {
        ResourceLocation texture = this.getRenderOverlayTexture(mc);
        if (texture == null) return;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, texture);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        BlockPos playerEyePos = new BlockPos(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());
        float brightness = LightTexture.getBrightness(mc.player.level.dimensionType(), mc.player.level.getMaxLocalRawBrightness(playerEyePos));
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(brightness, brightness, brightness, 0.1F);
        float uOffset = -mc.player.getYRot() / 64.0F;
        float vOffset = mc.player.getXRot() / 64.0F;
        Matrix4f pose = stack.last().pose();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(pose, -1.0F, -1.0F, -0.5F).uv(4.0F + uOffset, 4.0F + vOffset).endVertex();
        buffer.vertex(pose, 1.0F, -1.0F, -0.5F).uv(uOffset, 4.0F + vOffset).endVertex();
        buffer.vertex(pose, 1.0F, 1.0F, -0.5F).uv(uOffset, vOffset).endVertex();
        buffer.vertex(pose, -1.0F, 1.0F, -0.5F).uv(4.0F + uOffset, vOffset).endVertex();
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }

    /**
     * Modifies the color of the fog when the camera is within the fluid.
     *
     * <p>The result expects a three float vector representing the red, green,
     * and blue channels respectively. Each channel should be between [0,1].
     *
     * @param camera the camera instance
     * @param partialTick the delta time of where the current frame is within a tick
     * @param level the level the camera is located in
     * @param renderDistance the render distance of the client
     * @param darkenWorldAmount the amount to darken the world by
     * @param fluidFogColor the current color of the fog
     * @return the color of the fog
     */
    @NotNull
    default Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
    {
        return fluidFogColor;
    }

    /**
     * Modifies how the fog is currently being rendered when the camera is
     * within a fluid.
     *
     * @param camera the camera instance
     * @param mode the type of fog being rendered
     * @param renderDistance the render distance of the client
     * @param partialTick the delta time of where the current frame is within a tick
     * @param nearDistance the near plane of where the fog starts to render
     * @param farDistance the far plane of where the fog ends rendering
     * @param shape the shape of the fog being rendered
     */
    default void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
    {
    }

    /* Level-Based Accessors */

    /**
     * Returns the reference of the texture to apply to a source fluid.
     *
     * <p>This should return a reference to the texture and not the actual
     * texture itself (e.g. {@code minecraft:block/water_still} will point to
     * {@code assets/minecraft/textures/block/water_still.png}).
     *
     * <p>Important: This method should only return {@code null} for {@link Fluids#EMPTY}.
     * All other implementations must define this property.
     *
     * @param state the state of the fluid
     * @param getter the getter the fluid can be obtained from
     * @param pos the position of the fluid
     * @return the reference of the texture to apply to a source fluid
     */
    default ResourceLocation getStillTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos)
    {
        return this.getStillTexture();
    }

    /**
     * Returns the reference of the texture to apply to a flowing fluid.
     *
     * <p>This should return a reference to the texture and not the actual
     * texture itself (e.g. {@code minecraft:block/water_flow} will point to
     * {@code assets/minecraft/textures/block/water_flow.png}).
     *
     * <p>Important: This method should only return {@code null} for {@link Fluids#EMPTY}.
     * All other implementations must define this property.
     *
     * @param state the state of the fluid
     * @param getter the getter the fluid can be obtained from
     * @param pos the position of the fluid
     * @return the reference of the texture to apply to a flowing fluid
     */
    default ResourceLocation getFlowingTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos)
    {
        return this.getFlowingTexture();
    }

    /**
     * Returns the reference of the texture to apply to a fluid directly touching
     * a non-opaque block other than air. If no reference is specified, either
     * {@code #getStillTexture} or {@code #getFlowingTexture} will be applied
     * instead.
     *
     * <p>This should return a reference to the texture and not the actual
     * texture itself (e.g. {@code minecraft:block/water_overlay} will point to
     * {@code assets/minecraft/textures/block/water_overlay.png}).
     *
     * @param state the state of the fluid
     * @param getter the getter the fluid can be obtained from
     * @param pos the position of the fluid
     * @return the reference of the texture to apply to a fluid directly touching
     *         a non-opaque block
     */
    default ResourceLocation getOverlayTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos)
    {
        return this.getOverlayTexture();
    }


    /**
     * Returns the tint applied to the fluid's textures.
     *
     * <p>The result represents a 32-bit integer where each 8-bits represent
     * the alpha, red, green, and blue channel respectively.
     *
     * @param state the state of the fluid
     * @param getter the getter the fluid can be obtained from
     * @param pos the position of the fluid
     * @return the tint applied to the fluid's textures in ARGB format
     */
    default int getColorTint(FluidState state, BlockAndTintGetter getter, BlockPos pos)
    {
        return this.getColorTint();
    }

    /* Stack-Based Accessors */


    /**
     * Returns the tint applied to the fluid's textures.
     *
     * <p>The result represents a 32-bit integer where each 8-bits represent
     * the alpha, red, green, and blue channel respectively.
     *
     * @param stack the stack the fluid is in
     * @return the tint applied to the fluid's textures in ARGB format
     */
    default int getColorTint(FluidStack stack)
    {
        return this.getColorTint();
    }

    /**
     * Returns the reference of the texture to apply to a source fluid.
     *
     * <p>This should return a reference to the texture and not the actual
     * texture itself (e.g. {@code minecraft:block/water_still} will point to
     * {@code assets/minecraft/textures/block/water_still.png}).
     *
     * <p>Important: This method should only return {@code null} for {@link Fluids#EMPTY}.
     * All other implementations must define this property.
     *
     * @param stack the stack the fluid is in
     * @return the reference of the texture to apply to a source fluid
     */
    default ResourceLocation getStillTexture(FluidStack stack)
    {
        return this.getStillTexture();
    }

    /**
     * Returns the reference of the texture to apply to a flowing fluid.
     *
     * <p>This should return a reference to the texture and not the actual
     * texture itself (e.g. {@code minecraft:block/water_flow} will point to
     * {@code assets/minecraft/textures/block/water_flow.png}).
     *
     * <p>Important: This method should only return {@code null} for {@link Fluids#EMPTY}.
     * All other implementations must define this property.
     *
     * @param stack the stack the fluid is in
     * @return the reference of the texture to apply to a flowing fluid
     */
    default ResourceLocation getFlowingTexture(FluidStack stack)
    {
        return this.getFlowingTexture();
    }

    /**
     * Returns the reference of the texture to apply to a fluid directly touching
     * a non-opaque block other than air. If no reference is specified, either
     * {@code #getStillTexture} or {@code #getFlowingTexture} will be applied
     * instead.
     *
     * <p>This should return a reference to the texture and not the actual
     * texture itself (e.g. {@code minecraft:block/water_overlay} will point to
     * {@code assets/minecraft/textures/block/water_overlay.png}).
     *
     * @param stack the stack the fluid is in
     * @return the reference of the texture to apply to a fluid directly touching
     *         a non-opaque block
     */
    default ResourceLocation getOverlayTexture(FluidStack stack)
    {
        return this.getOverlayTexture();
    }
}
