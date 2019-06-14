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

package net.minecraftforge.fml.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.SidedProvider;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.gui.LoadingErrorScreen;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

@OnlyIn(Dist.CLIENT)
public class ClientModLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean loading;
    private static Minecraft mc;
    private static LoadingFailedException error;

    public static void begin(final Minecraft minecraft, final ResourcePackList<ResourcePackInfoClient> defaultResourcePacks, final IReloadableResourceManager mcResourceManager, DownloadingPackFinder metadataSerializer)
    {
        loading = true;
        ClientModLoader.mc = minecraft;
        SidedProvider.setClient(()->minecraft);
        LogicalSidedProvider.setClient(()->minecraft);
        try {
            ModLoader.get().loadMods();
        } catch (LoadingFailedException e) {
            MinecraftForge.EVENT_BUS.shutdown();
            error = e;
        }
        ResourcePackLoader.loadResourcePacks(defaultResourcePacks);
    }

    public static void end()
    {
        try {
            ModLoader.get().finishMods();
        } catch (LoadingFailedException e) {
            MinecraftForge.EVENT_BUS.shutdown();
            if (error == null) error = e;
        }
        loading = false;
        mc.gameSettings.loadOptions();
    }

    public static VersionChecker.Status checkForUpdates()
    {
        return VersionChecker.Status.UP_TO_DATE;
    }

    public static void complete()
    {
        GlStateManager.disableTexture2D();
        GlStateManager.enableTexture2D();
        List<ModLoadingWarning> warnings = ModLoader.get().getWarnings();
        boolean showWarnings = true;
        try {
            showWarnings = ForgeConfig.CLIENT.showLoadWarnings.get();
        } catch (NullPointerException e) {
            // We're in an early error state, config is not available. Assume true.
        }
        if (!showWarnings) {
            //User disabled warning screen, as least log them
            if (!warnings.isEmpty()) {
                LOGGER.warn(LOADING, "Mods loaded with {} warning(s)", warnings.size());
                warnings.forEach(warning -> LOGGER.warn(LOADING, warning.formatToString()));
            }
            warnings = Collections.emptyList(); //Clear warnings, as the user does not want to see them
        }
        if (error != null || !warnings.isEmpty()) {
            mc.displayGuiScreen(new LoadingErrorScreen(error, warnings));
        } else {
            ClientHooks.logMissingTextureErrors();
        }
    }

    public static boolean isLoading()
    {
        return loading;
    }
}
