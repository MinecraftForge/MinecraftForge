/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
            if (ENABLED)
                event.register(Items.EGG, new StackSizeDurabilityBar());
        }
    }

    private static class StackSizeDurabilityBar implements IItemDecorator
    {
        @Override
        public boolean render(GuiGraphics graphics, Font font, ItemStack stack, int xOffset, int yOffset)
        {
            RenderSystem.disableBlend();
            float f = Math.max(0.0F, (float)stack.getCount() / stack.getMaxStackSize());
            int i = Math.round((float)stack.getCount() * 13.0F / stack.getMaxStackSize());
            int j = Mth.hsvToRgb(f / 3.0F, 1f, 1f) | 0xFF000000;
            int x = xOffset + 2;
            int y = yOffset + 13;
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, ItemRenderer.ITEM_COUNT_BLIT_OFFSET + 1F);
            graphics.fill(x, y, x + 13, y + 2, 0xFF000000);
            graphics.fill(x, y, x + i, y + 1, j);
            graphics.pose().popPose();
            RenderSystem.enableBlend();
            return true;
        }
    }
}
