/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraftforge.api.Side;
import net.minecraftforge.api.SideOnly;
import net.minecraftforge.fml.SidedProvider;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.client.gui.GuiNotification;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientModLoader
{
    private static boolean loading;
    private static Minecraft mc;

    /**
     * Mod loading entrypoint for the client
     * @param minecraft
     * @param defaultResourcePacks
     * @param mcResourceManager
     * @param metadataSerializer_
     */
    public static void begin(final Minecraft minecraft, final List<IResourcePack> defaultResourcePacks, final IReloadableResourceManager mcResourceManager, MetadataSerializer metadataSerializer_)
    {
        loading = true;
        ClientModLoader.mc = minecraft;
        SidedProvider.setClient(()->minecraft);
        SplashProgress.start();
        ModLoader.get().loadMods();
        minecraft.refreshResources();
    }

    public static void end()
    {
        ModLoader.get().finishMods();
        loading = false;
        mc.gameSettings.loadOptions();
        SplashProgress.finish();
    }

    public static VersionChecker.Status checkForUpdates()
    {
        return VersionChecker.Status.UP_TO_DATE;
    }

    public static void complete()
    {
        GlStateManager.disableTexture2D();
        GlStateManager.enableTexture2D();
    }

    // If the startup screen has a notification on it, render that instead of the loading screen
    public static boolean drawNotificationOverProgressScreen(final Minecraft client, final ScaledResolution scaledResolution) throws IOException
    {
        if (client.currentScreen instanceof GuiNotification)
        {
            int width = scaledResolution.getScaledWidth();
            int height = scaledResolution.getScaledHeight();
            int mouseX = Mouse.getX() * width / client.displayWidth;
            int mouseZ = height - Mouse.getY() * height / client.displayHeight - 1;

            client.currentScreen.drawScreen(mouseX, mouseZ, 0);
            client.currentScreen.handleInput();

            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean isErrored()
    {
        return false;
    }

    public static boolean isLoading()
    {
        return loading;
    }
}
