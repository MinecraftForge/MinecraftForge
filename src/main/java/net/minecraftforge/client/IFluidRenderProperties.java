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

package net.minecraftforge.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nullable;

/**
 * The following methods are meant to be used for specifically handling rendering via the vanilla RenderSystem.
 * Or within the current bounds of where vanilla calls them from.
 */
public interface IFluidRenderProperties
{
    IFluidRenderProperties DUMMY = new IFluidRenderProperties()
    {
    };

    /**
     * Used to render the FluidOverlay while inside of a Fluid
     * @param mc The current {@link Minecraft} client instance
     * @param stack The current {@link PoseStack} instance
     */
    default void renderOverlay(Minecraft mc, PoseStack stack)
    {
        Player player = mc.player;
        FluidAttributes attr = player.getTouchingFluid().getType().getAttributes();
        @Nullable ResourceLocation overlayTexture = attr.getViewOverlayTexture();
        if (overlayTexture != null)
        {
            RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
            RenderSystem.enableTexture();
            RenderSystem.setShaderTexture(0, overlayTexture);
            BufferBuilder builder = Tesselator.getInstance().getBuilder();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float brightness = player.getBrightness();
            long color = Integer.toUnsignedLong(attr.getColor());
            float red = ((color >> 16) & 0xFF) / 255.0F * brightness;
            float green = ((color >> 8) & 0xFF) / 255.0F * brightness;
            float blue = (color & 0xFF) / 255.0F * brightness;
            float alpha = 0.1F;
            float uOffset = -player.getYRot() / 64.0F;
            float vOffset = player.getXRot() / 64.0F;
            Matrix4f matrix = stack.last().pose();
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            builder.vertex(matrix, -1.0F, -1.0F, -0.5F).color(red, green, blue, alpha).uv(4.0F + uOffset, 4.0F + vOffset).endVertex();
            builder.vertex(matrix, 1.0F, -1.0F, -0.5F).color(red, green, blue, alpha).uv(uOffset, 4.0F + vOffset).endVertex();
            builder.vertex(matrix, 1.0F, 1.0F, -0.5F).color(red, green, blue, alpha).uv(uOffset, vOffset).endVertex();
            builder.vertex(matrix, -1.0F, 1.0F, -0.5F).color(red, green, blue, alpha).uv(4.0F + uOffset, vOffset).endVertex();
            builder.end();
            BufferUploader.end(builder);
            RenderSystem.disableBlend();
        }
    }

    /**
     * Used to properly set the fog color into an Vector3f for the Shader system to interpret.
     *
     * @param camera The current {@link Camera} instance
     * @param partialTicks The current partial ticks represented as a float
     * @param level The current {@link ClientLevel}
     * @return Returns an {@link Vector3f} representation of the stored color value of the fluid the camera is currently inside of.
     */
    default Vector3f setFogColor(Camera camera, float partialTicks, ClientLevel level)
    {
        long color = Integer.toUnsignedLong(camera.getEntity().getTouchingFluid().getType().getAttributes().getColor());
        return new Vector3f(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
    }

    /**
     * Used to manually modify the current fog color while inside of a fluid.
     * This new fog color is represented as a {@link Vector3f} for the Shader system to interpret.
     *
     * @param camera The current {@link Camera} instance
     * @param partialTicks The current partial ticks represented as a float
     * @param level The current {@link ClientLevel}
     * @param red The red value represented as a float between 0-1
     * @param green The green value represented as a float between 0-1
     * @param blue The blue value represented as a float between 0-1
     * @return Returns an {@link Vector3f} representation of the stored color value of the fluid the camera is currently inside of.
     */
    default Vector3f modifyFogColor(Camera camera, float partialTicks, ClientLevel level, float red, float green, float blue)
    {
        return new Vector3f(red, green, blue);
    }

    /**
     * Used to properly setup the fluid fog for when you're inside of the fluid.
     *
     * @param camera The current {@link Camera} instance
     * @param fogMode The currently set {@link net.minecraft.client.renderer.FogRenderer.FogMode}
     * @param renderDistance The currently set renderDistance represented as a float value.
     */
    default void setupFog(Camera camera, FogRenderer.FogMode fogMode, float renderDistance)
    {
        float constant = 180.0F;
        if (camera.getEntity() instanceof LocalPlayer player) {
            constant *= player.areEyesInFluid() ? 1.0 : 0.25;
        }
        RenderSystem.setShaderFogStart(-8F);
        RenderSystem.setShaderFogEnd(constant * 0.5F);
        net.minecraftforge.client.ForgeHooksClient.onFogRender(fogMode, camera, renderDistance, constant);
    }
}
