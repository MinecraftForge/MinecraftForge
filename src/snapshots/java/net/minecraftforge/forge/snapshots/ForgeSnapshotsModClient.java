/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.forge.snapshots;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ForgeSnapshotsModClient
{
    public static void renderMainMenuWarning(VersionChecker.Status status, TitleScreen gui, GuiGraphics guiGraphics, Font font, int width, int height, int alpha)
    {
        // Render a warning at the top of the screen
        Component line = Component.translatable("forge.froge.supportWarning").withStyle(ChatFormatting.RED);
        guiGraphics.drawCenteredString(font, line, width / 2, 4 + (font.lineHeight + 1) / 2, 0xFFFFFF | alpha);
    }

    @SubscribeEvent
    static void onScreenOpen(final ScreenEvent.Opening event)
    {
        if (ForgeSnapshotsMod.seenSnapshotWarning || !(event.getNewScreen() instanceof TitleScreen titleScreen))
            return;

        event.setNewScreen(new ConfirmScreen(confirmed -> {
            if (confirmed)
            {
                ForgeSnapshotsMod.seenSnapshotWarning = true;
                Minecraft.getInstance().options.save();
                Minecraft.getInstance().setScreen(titleScreen);
            }
            else
            {
                System.exit(0);
            }
        }, Component.translatable("forge.froge.warningScreen.title"), Component.translatable("forge.froge.warningScreen.text"),
                CommonComponents.GUI_ACKNOWLEDGE, Component.translatable("forge.gui.exit")));
    }

    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // static void onGuiOverlayDebugText(final CustomizeGuiOverlayEvent.DebugText event)
    // {
    //     if (Minecraft.getInstance().options.renderDebug)
    //     {
    //         event.getLeft().add(0, "Minecraft Froge");
    //         event.getRight().add(0, "NOT SUPPORTED");
    //     }
    // }
}
