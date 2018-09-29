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
import net.minecraftforge.fml.language.ModFileScanData;
import net.minecraftforge.fml.loading.DefaultModInfos;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.LOADING;

/**
 * Master list of all mods - game-side version. This is classloaded in the game scope and
 * can dispatch game level events as a result.
 */
public class ModList
{
    private static Logger LOGGER = LogManager.getLogger();
    private static ModList INSTANCE;
    private final List<ModFileInfo> modFiles;
    private final List<ModInfo> sortedList;
    private final Map<String, ModFileInfo> fileById;
    private List<ModContainer> mods;
    private Map<String, ModContainer> indexedMods;
    private ForkJoinPool modLoadingThreadPool = new ForkJoinPool();
    private List<ModFileScanData> modFileScanData;

    private ModList(final List<ModFile> modFiles, final List<ModInfo> sortedList)
    {
        this.modFiles = modFiles.stream().map(ModFile::getModFileInfo).map(ModFileInfo.class::cast).collect(Collectors.toList());
        this.sortedList = sortedList.stream().
                map(ModInfo.class::cast).
                collect(Collectors.toList());
        this.fileById = this.modFiles.stream().map(ModFileInfo::getMods).flatMap(Collection::stream).
                map(ModInfo.class::cast).
                collect(Collectors.toMap(ModInfo::getModId, ModInfo::getOwningFile));
    }

    public static ModList of(List<ModFile> modFiles, List<ModInfo> sortedList)
    {
        INSTANCE = new ModList(modFiles, sortedList);
        return INSTANCE;
    }

    public static ModList get() {
        return INSTANCE;
    }

    public List<ModFileInfo> getModFiles()
    {
        return modFiles;
    }


    public ModFileInfo getModFileById(String modid)
    {
        return this.fileById.get(modid);
    }

    public void dispatchLifeCycleEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent, final Consumer<List<ModLoadingException>> errorHandler) {
        FMLLoader.getLanguageLoadingProvider().forEach(lp->lp.preLifecycleEvent(lifecycleEvent));
        DeferredWorkQueue.deferredWorkQueue.clear();
        try
        {
            modLoadingThreadPool.submit(()->this.mods.parallelStream().forEach(m->m.transitionState(lifecycleEvent, errorHandler))).get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            LOGGER.error(LOADING, "Encountered an exception during parallel processing", e);
        }
        LOGGER.debug(LOADING, "Dispatching synchronous work, {} jobs", DeferredWorkQueue.deferredWorkQueue.size());
        DeferredWorkQueue.deferredWorkQueue.forEach(FutureTask::run);
        LOGGER.debug(LOADING, "Synchronous work queue complete");
        FMLLoader.getLanguageLoadingProvider().forEach(lp->lp.postLifecycleEvent(lifecycleEvent));
    }

    public void setLoadedMods(final List<ModContainer> modContainers)
    {
        this.mods = modContainers;
        this.indexedMods = modContainers.stream().collect(Collectors.toMap(ModContainer::getModId, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getModObjectById(String modId)
    {
        return getModContainerById(modId).map(ModContainer::getMod).map(o -> (T) o);
    }

    public Optional<? extends ModContainer> getModContainerById(String modId)
    {
        return Optional.ofNullable(this.indexedMods.get(modId));
    }
    
    public Optional<? extends ModContainer> getModContainerByObject(Object obj)
    {
        return mods.stream().filter(mc -> mc.getMod() == obj).findFirst();
    }

    public List<ModInfo> getMods()
    {
        return this.sortedList;
    }

    public boolean isLoaded(String modTarget)
    {
        return this.indexedMods.containsKey(modTarget);
    }

    public int size()
    {
        return mods.size();
    }

    public List<ModFileScanData> getAllScanData()
    {
        if (modFileScanData == null)
        {
            modFileScanData = this.sortedList.stream().
                    map(ModInfo::getOwningFile).
                    filter(Objects::nonNull).
                    map(ModFileInfo::getFile).
                    map(ModFile::getScanResult).
                    collect(Collectors.toList());
        }
        return modFileScanData;

    }

    public void forEachModFile(Consumer<ModFile> fileConsumer)
    {
        modFiles.stream().map(ModFileInfo::getFile).forEach(fileConsumer);
    }
}
