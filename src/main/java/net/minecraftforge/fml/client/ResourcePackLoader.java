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

package net.minecraftforge.fml.client;

import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.resources.AbstractResourcePack;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResourcePackLoader
{
    private static Map<ModFile, ModFileResourcePack> modResourcePacks;
    private static AbstractResourcePack forgePack;
    private static ResourcePackList<?> resourcePackList;

    public static IResourcePack getResourcePackFor(String modId)
    {
        return modResourcePacks.get(ModList.get().getModFileById(modId).getFile());
    }

    @SuppressWarnings("unchecked")
    public static <T extends ResourcePackInfo> T getResourcePackInfo(String modId) {
        return (T)resourcePackList.func_198981_a(modId);
    }

    public static <T extends ResourcePackInfo> void loadResourcePacks(ResourcePackList<T> resourcePacks) {
        resourcePackList = resourcePacks;
        modResourcePacks = ModList.get().getModFiles().stream().
                map(mf -> new ModFileResourcePack(mf.getFile())).
                collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity()));
        forgePack = Files.isDirectory(FMLLoader.getForgePath()) ?
                new FolderPack(FMLLoader.getForgePath().toFile()) :
                new FilePack(FMLLoader.getForgePath().toFile());
        resourcePacks.func_198982_a(new ModPackFinder());
    }

    private static class ModPackFinder implements net.minecraft.resources.IPackFinder
    {
        @Override
        public <T extends ResourcePackInfo> void func_195730_a(Map<String, T> packList, ResourcePackInfo.IFactory<T> factory)
        {
            packList.put("forge", ResourcePackInfo.func_195793_a("forge", true, ()->forgePack, factory, ResourcePackInfo.Priority.BOTTOM));
        }
    }
}
