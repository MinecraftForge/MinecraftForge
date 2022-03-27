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
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean loading;
    private static Minecraft mc;
    private static boolean loadingComplete;
    private static LoadingFailedException error;
//    private static EarlyLoaderGUI earlyLoaderGUI;

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
    public static void begin(final Minecraft minecraft, final PackRepository defaultResourcePacks, final ReloadableResourceManager mcResourceManager, ClientPackSource metadataSerializer)
    {
        // force log4j to shutdown logging in a shutdown hook. This is because we disable default shutdown hook so the server properly logs it's shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(LogManager::shutdown));
        loading = true;
        ClientModLoader.mc = minecraft;
//        LogicalSidedProvider.setClient(()->minecraft);
//        LanguageHook.loadForgeAndMCLangs();
//        earlyLoaderGUI = new EarlyLoaderGUI(minecraft.getWindow());
        createRunnableWithCatch(()->ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ()->{})).run();
        mcResourceManager.registerReloadListener(ClientModLoader::onResourceReload);
//        if (error == null) {
////            ResourcePackLoader.loadResourcePacks(defaultResourcePacks, ClientModLoader::buildPackFinder);
////            DatapackCodec.DEFAULT.addModPacks(ResourcePackLoader.getPackNames());
////            mcResourceManager.registerReloadListener(BrandingControl.resourceManagerReloadListener());
////            ModelLoaderRegistry.init();
//        }
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
//        earlyLoaderGUI.handleElsewhere();
        createRunnableWithCatch(() -> ModLoader.get().loadMods(syncExecutor, parallelExecutor, ()->{})).run();
    }

    private static void postSidedRunnable() {
//        LOGGER.debug(LOADING, "Running post client event work");
//        RenderingRegistry.loadEntityRenderers(mc.getEntityRenderDispatcher());
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

    public static boolean completeModLoading()
    {
//        RenderSystem.disableTexture();
//        RenderSystem.enableTexture();
//        List<ModLoadingWarning> warnings = ModLoader.get().getWarnings();
//        boolean showWarnings = true;
//        try {
//            showWarnings = ForgeConfig.CLIENT.showLoadWarnings.get();
//        } catch (NullPointerException e) {
//            // We're in an early error state, config is not available. Assume true.
//        }
//        if (!showWarnings) {
//            //User disabled warning screen, as least log them
//            if (!warnings.isEmpty()) {
//                LOGGER.warn(LOADING, "Mods loaded with {} warning(s)", warnings.size());
//                warnings.forEach(warning -> LOGGER.warn(LOADING, warning.formatToString()));
//            }
//            warnings = Collections.emptyList(); //Clear warnings, as the user does not want to see them
//        }
//        File dumpedLocation = null;
//        if (error == null) {
//            // We can finally start the forge eventbus up
//            MinecraftForge.EVENT_BUS.start();
//        } else {
//            // Double check we have the langs loaded for forge
//            LanguageHook.loadForgeAndMCLangs();
//            dumpedLocation = CrashReportExtender.dumpModLoadingCrashReport(LOGGER, error, mc.gameDirectory);
//        }
//        if (error != null || !warnings.isEmpty()) {
//            mc.setScreen(new LoadingErrorScreen(error, warnings, dumpedLocation));
//            return true;
//        } else {
//            ClientHooks.logMissingTextureErrors();
//            return false;
//        }
        return true;
    }

    public static void renderProgressText() {
//        earlyLoaderGUI.renderFromGUI();
    }
    public static boolean isLoading()
    {
        return loading;
    }

//    private static ResourcePackLoader.IPackInfoFinder buildPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ResourcePackInfo> packSetter) {
//        return (packList, factory) -> clientPackFinder(modResourcePacks, packSetter, packList, factory);
//    }
//
//    private static void clientPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ResourcePackInfo> packSetter, Consumer<ResourcePackInfo> consumer, ResourcePackInfo.IFactory factory) {
//        List<ModFileResourcePack> hiddenPacks = new ArrayList<>();
//        for (Entry<ModFile, ? extends ModFileResourcePack> e : modResourcePacks.entrySet())
//        {
//            IModInfo mod = e.getKey().getModInfos().get(0);
//            if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
//            final String name = "mod:" + mod.getModId();
//            final ResourcePackInfo packInfo = ResourcePackInfo.create(name, false, e::getValue, factory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.DEFAULT);
//            if (packInfo == null) {
//                // Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
//                ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
//                continue;
//            }
//            packSetter.accept(e.getValue(), packInfo);
//            LOGGER.debug(CORE, "Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
//            if (mod.getOwningFile().showAsResourcePack()) {
//                consumer.accept(packInfo);
//            } else {
//                hiddenPacks.add(e.getValue());
//            }
//        }
//        final ResourcePackInfo packInfo = ResourcePackInfo.create("mod_resources", true, () -> new DelegatingResourcePack("mod_resources", "Mod Resources",
//                new PackMetadataSection(new TranslationTextComponent("fml.resources.modresources", hiddenPacks.size()), 6),
//                hiddenPacks), factory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.DEFAULT);
//        consumer.accept(packInfo);
//    }
}
