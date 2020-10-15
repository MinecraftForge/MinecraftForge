/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.debug.client.rendering;

import static org.lwjgl.opengl.GL11.GL_QUADS;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.border.BorderStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderFramebufferLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

@Mod("render_framebuffers_last_event_test")
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class FramebufferRenderLastEventTest
{
    static final ResourceLocation BORDER = new ResourceLocation("textures/misc/forcefield.png");
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onEvent(RenderFramebufferLastEvent event)
    {
        if (!ENABLED)
            return;

        Minecraft mc = Minecraft.getInstance();
        World world = mc.world;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        MatrixStack matrixStack = event.getMatrixStack();
        Vector3d view = mc.getRenderManager().info.getProjectedView();

        if (world == null)
            return;

        RenderTypes.getWeatherTarget().setupRenderState();
        renderBorder(buffer, matrixStack, view.getX(), view.getY(), view.getZ());
        RenderTypes.getWeatherTarget().clearRenderState();
    }

    // Example rendering something, this is a clone of the vanilla world border
    private static void renderBorder(BufferBuilder builder, MatrixStack matrixStack, double playerX, double playerY, double playerZ)
    {
        Minecraft mc = Minecraft.getInstance();
        int viewDistance = mc.gameSettings.renderDistanceChunks * 16;
        float centerX = 0;
        float centerZ = 0;
        float radius = 32;

        if (mc.player == null)
            return;

        BorderStatus status = BorderStatus.STATIONARY;

        float minX = centerX - radius;
        float maxX = centerX + radius;
        float minZ = centerZ - radius;
        float maxZ = centerZ + radius;

        if (playerX >= maxX - viewDistance || playerX <= minX + viewDistance || playerZ >= maxZ - viewDistance || playerZ <= minZ + viewDistance)
        {
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            mc.getTextureManager().bindTexture(BORDER);
            RenderSystem.depthMask(Minecraft.func_238218_y_());
            matrixStack.push();
            {
                matrixStack.translate(-playerX, -playerY, -playerZ);
                int borderColor = status.getColor();
                float red = (float) (borderColor >> 16 & 255) / 255.0F;
                float green = (float) (borderColor >> 8 & 255) / 255.0F;
                float blue = (float) (borderColor & 255) / 255.0F;
                RenderSystem.polygonOffset(-3.0F, -3.0F);
                RenderSystem.enablePolygonOffset();
                RenderSystem.defaultAlphaFunc();
                RenderSystem.enableAlphaTest();
                RenderSystem.disableCull();
                float offset = (float) (Util.milliTime() % 3000L) / 3000.0F;

                builder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
                Matrix4f matrix4f = matrixStack.getLast().getMatrix();

                float d8 = Math.max(MathHelper.floor(playerZ - viewDistance), minZ);
                float d9 = Math.min(MathHelper.ceil(playerZ + viewDistance), maxZ);

                if (playerX > maxX - viewDistance)
                {
                    float f7 = 0.0F;

                    for (float d10 = d8; d10 < d9; f7 += 0.5F)
                    {
                        float d11 = Math.min(1.0f, d9 - d10);
                        float f8 = d11 * 0.5F;
                        builder.pos(matrix4f, maxX, 256.0f, d10).color(red, green, blue, 1.0f).tex(offset + f7, offset + 0.0F).endVertex();
                        builder.pos(matrix4f, maxX, 256.0f, d10 + d11).color(red, green, blue, 1.0f).tex(offset + f8 + f7, offset + 0.0F).endVertex();
                        builder.pos(matrix4f, maxX, 0.0f, d10 + d11).color(red, green, blue, 1.0f).tex(offset + f8 + f7, offset + 128.0F).endVertex();
                        builder.pos(matrix4f, maxX, 0.0f, d10).color(red, green, blue, 1.0f).tex(offset + f7, offset + 128.0F).endVertex();
                        ++d10;
                    }
                }

                if (playerX < minX + viewDistance)
                {
                    float f9 = 0.0F;

                    for (float d12 = d8; d12 < d9; f9 += 0.5F)
                    {
                        float d15 = Math.min(1.0f, d9 - d12);
                        float f12 = d15 * 0.5F;
                        builder.pos(matrix4f, minX, 256.0f, d12).color(red, green, blue, 1.0f).tex(offset + f9, offset + 0.0F).endVertex();
                        builder.pos(matrix4f, minX, 256.0f, d12 + d15).color(red, green, blue, 1.0f).tex(offset + f12 + f9, offset + 0.0F).endVertex();
                        builder.pos(matrix4f, minX, 0.0f, d12 + d15).color(red, green, blue, 1.0f).tex(offset + f12 + f9, offset + 128.0F).endVertex();
                        builder.pos(matrix4f, minX, 0.0f, d12).color(red, green, blue, 1.0f).tex(offset + f9, offset + 128.0F).endVertex();
                        ++d12;
                    }
                }

                d8 = Math.max(MathHelper.floor(playerX - viewDistance), minX);
                d9 = Math.min(MathHelper.ceil(playerX + viewDistance), maxX);

                if (playerZ > maxZ - viewDistance)
                {
                    float f10 = 0.0F;

                    for (float d13 = d8; d13 < d9; f10 += 0.5F)
                    {
                        float d16 = Math.min(1.0f, d9 - d13);
                        float f13 = d16 * 0.5F;
                        builder.pos(matrix4f, d13, 256.0f, maxZ).color(red, green, blue, 1.0f).tex(offset + f10, offset + 0.0F).endVertex();
                        builder.pos(matrix4f, d13 + d16, 256.0f, maxZ).color(red, green, blue, 1.0f).tex(offset + f13 + f10, offset + 0.0F).endVertex();
                        builder.pos(matrix4f, d13 + d16, 0.0f, maxZ).color(red, green, blue, 1.0f).tex(offset + f13 + f10, offset + 128.0F).endVertex();
                        builder.pos(matrix4f, d13, 0.0f, maxZ).color(red, green, blue, 1.0f).tex(offset + f10, offset + 128.0F).endVertex();
                        ++d13;
                    }
                }

                if (playerZ < minZ + viewDistance)
                {
                    float f11 = 0.0F;

                    for (float d14 = d8; d14 < d9; f11 += 0.5F)
                    {
                        float d17 = Math.min(1.0f, d9 - d14);
                        float f14 = d17 * 0.5F;
                        builder.pos(matrix4f, d14, 256.0f, minZ).color(red, green, blue, 1.0f).tex(offset + f11, offset + 0.0F).endVertex();
                        builder.pos(matrix4f, d14 + d17, 256.0f, minZ).color(red, green, blue, 1.0f).tex(offset + f14 + f11, offset + 0.0F).endVertex();
                        builder.pos(matrix4f, d14 + d17, 0.0f, minZ).color(red, green, blue, 1.0f).tex(offset + f14 + f11, offset + 128.0F).endVertex();
                        builder.pos(matrix4f, d14, 0.0f, minZ).color(red, green, blue, 1.0f).tex(offset + f11, offset + 128.0F).endVertex();
                        ++d14;
                    }
                }

                Tessellator.getInstance().draw();

                RenderSystem.enableCull();
                RenderSystem.disableAlphaTest();
                RenderSystem.polygonOffset(0.0F, 0.0F);
                RenderSystem.disablePolygonOffset();
                RenderSystem.enableAlphaTest();
                RenderSystem.disableBlend();
            }
            matrixStack.pop();
            RenderSystem.depthMask(true);
        }
    }

    private static final class RenderTypes extends RenderType
    {
        private RenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn)
        {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
        }

        private static RenderState getWeatherTarget()
        {
            return field_239238_U_;
        }
    }
}
