/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.loading;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.*;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.loading.ImmediateWindowHandler;
import net.minecraftforge.internal.BrandingControl;
import net.minecraftforge.logging.CrashReportExtender;
import net.minecraftforge.common.util.LogicalSidedProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.gui.LoadingErrorScreen;
import net.minecraftforge.resource.ResourcePackLoader;
import net.minecraftforge.server.LanguageHook;

@OnlyIn(Dist.CLIENT)
public class ClientModLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean loading;
    private static Minecraft mc;
    private static boolean loadingComplete;
    private static LoadingFailedException error;

    public static void begin(final Minecraft minecraft, final PackRepository defaultResourcePacks, final ReloadableResourceManager mcResourceManager) {
        // force log4j to shutdown logging in a shutdown hook. This is because we disable default shutdown hook so the server properly logs it's shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(LogManager::shutdown));
        ImmediateWindowHandler.updateProgress("Loading mods");
        loading = true;
        ClientModLoader.mc = minecraft;
        LogicalSidedProvider.setClient(()->minecraft);
        LanguageHook.loadForgeAndMCLangs();
        createRunnableWithCatch(()->ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ImmediateWindowHandler::renderTick)).run();
        if (error == null) {
            ResourcePackLoader.loadResourcePacks(defaultResourcePacks, true);
            ModLoader.get().postEvent(new AddPackFindersEvent(PackType.CLIENT_RESOURCES, defaultResourcePacks::addPackFinder));
            DataPackConfig.DEFAULT.addModPacks(ResourcePackLoader.getPackNames());
            mcResourceManager.registerReloadListener(ClientModLoader::onResourceReload);
            mcResourceManager.registerReloadListener(BrandingControl.resourceManagerReloadListener());
        }
    }

    private static CompletableFuture<Void> onResourceReload(final PreparableReloadListener.PreparationBarrier stage, final ResourceManager resourceManager, final ProfilerFiller prepareProfiler, final ProfilerFiller executeProfiler, final Executor asyncExecutor, final Executor syncExecutor) {
        return CompletableFuture.runAsync(createRunnableWithCatch(() -> startModLoading(ModWorkManager.wrappedExecutor(syncExecutor), asyncExecutor)), ModWorkManager.parallelExecutor())
                .thenCompose(stage::wait)
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
        createRunnableWithCatch(() -> ModLoader.get().loadMods(syncExecutor, parallelExecutor, ImmediateWindowHandler::renderTick)).run();
    }

    private static void finishModLoading(ModWorkManager.DrivenExecutor syncExecutor, Executor parallelExecutor) {
        createRunnableWithCatch(() -> ModLoader.get().finishMods(syncExecutor, parallelExecutor, ImmediateWindowHandler::renderTick)).run();
        loading = false;
        loadingComplete = true;
        // reload game settings on main thread
        syncExecutor.execute(()->mc.options.load(true));
    }

    public static VersionChecker.Status checkForUpdates() {
        boolean anyOutdated = ModList.get().getMods().stream()
                .map(VersionChecker::getResult)
                .map(result -> result.status())
                .anyMatch(status -> status == VersionChecker.Status.OUTDATED || status == VersionChecker.Status.BETA_OUTDATED);
        return anyOutdated ? VersionChecker.Status.OUTDATED : null;
    }

    public static boolean completeModLoading() {
        var warnings = ModLoader.get().getWarnings();
        boolean showWarnings = ForgeConfig.CLIENT.showLoadWarnings();

        if (!showWarnings) {
            //User disabled warning screen, as least log them
            if (!warnings.isEmpty()) {
                LOGGER.warn(Logging.LOADING, "Mods loaded with {} warning(s)", warnings.size());
                warnings.forEach(warning -> LOGGER.warn(Logging.LOADING, warning.formatToString()));
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
            dumpedLocation = CrashReportExtender.dumpModLoadingCrashReport(LOGGER, error, mc.gameDirectory);
        }

        if (error != null || !warnings.isEmpty()) {
            mc.setScreen(new LoadingErrorScreen(error, warnings, dumpedLocation));
            return true;
        }

        return false;
    }

    public static boolean isLoading() {
        return loading;
    }
}
