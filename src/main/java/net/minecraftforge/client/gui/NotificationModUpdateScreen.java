/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.ClientModLoader;
import net.minecraftforge.api.distmarker.Dist;

@OnlyIn(Dist.CLIENT)
public class NotificationModUpdateScreen extends Screen
{

    private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation(ForgeVersion.MOD_ID, "textures/gui/version_check_icons.png");

    private final Button modButton;
    private VersionChecker.Status showNotification = null;
    private boolean hasCheckedForUpdates = false;

    public NotificationModUpdateScreen(Button modButton)
    {
        super(new TranslationTextComponent("forge.menu.updatescreen.title"));
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
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (showNotification == null || !showNotification.shouldDraw() || ForgeMod.disableVersionCheck)
        {
            return;
        }

        Minecraft.getInstance().getTextureManager().bindTexture(VERSION_CHECK_ICONS);
        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.pushMatrix();

        int x = modButton.x;
        int y = modButton.y;
        int w = modButton.getWidth();
        int h = modButton.getHeight();

        blit(x + w - (h / 2 + 4), y + (h / 2 - 4), showNotification.getSheetOffset() * 8, (showNotification.isAnimated() && ((System.currentTimeMillis() / 800 & 1) == 1)) ? 8 : 0, 8, 8, 64, 16);
        GlStateManager.popMatrix();
    }

    public static NotificationModUpdateScreen init(MainMenuScreen guiMainMenu, Button modButton)
    {
        NotificationModUpdateScreen notificationModUpdateScreen = new NotificationModUpdateScreen(modButton);
        notificationModUpdateScreen.init(guiMainMenu.getMinecraft(), guiMainMenu.width, guiMainMenu.height);
        notificationModUpdateScreen.init();
        return notificationModUpdateScreen;
    }

}
