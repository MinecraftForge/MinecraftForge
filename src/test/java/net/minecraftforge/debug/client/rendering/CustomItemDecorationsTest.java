/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.ItemDecoratorHandler;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CustomItemDecorationsTest.MOD_ID)
public class CustomItemDecorationsTest
{
    public static final String MOD_ID = "custom_item_decorations_test";
    private static final boolean ENABLED = true;

    public CustomItemDecorationsTest() { }

    @Mod.EventBusSubscriber(modid = CustomItemDecorationsTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents
    {
        @SubscribeEvent
        public static void onRegisterItemDecorations(final RegisterItemDecorationsEvent event)
        {
            if (ENABLED) {
                event.register(Items.EGG, new StackSizeDurabilityBar());
            }
        }
    }

    private static class StackSizeDurabilityBar implements IItemDecorator
    {

        @Override
        public boolean render(Font font, ItemStack stack, int xOffset, int yOffset, float blitOffset)
        {
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            float f = Math.max(0.0F, (stack.getMaxStackSize() - (float)stack.getCount()) / stack.getMaxStackSize());
            int i = Math.round(13.0F - (float)stack.getCount() * 13.0F / stack.getMaxStackSize());
            int j = Mth.hsvToRgb(f / 3.0F,1f,1f);
            fillRect(bufferbuilder, xOffset + 2, yOffset, blitOffset + 189, 13, 2, 0, 0, 0);
            fillRect(bufferbuilder, xOffset + 2, yOffset, blitOffset + 190, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255);
            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            return true;
        }

        private static void fillRect(BufferBuilder pRenderer, int pX, int pY, float pZ, int pWidth, int pHeight, int pRed, int pGreen, int pBlue)
        {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            pRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            pRenderer.vertex(pX, pY, pZ).color(pRed, pGreen, pBlue, 255).endVertex();
            pRenderer.vertex(pX, pY + pHeight, pZ).color(pRed, pGreen, pBlue, 255).endVertex();
            pRenderer.vertex(pX + pWidth, pY + pHeight, pZ).color(pRed, pGreen, pBlue, 255).endVertex();
            pRenderer.vertex(pX + pWidth, pY, pZ).color(pRed, pGreen, pBlue, 255).endVertex();
            BufferUploader.drawWithShader(pRenderer.end());
        }
    }
}
