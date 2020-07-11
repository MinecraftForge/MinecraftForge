/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.resources.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.BrandingControl;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.SidedProvider;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.gui.screen.LoadingErrorScreen;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.DelegatableResourcePack;
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
    private static LoadingFailedException error;
    private static EarlyLoaderGUI earlyLoaderGUI;

    public static void begin(final Minecraft minecraft, final ResourcePackList<ClientResourcePackInfo> defaultResourcePacks, final IReloadableResourceManager mcResourceManager, DownloadingPackFinder metadataSerializer)
    {
        // force log4j to shutdown logging in a shutdown hook. This is because we disable default shutdown hook so the server properly logs it's shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(LogManager::shutdown));
        loading = true;
        ClientModLoader.mc = minecraft;
        SidedProvider.setClient(()->minecraft);
        LogicalSidedProvider.setClient(()->minecraft);
        LanguageHook.loadForgeAndMCLangs();
        earlyLoaderGUI = new EarlyLoaderGUI(minecraft.getMainWindow());
        createRunnableWithCatch(() -> ModLoader.get().gatherAndInitializeMods(earlyLoaderGUI::renderTick)).run();
        ResourcePackLoader.loadResourcePacks(defaultResourcePacks, ClientModLoader::buildPackFinder);
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
        ModelLoaderRegistry.initComplete();
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
        if (error == null) {
            // We can finally start the forge eventbus up
            MinecraftForge.EVENT_BUS.start();
        }
        if (error != null || !warnings.isEmpty()) {
            mc.displayGuiScreen(new LoadingErrorScreen(error, warnings));
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

    private static <T extends ResourcePackInfo> ResourcePackLoader.IPackInfoFinder<T> buildPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ? super T> packSetter) {
        return (packList, factory) -> clientPackFinder(modResourcePacks, packSetter, packList, factory);
    }

    private static <T extends ResourcePackInfo> void clientPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ? super T> packSetter, Consumer<T> consumer, ResourcePackInfo.IFactory<? extends T> factory) {
        List<DelegatableResourcePack> hiddenPacks = new ArrayList<>();
        for (Entry<ModFile, ? extends ModFileResourcePack> e : modResourcePacks.entrySet())
        {
            IModInfo mod = e.getKey().getModInfos().get(0);
            if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
            final String name = "mod:" + mod.getModId();
            final T packInfo = ResourcePackInfo.createResourcePack(name, false, e::getValue, factory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.field_232625_a_);
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
        final T packInfo = ResourcePackInfo.createResourcePack("mod_resources", true, () -> new DelegatingResourcePack("mod_resources", "Mod Resources",
                new PackMetadataSection(new TranslationTextComponent("fml.resources.modresources", hiddenPacks.size()), 5),
                hiddenPacks), factory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.field_232625_a_);
        consumer.accept(packInfo);
    }
}
