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

package net.minecraftforge.fml.packs;

import net.minecraft.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResourcePackLoader
{
    private static Map<ModFile, ModFileResourcePack> modResourcePacks;
    private static ResourcePackList<?> resourcePackList;

    public static ModFileResourcePack getResourcePackFor(String modId)
    {
        return modResourcePacks.get(ModList.get().getModFileById(modId).getFile());
    }

    @SuppressWarnings("unchecked")
    public static <T extends ResourcePackInfo> T getResourcePackInfoForModId(String modId) {
        if (Objects.equals(modId, "minecraft")) {
            // Additional resources for MC are associated with forge
            return getResourcePackFor("forge").getPackInfo();
        }
        return getResourcePackFor(modId).getPackInfo();
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
                String name = "modfile/" + e.getKey().getFileName();
                final T packInfo = ResourcePackInfo.func_195793_a(name, true, () -> e.getValue(), factory, ResourcePackInfo.Priority.BOTTOM);
                e.getValue().setPackInfo(packInfo);
                packList.put(name, packInfo);
            }
        }
        
    }
}
