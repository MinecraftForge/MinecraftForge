/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.loading;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.Logging.LOADING;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.loading.ImmediateWindowHandler;
import net.minecraftforge.internal.BrandingControl;
import net.minecraftforge.logging.CrashReportExtender;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.gui.LoadingErrorScreen;
import net.minecraftforge.resource.DelegatingPackResources;
import net.minecraftforge.resource.ResourcePackLoader;
import net.minecraftforge.server.LanguageHook;
import net.minecraftforge.forgespi.language.IModInfo;

@OnlyIn(Dist.CLIENT)
public class ClientModLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean loading;
    private static Minecraft mc;
    private static boolean loadingComplete;
    private static LoadingFailedException error;

    public static void begin(final Minecraft minecraft, final PackRepository defaultResourcePacks, final ReloadableResourceManager mcResourceManager)
    {
        // force log4j to shutdown logging in a shutdown hook. This is because we disable default shutdown hook so the server properly logs it's shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(LogManager::shutdown));
        ImmediateWindowHandler.updateProgress("Loading mods");
        loading = true;
        ClientModLoader.mc = minecraft;
        LogicalSidedProvider.setClient(()->minecraft);
        LanguageHook.loadForgeAndMCLangs();
        createRunnableWithCatch(()->ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ImmediateWindowHandler::renderTick)).run();
        if (error == null) {
            ResourcePackLoader.loadResourcePacks(defaultResourcePacks, ClientModLoader::buildPackFinder);
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

    private static void finishModLoading(ModWorkManager.DrivenExecutor syncExecutor, Executor parallelExecutor)
    {
        createRunnableWithCatch(() -> ModLoader.get().finishMods(syncExecutor, parallelExecutor, ImmediateWindowHandler::renderTick)).run();
        loading = false;
        loadingComplete = true;
        // reload game settings on main thread
        syncExecutor.execute(()->mc.options.load(true));
    }

    public static VersionChecker.Status checkForUpdates()
    {
        boolean anyOutdated = ModList.get().getMods().stream()
                .map(VersionChecker::getResult)
                .map(result -> result.status())
                .anyMatch(status -> status == VersionChecker.Status.OUTDATED || status == VersionChecker.Status.BETA_OUTDATED);
        return anyOutdated ? VersionChecker.Status.OUTDATED : null;
    }

    public static boolean completeModLoading()
    {
        List<ModLoadingWarning> warnings = ModLoader.get().getWarnings();
        boolean showWarnings = true;
        try {
            showWarnings = ForgeConfig.CLIENT.showLoadWarnings.get();
        } catch (NullPointerException | IllegalStateException e) {
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
            dumpedLocation = CrashReportExtender.dumpModLoadingCrashReport(LOGGER, error, mc.gameDirectory);
        }
        if (error != null || !warnings.isEmpty()) {
            mc.setScreen(new LoadingErrorScreen(error, warnings, dumpedLocation));
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLoading()
    {
        return loading;
    }

    private static RepositorySource buildPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks) {
        return packAcceptor -> clientPackFinder(modResourcePacks, packAcceptor);
    }

    private static void clientPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks, Consumer<Pack> packAcceptor) {
        List<PathPackResources> hiddenPacks = new ArrayList<>();
        for (Entry<IModFile, ? extends PathPackResources> e : modResourcePacks.entrySet())
        {
            IModInfo mod = e.getKey().getModInfos().get(0);
            final String name = "mod:" + mod.getModId();
            final Pack modPack = Pack.readMetaAndCreate(name, Component.literal(e.getValue().packId()), false, id -> e.getValue(), PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.DEFAULT);
            if (modPack == null) {
                // Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
                ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
                continue;
            }
            LOGGER.debug(CORE, "Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
            if (mod.getOwningFile().showAsResourcePack()) {
                packAcceptor.accept(modPack);
            } else {
                hiddenPacks.add(e.getValue());
            }
        }

        // Create a resource pack merging all mod resources that should be hidden
        final Pack modResourcesPack = Pack.readMetaAndCreate("mod_resources", Component.literal("Mod Resources"), true,
                id -> new DelegatingPackResources(id, false, new PackMetadataSection(Component.translatable("fml.resources.modresources", hiddenPacks.size()),
                        SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)), hiddenPacks),
                PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.DEFAULT);
        packAcceptor.accept(modResourcesPack);
    }
}
