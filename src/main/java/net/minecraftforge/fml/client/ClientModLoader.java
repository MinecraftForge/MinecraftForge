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
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.client.gui.LoadingErrorScreen;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.GlStateManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

@OnlyIn(Dist.CLIENT)
public class ClientModLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean loading;
    private static Minecraft mc;
    private static LoadingFailedException error;
    private static EarlyLoaderGUI earlyLoaderGUI;

    public static void begin(final Minecraft minecraft, final ResourcePackList<ClientResourcePackInfo> defaultResourcePacks, final IReloadableResourceManager mcResourceManager, DownloadingPackFinder metadataSerializer)
    {
        loading = true;
        ClientModLoader.mc = minecraft;
        SidedProvider.setClient(()->minecraft);
        LogicalSidedProvider.setClient(()->minecraft);
        earlyLoaderGUI = new EarlyLoaderGUI(minecraft.mainWindow);
        createRunnableWithCatch(() -> ModLoader.get().gatherAndInitializeMods(earlyLoaderGUI::renderTick)).run();
        ResourcePackLoader.loadResourcePacks(defaultResourcePacks);
        mcResourceManager.addReloadListener(ClientModLoader::onreload);
        mcResourceManager.addReloadListener(BrandingControl.resourceManagerReloadListener());
        ModelLoaderRegistry.init();
    }

    private static CompletableFuture<Void> onreload(final IFutureReloadListener.IStage stage, final IResourceManager resourceManager, final IProfiler prepareProfiler, final IProfiler executeProfiler, final Executor asyncExecutor, final Executor syncExecutor) {
        return CompletableFuture.runAsync(createRunnableWithCatch(() -> startModLoading(syncExecutor)), asyncExecutor).
                thenCompose(stage::markCompleteAwaitingOthers).
                thenRunAsync(() -> finishModLoading(syncExecutor), asyncExecutor);
    }

    private static Runnable createRunnableWithCatch(Runnable r) {
        return ()-> {
            try {
                r.run();
            } catch (LoadingFailedException e) {
                MinecraftForge.EVENT_BUS.shutdown();
                if (error == null) error = e;
            }
        };
    }

    private static void startModLoading(Executor executor) {
        earlyLoaderGUI.handleElsewhere();
        createRunnableWithCatch(() -> ModLoader.get().loadMods(executor, ClientModLoader::preSidedRunnable, ClientModLoader::postSidedRunnable)).run();
    }

    private static void postSidedRunnable(Consumer<Supplier<Event>> perModContainerEventProcessor) {
        RenderingRegistry.loadEntityRenderers(mc.getRenderManager());
    }

    private static void preSidedRunnable(Consumer<Supplier<Event>> perModContainerEventProcessor) {
        perModContainerEventProcessor.accept(ModelRegistryEvent::new);
    }

    private static void finishModLoading(Executor executor)
    {
        createRunnableWithCatch(() -> ModLoader.get().finishMods(executor)).run();
        loading = false;
        // reload game settings on main thread
        executor.execute(()->mc.gameSettings.loadOptions());
    }

    public static VersionChecker.Status checkForUpdates()
    {
        return VersionChecker.Status.UP_TO_DATE;
    }

    public static void complete()
    {
        GlStateManager.disableTexture();
        GlStateManager.enableTexture();
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
        if (error == null) {
            // We can finally start the forge eventbus up
            MinecraftForge.EVENT_BUS.start();
        }
    }

    public static void renderProgressText() {
        earlyLoaderGUI.renderFromGUI();
    }
    public static boolean isLoading()
    {
        return loading;
    }
}
