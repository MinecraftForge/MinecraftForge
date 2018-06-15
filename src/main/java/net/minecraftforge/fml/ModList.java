/*
 * Minecraft Forge
 * Copyright (c) 2018.
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
import net.minecraftforge.fml.loading.DefaultModInfos;
import net.minecraftforge.fml.loading.moddiscovery.BackgroundScanHandler;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Master list of all mods
 */
public class ModList
{
    private static ModList INSTANCE;
    private final List<ModFileInfo> modFiles;
    private final List<ModInfo> sortedList;
    private final Map<String, ModFileInfo> fileById;
    private BackgroundScanHandler scanner;
    private List<ModContainer> mods;
    private Map<String, ModContainer> indexedMods;

    private ModList(final List<ModFile> modFiles, final List<ModInfo> sortedList)
    {
        this.modFiles = modFiles.stream().map(ModFile::getModFileInfo).map(ModFileInfo.class::cast).collect(Collectors.toList());
        this.sortedList = Streams.concat(DefaultModInfos.getModInfos().stream(), sortedList.stream()).
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

    public void dispatchLifeCycleEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
        this.mods.parallelStream().forEach(m->m.transitionState(lifecycleEvent));
        final List<ModContainer> erroredContainers = this.mods.stream().filter(m -> m.getCurrentState() == ModLoadingStage.ERROR).collect(Collectors.toList());
        if (!erroredContainers.isEmpty()) {
            throw new RuntimeException("Errored containers found!");
        }
    }

    public void setLoadedMods(final List<ModContainer> modContainers)
    {
        this.mods = modContainers;
        this.indexedMods = modContainers.stream().collect(Collectors.toMap(ModContainer::getModId, Function.identity()));
    }

    public Optional<Object> getModObjectById(String modId)
    {
        return getModContainerById(modId).map(ModContainer::getMod);
    }

    public Optional<? extends ModContainer> getModContainerById(String modId)
    {
        return Optional.ofNullable(this.indexedMods.get(modId));
    }

    public List<ModInfo> getMods()
    {
        return this.sortedList;
    }

    public boolean isLoaded(String modTarget)
    {
        return this.indexedMods.containsKey(modTarget);
    }
}
