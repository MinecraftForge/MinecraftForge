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

package net.minecraftforge.debug.client.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Sample applications of {@link RenderItemEvent}.
 */
@Mod(RenderItemEventTest.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = RenderItemEventTest.MODID)
public class RenderItemEventTest
{
    public static final String MODID = "render_item_event_test";

    /* Controls for the tests - USE_TEST will disable all other tests if false */
    public static boolean USE_TEST = true;
    public static boolean DISPLAY_CUSTOM_ITEMCOUNT = true;
    public static boolean DISPLAY_CUSTOM_DURABILITY = true;
    public static boolean DISPLAY_CUSTOM_COOLDOWN = true;
    public static boolean DISPLAY_EXTRA_DETAILS = true;

    /**
     * Sample usage of the {@link RenderItemEvent.Model} event.
     * <br>
     * If DISPLAY_CUSTOM_DURABILITY is true, this event handler will display the durability of applicable ItemStacks as
     * colored rectanges behind the rendered Items. The width of the rectangle represents is scaled according to the
     * stack durability.
     */
    @SubscribeEvent
    public static void onRenderItemModel(RenderItemEvent.Model event)
    {
        if (!USE_TEST) return;

        if (DISPLAY_CUSTOM_DURABILITY)
        {
            ItemStack stack = event.getItemStack();

            if (stack.isDamageableItem())
            {
                double durabilityScale = 1.0D - stack.getItem().getDurabilityForDisplay(stack);
                int color = stack.getItem().getRGBDurabilityForDisplay(stack);

                if (durabilityScale < 1.0D)
                {
                    MatrixStack matrixStack = event.getMatrixStack();

                    int red = color >> 16 & 255, green = color >> 8 & 255, blue = color & 255;

                    matrixStack.pushPose();
                    drawVertexRect(event.getRenderTypeBuffer(), -0.5, -0.5, durabilityScale, 1.0, red, green, blue, 100);
                    matrixStack.popPose();
                }
            }
        }
    }

    /**
     * Sample usage of the {@link RenderItemEvent.Overlay} event, and all of its different parts. See
     * {@link RenderItemEvent.Overlay.OverlayPart} for the possible different Overlay events.
     * <ul>
     *     <li>If DISPLAY_CUSTOM_DURABILITY is true, this event handler will cancel the rendering of the default
     *     durability bar in favor of the custom one drawn by
     *     {@link RenderItemEventTest#onRenderItemModel(RenderItemEvent.Model)}.</li>
     *     <li>If DISPLAY_CUSTOM_ITEMCOUNT is true, this event handler will cancel the rendering of the default item
     *     count and display the count of a stack using a progress bar similar to the default durability bar for tools.
     *     </li>
     *     <li>If DISPLAY_CUSTOM_COOLDOWN is true, this event handler will cancel the rendering of the default item
     *     cooldown and display the cooldown of applicable stacks using a progress bar similar to the default durability
     *     bar for tools.</li>
     *     <li>If DISPLAY_EXTRA_DETAILS is true, this event handler will draw a small gold square over the top left
     *     corner of all edible items using the {@link RenderItemEvent.Overlay} event designated by the extra overlay
     *     part, {@link RenderItemEvent.Overlay.OverlayPart#EXTRA}</li>
     * </ul>
     */
    @SubscribeEvent
    public static void onRenderItemOverlay(RenderItemEvent.Overlay event)
    {
        if (!USE_TEST) return;

        RenderItemEvent.Overlay.OverlayPart part = event.getOverlayPart();
        ItemStack stack = event.getItemStack();
        int x = event.getX(), y = event.getY();

        if (DISPLAY_CUSTOM_DURABILITY && part == RenderItemEvent.Overlay.OverlayPart.DURABILITY)
        {
            if (event.isCancelable()) event.setCanceled(true);
        }

        if (DISPLAY_CUSTOM_ITEMCOUNT && part == RenderItemEvent.Overlay.OverlayPart.COUNT)
        {
            if (event.isCancelable()) event.setCanceled(true);

            if (stack.getCount() != 1)
            {
                double scale = (double) stack.getCount() / (double) stack.getMaxStackSize();

                int width = (int) (scale * 13.0D);

                if (!stack.isDamageableItem())
                {
                    y += 2;
                }

                drawRect(x + 2, y + 11, 13, 2, 0, 0, 0, 255);
                drawRect(x + 2, y + 11, width, 1, 255, 255, 255, 255);
            }
        }

        if (DISPLAY_CUSTOM_COOLDOWN && part == RenderItemEvent.Overlay.OverlayPart.COOLDOWN)
        {
            if (event.isCancelable()) event.setCanceled(true);

            ClientPlayerEntity player = Minecraft.getInstance().player;

            if (player != null)
            {
                float cooldownPercent = player.getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());

                if (cooldownPercent != 0.0F)
                {
                    int width = (int) (cooldownPercent * 13.0F);

                    if (!stack.isDamageableItem())
                    {
                        y += 2;
                    }

                    drawRect(x + 2, y + 9, 13, 2, 0, 0, 0, 255);
                    drawRect(x + 2, y + 9, width, 1, 255, 0, 0, 255);
                }
            }
        }

        if (DISPLAY_EXTRA_DETAILS && part == RenderItemEvent.Overlay.OverlayPart.EXTRA)
        {
            if (stack.getItem().isEdible())
            {
                int red = 255, green = 187, blue = 51, alpha = 255;

                drawRect(x, y, 3, 3, red, green, blue, alpha);
            }
        }
    }

    /**
     * Utility function for drawing rectangles onto the screen using the tessellator.
     */
    private static void drawRect(double x, double y, double width, double height, int red, int green, int blue, int alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();

        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(x,         y,          0D).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(x,         y + height, 0D).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(x + width, y + height, 0D).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(x + width, y,          0D).color(red, green, blue, alpha).endVertex();
        tessellator.end();

        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    /**
     * Utility function for drawing rectangles onto the screen using a vertex builder.
     */
    private static void drawVertexRect(IRenderTypeBuffer renderTypeBuffer, double x, double y, double width, double height, int red, int green, int blue, int alpha)
    {
        IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(RenderTypeHolder.RECTANGLES);
        vertexBuilder.vertex(x,         y + height, 0).color(red, green, blue, alpha).endVertex();
        vertexBuilder.vertex(x,         y,          0).color(red, green, blue, alpha).endVertex();
        vertexBuilder.vertex(x + width, y,          0).color(red, green, blue, alpha).endVertex();
        vertexBuilder.vertex(x + width, y + height, 0).color(red, green, blue, alpha).endVertex();
    }

    /**
     * Utility class that holds the custom RenderType used by
     * {@link RenderItemEventTest#drawVertexRect(IRenderTypeBuffer, double, double, double, double, int, int, int, int)}
     */
    private static class RenderTypeHolder extends RenderState
    {
        private static final RenderType RECTANGLES = RenderType.create(
                "rectangles", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, false,
                RenderType.State.builder()
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .setTransparencyState(RenderState.TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false));

        private RenderTypeHolder(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_)
        {
            super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
        }
    }
}
