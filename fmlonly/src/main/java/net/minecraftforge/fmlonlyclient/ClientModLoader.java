/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmlonlyclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModWorkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
public class ClientModLoader
{
    private static boolean loading;
    private static Minecraft mc;
    private static boolean loadingComplete;
    private static LoadingFailedException error;

    public static void begin(final Minecraft minecraft, final PackRepository defaultResourcePacks, final ReloadableResourceManager mcResourceManager)
    {
        // force log4j to shutdown logging in a shutdown hook. This is because we disable default shutdown hook so the server properly logs it's shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(LogManager::shutdown));
        loading = true;
        ClientModLoader.mc = minecraft;
        createRunnableWithCatch(()->ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ()->{})).run();
        mcResourceManager.registerReloadListener(ClientModLoader::onResourceReload);
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
        if (error!=null) throw error;
        createRunnableWithCatch(() -> ModLoader.get().loadMods(syncExecutor, parallelExecutor, ()->{})).run();
    }

    private static void finishModLoading(ModWorkManager.DrivenExecutor syncExecutor, Executor parallelExecutor)
    {
        if (error!=null) throw error;
        createRunnableWithCatch(() -> ModLoader.get().finishMods(syncExecutor, parallelExecutor, ()->{})).run();
        loading = false;
        loadingComplete = true;
        // reload game settings on main thread
        syncExecutor.execute(()->mc.options.load());
    }

    public static boolean isLoading()
    {
        return loading;
    }
}
