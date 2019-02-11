/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.packs;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.CORE;

public class ResourcePackLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static Map<ModFile, ModFileResourcePack> modResourcePacks;
    private static ResourcePackList<?> resourcePackList;

    public static Optional<ModFileResourcePack> getResourcePackFor(String modId)
    {
        return Optional.ofNullable(ModList.get().getModFileById(modId)).
                map(ModFileInfo::getFile).map(mf->modResourcePacks.get(mf));
    }

    public static <T extends ResourcePackInfo> void loadResourcePacks(ResourcePackList<T> resourcePacks) {
        resourcePackList = resourcePacks;
        modResourcePacks = ModList.get().getModFiles().stream().
                map(mf -> new ModFileResourcePack(mf.getFile())).
                collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity()));
        resourcePacks.addPackFinder(new ModPackFinder());
    }

    private static class ModPackFinder implements IPackFinder
    {
        @Override
        public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packList, ResourcePackInfo.IFactory<T> factory)
        {
            for (Entry<ModFile, ModFileResourcePack> e : modResourcePacks.entrySet())
            {
                final String name = "mod:" + e.getKey().getModInfos().get(0).getModId();
                final T packInfo = ResourcePackInfo.func_195793_a(name, true, e::getValue, factory, ResourcePackInfo.Priority.BOTTOM);
                e.getValue().setPackInfo(packInfo);
                LOGGER.debug(CORE, "Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
                packList.put(name, packInfo);
            }
        }
        
    }
}
