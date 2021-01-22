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

package net.minecraftforge.fml.client;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraftforge.fml.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.gui.screen.LoadingErrorScreen;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.DelegatingResourcePack;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.fml.server.LanguageHook;
import net.minecraftforge.forgespi.language.IModInfo;

@OnlyIn(Dist.CLIENT)
public class ClientModLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean loading;
    private static Minecraft mc;
    private static boolean loadingComplete;
    private static LoadingFailedException error;
    private static EarlyLoaderGUI earlyLoaderGUI;

    private static class SpacedRunnable implements Runnable {
        static final long NANO_SLEEP_TIME = TimeUnit.MILLISECONDS.toNanos(50);
        private final Runnable wrapped;
        private long lastRun;

        private SpacedRunnable(final Runnable wrapped) {
            this.wrapped = wrapped;
            this.lastRun = System.nanoTime() - NANO_SLEEP_TIME;
        }

        @Override
        public void run() {
            if (System.nanoTime() - this.lastRun > NANO_SLEEP_TIME) {
                wrapped.run();
                this.lastRun = System.nanoTime();
            }
        }
    }
    public static void begin(final Minecraft minecraft, final ResourcePackList defaultResourcePacks, final IReloadableResourceManager mcResourceManager, DownloadingPackFinder metadataSerializer)
    {
        // force log4j to shutdown logging in a shutdown hook. This is because we disable default shutdown hook so the server properly logs it's shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(LogManager::shutdown));
        loading = true;
        ClientModLoader.mc = minecraft;
        LogicalSidedProvider.setClient(()->minecraft);
        LanguageHook.loadForgeAndMCLangs();
        earlyLoaderGUI = new EarlyLoaderGUI(minecraft.getMainWindow());
        createRunnableWithCatch(()->ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), new SpacedRunnable(earlyLoaderGUI::renderTick))).run();
        if (error == null) {
            ResourcePackLoader.loadResourcePacks(defaultResourcePacks, ClientModLoader::buildPackFinder);
            DatapackCodec.VANILLA_CODEC.addModPacks(ResourcePackLoader.getPackNames());
            mcResourceManager.addReloadListener(ClientModLoader::onResourceReload);
            mcResourceManager.addReloadListener(BrandingControl.resourceManagerReloadListener());
            ModelLoaderRegistry.init();
        }
    }

    private static CompletableFuture<Void> onResourceReload(final IFutureReloadListener.IStage stage, final IResourceManager resourceManager, final IProfiler prepareProfiler, final IProfiler executeProfiler, final Executor asyncExecutor, final Executor syncExecutor) {
        return CompletableFuture.runAsync(createRunnableWithCatch(() -> startModLoading(ModWorkManager.wrappedExecutor(syncExecutor), asyncExecutor)), ModWorkManager.parallelExecutor())
                .thenCompose(stage::markCompleteAwaitingOthers)
                .thenRunAsync(() -> finishModLoading(ModWorkManager.wrappedExecutor(syncExecutor), asyncExecutor), ModWorkManager.parallelExecutor());
    }

    private static Runnable createRunnableWithCatch(Runnable r) {
        return ()-> {
            if (loadingComplete) return;
            try {
                r.run();
            } catch (LoadingFailedException e) {
                if (error == null) error = e;
            }
        };
    }

    private static void startModLoading(ModWorkManager.DrivenExecutor syncExecutor, Executor parallelExecutor) {
        earlyLoaderGUI.handleElsewhere();
        createRunnableWithCatch(() -> ModLoader.get().loadMods(syncExecutor, parallelExecutor, executor -> CompletableFuture.runAsync(ClientModLoader::preSidedRunnable, executor), executor -> CompletableFuture.runAsync(ClientModLoader::postSidedRunnable, executor), new SpacedRunnable(earlyLoaderGUI::renderTick))).run();
    }

    private static void postSidedRunnable() {
        LOGGER.debug(LOADING, "Running post client event work");
        RenderingRegistry.loadEntityRenderers(mc.getRenderManager());
    }

    private static void preSidedRunnable() {
        LOGGER.debug(LOADING, "Running pre client event work");
    }

    private static void finishModLoading(ModWorkManager.DrivenExecutor syncExecutor, Executor parallelExecutor)
    {
        createRunnableWithCatch(() -> ModLoader.get().finishMods(syncExecutor, parallelExecutor, new SpacedRunnable(earlyLoaderGUI::renderTick))).run();
        loading = false;
        loadingComplete = true;
        // reload game settings on main thread
        syncExecutor.execute(()->mc.gameSettings.loadOptions());
    }

    public static VersionChecker.Status checkForUpdates()
    {
        boolean anyOutdated = ModList.get().getMods().stream()
                .map(VersionChecker::getResult)
                .map(result -> result.status)
                .anyMatch(status -> status == VersionChecker.Status.OUTDATED || status == VersionChecker.Status.BETA_OUTDATED);
        return anyOutdated ? VersionChecker.Status.OUTDATED : null;
    }

    public static boolean completeModLoading()
    {
        RenderSystem.disableTexture();
        RenderSystem.enableTexture();
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
        File dumpedLocation = null;
        if (error == null) {
            // We can finally start the forge eventbus up
            MinecraftForge.EVENT_BUS.start();
        } else {
            // Double check we have the langs loaded for forge
            LanguageHook.loadForgeAndMCLangs();
            dumpedLocation = CrashReportExtender.dumpModLoadingCrashReport(LOGGER, error, mc.gameDir);
        }
        if (error != null || !warnings.isEmpty()) {
            mc.displayGuiScreen(new LoadingErrorScreen(error, warnings, dumpedLocation));
            return true;
        } else {
            ClientHooks.logMissingTextureErrors();
            return false;
        }
    }

    public static void renderProgressText() {
        earlyLoaderGUI.renderFromGUI();
    }
    public static boolean isLoading()
    {
        return loading;
    }

    private static ResourcePackLoader.IPackInfoFinder buildPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ResourcePackInfo> packSetter) {
        return (packList, factory) -> clientPackFinder(modResourcePacks, packSetter, packList, factory);
    }

    private static void clientPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ResourcePackInfo> packSetter, Consumer<ResourcePackInfo> consumer, ResourcePackInfo.IFactory factory) {
        List<ModFileResourcePack> hiddenPacks = new ArrayList<>();
        for (Entry<ModFile, ? extends ModFileResourcePack> e : modResourcePacks.entrySet())
        {
            IModInfo mod = e.getKey().getModInfos().get(0);
            if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
            final String name = "mod:" + mod.getModId();
            final ResourcePackInfo packInfo = ResourcePackInfo.createResourcePack(name, false, e::getValue, factory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.PLAIN);
            if (packInfo == null) {
                // Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
                ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
                continue;
            }
            packSetter.accept(e.getValue(), packInfo);
            LOGGER.debug(CORE, "Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
            if (mod.getOwningFile().showAsResourcePack()) {
                consumer.accept(packInfo);
            } else {
                hiddenPacks.add(e.getValue());
            }
        }
        final ResourcePackInfo packInfo = ResourcePackInfo.createResourcePack("mod_resources", true, () -> new DelegatingResourcePack("mod_resources", "Mod Resources",
                new PackMetadataSection(new TranslationTextComponent("fml.resources.modresources", hiddenPacks.size()), 6),
                hiddenPacks), factory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.PLAIN);
        consumer.accept(packInfo);
    }
}
