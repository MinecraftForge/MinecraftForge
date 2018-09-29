/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml;

import com.google.common.collect.Streams;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.language.IModInfo;
import net.minecraftforge.fml.loading.DefaultModInfos;
import net.minecraftforge.fml.loading.EarlyLoadingException;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.registries.GameData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.fml.Logging.CORE;

public class ModLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static ModLoader INSTANCE;
    private final ClassLoader launchClassLoader;
    private final LoadingModList loadingModList;
    private final ModLoadingClassLoader modClassLoader;

    private final List<ModLoadingException> loadingExceptions;
    private ModLoader()
    {
        INSTANCE = this;
        this.launchClassLoader = FMLLoader.getLaunchClassLoader();
        this.loadingModList = FMLLoader.getLoadingModList();
        this.modClassLoader = new ModLoadingClassLoader(this.launchClassLoader);
        this.loadingExceptions = FMLLoader.getLoadingModList().
                getErrors().stream().map(ModLoadingException::fromEarlyException).collect(Collectors.toList());
        Thread.currentThread().setContextClassLoader(this.modClassLoader);
    }

    public static ModLoader get()
    {
        return INSTANCE == null ? INSTANCE = new ModLoader() : INSTANCE;
    }

    private static Runnable fireClientEvents()
    {
        return ()->MinecraftForge.EVENT_BUS.post(new ModelRegistryEvent());
    }

    public void loadMods() {
        if (!this.loadingExceptions.isEmpty()) {
            throw new LoadingFailedException(loadingExceptions);
        }
        final ModList modList = ModList.of(loadingModList.getModFiles().stream().map(ModFileInfo::getFile).collect(Collectors.toList()), loadingModList.getMods());
        ModContainer forgeModContainer;
        try
        {
            forgeModContainer = (ModContainer)Class.forName("net.minecraftforge.common.ForgeModContainer", true, modClassLoader).
            getConstructor(ModLoadingClassLoader.class).newInstance(modClassLoader);
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e)
        {
            LOGGER.error(CORE,"Unable to load the Forge Mod Container", e);
            loadingExceptions.add(new ModLoadingException(DefaultModInfos.forgeModInfo, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadforge", e));
            forgeModContainer = null;
        }
        final Stream<ModContainer> modContainerStream = loadingModList.getModFiles().stream().
                map(ModFileInfo::getFile).
                map(mf -> buildMods(mf, modClassLoader)).
                flatMap(Collection::stream);
        if (!loadingExceptions.isEmpty()) {
            LOGGER.error(CORE, "Failed to initialize mod containers");
            throw new LoadingFailedException(loadingExceptions);
        }
        modList.setLoadedMods(Streams.concat(Stream.of(forgeModContainer), modContainerStream).collect(Collectors.toList()));
        dispatchAndHandleError(LifecycleEventProvider.CONSTRUCT);
        WorldPersistenceHooks.addHook(new FMLWorldPersistenceHook());
        WorldPersistenceHooks.addHook((WorldPersistenceHooks.WorldPersistenceHook)forgeModContainer.getMod());
        GameData.fireCreateRegistryEvents();
        CapabilityManager.INSTANCE.injectCapabilities(modList.getAllScanData());
        dispatchAndHandleError(LifecycleEventProvider.PREINIT);
        DistExecutor.runWhenOn(Dist.CLIENT, ModLoader::fireClientEvents);
        dispatchAndHandleError(LifecycleEventProvider.SIDEDINIT);
    }

    private void dispatchAndHandleError(LifecycleEventProvider event) {
        if (!loadingExceptions.isEmpty()) {
            LOGGER.error("Skipping lifecycle event {}, {} errors found.", event, loadingExceptions.size());
        } else {
            event.dispatch(this::accumulateErrors);
        }
        if (!loadingExceptions.isEmpty()) {
            LOGGER.error("Failed to complete lifecycle event {}, {} errors found", event, loadingExceptions.size());
            throw new LoadingFailedException(loadingExceptions);
        }
    }
    private void accumulateErrors(List<ModLoadingException> errors) {
        loadingExceptions.addAll(errors);
    }

    private List<ModContainer> buildMods(final ModFile modFile, final ModLoadingClassLoader modClassLoader)
    {
        final Map<String, IModInfo> modInfoMap = modFile.getModFileInfo().getMods().stream().collect(Collectors.toMap(IModInfo::getModId, Function.identity()));

        return modFile.getScanResult().getTargets().entrySet().stream().
                map(e-> {
                    try {
                        return e.getValue().<ModContainer>loadMod(modInfoMap.get(e.getKey()), modClassLoader, modFile.getScanResult());
                    } catch (ModLoadingException mle) {
                        loadingExceptions.add(mle);
                        return null;
                    }
                }).collect(Collectors.toList());
    }


    public void finishMods()
    {
        dispatchAndHandleError(LifecycleEventProvider.INIT);
        dispatchAndHandleError(LifecycleEventProvider.POSTINIT);
        dispatchAndHandleError(LifecycleEventProvider.COMPLETE);
        GameData.freezeData();
    }

}
