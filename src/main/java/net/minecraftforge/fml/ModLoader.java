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

import net.minecraftforge.fml.language.IModInfo;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModLoader
{
    private static final Logger LOGGER = LogManager.getLogger("FML");
    private static ModLoader INSTANCE;
    private final ClassLoader launchClassLoader;
    private final LoadingModList loadingModList;
    private final ModLoadingClassLoader modClassLoader;

    private ModLoader()
    {
        INSTANCE = this;
        this.launchClassLoader = FMLLoader.getLaunchClassLoader();
        this.loadingModList = FMLLoader.getLoadingModList();
        this.modClassLoader = new ModLoadingClassLoader(this.launchClassLoader);
        Thread.currentThread().setContextClassLoader(this.modClassLoader);
    }

    public static ModLoader get()
    {
        return INSTANCE == null ? INSTANCE = new ModLoader() : INSTANCE;
    }

    public void loadMods() {
        final ModList modList = ModList.of(loadingModList.getModFiles().stream().map(ModFileInfo::getFile).collect(Collectors.toList()), loadingModList.getMods());
        modList.setLoadedMods(loadingModList.getModFiles().stream().
                map(ModFileInfo::getFile).
                map(mf -> buildMods(mf, modClassLoader)).
                flatMap(Collection::stream).collect(Collectors.toList()));

        LifecycleEventProvider.LOAD.dispatch();
        LifecycleEventProvider.CONSTRUCT.dispatch();
    }

    private List<ModContainer> buildMods(final ModFile modFile, final ModLoadingClassLoader modClassLoader)
    {
        final Map<String, IModInfo> modInfoMap = modFile.getModFileInfo().getMods().stream().collect(Collectors.toMap(IModInfo::getModId, Function.identity()));

        return modFile.getScanResult().getTargets().entrySet().stream().
                map(e->e.getValue().<ModContainer>loadMod(modInfoMap.get(e.getKey()), modClassLoader, modFile.getScanResult())).
                collect(Collectors.toList());
    }


}
