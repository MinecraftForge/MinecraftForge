/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.client.loading.ClientModLoader;
import net.minecraftforge.api.distmarker.Dist;

@OnlyIn(Dist.CLIENT)
public class TitleScreenModUpdateIndicator extends Screen
{

    private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation(ForgeVersion.MOD_ID, "textures/gui/version_check_icons.png");

    private final Button modButton;
    private VersionChecker.Status showNotification = null;
    private boolean hasCheckedForUpdates = false;

    public TitleScreenModUpdateIndicator(Button modButton)
    {
        super(Component.translatable("forge.menu.updatescreen.title"));
        this.modButton = modButton;
    }

    @Override
    public void init()
    {
        if (!hasCheckedForUpdates)
        {
            if (modButton != null)
            {
                showNotification = ClientModLoader.checkForUpdates();
            }
            hasCheckedForUpdates = true;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        if (showNotification == null || !showNotification.shouldDraw() || !FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.VERSION_CHECK))
        {
            return;
        }

        int x = modButton.getX();
        int y = modButton.getY();
        int w = modButton.getWidth();
        int h = modButton.getHeight();

        guiGraphics.blit(VERSION_CHECK_ICONS, x + w - (h / 2 + 4), y + (h / 2 - 4), showNotification.getSheetOffset() * 8, (showNotification.isAnimated() && ((System.currentTimeMillis() / 800 & 1) == 1)) ? 8 : 0, 8, 8, 64, 16);
    }

    public static TitleScreenModUpdateIndicator init(TitleScreen guiMainMenu, Button modButton)
    {
        TitleScreenModUpdateIndicator titleScreenModUpdateIndicator = new TitleScreenModUpdateIndicator(modButton);
        titleScreenModUpdateIndicator.resize(guiMainMenu.getMinecraft(), guiMainMenu.width, guiMainMenu.height);
        titleScreenModUpdateIndicator.init();
        return titleScreenModUpdateIndicator;
    }

}
