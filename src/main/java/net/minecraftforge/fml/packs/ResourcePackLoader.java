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
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResourcePackLoader
{
    private static Map<ModFile, ModFileResourcePack> modResourcePacks;
    private static AbstractResourcePack forgePack;
    private static ResourcePackList<?> resourcePackList;

    public static IResourcePack getResourcePackFor(String modId)
    {
        if (modId == "forge") return forgePack;
        else return modResourcePacks.get(ModList.get().getModFileById(modId).getFile());
    }

    @SuppressWarnings("unchecked")
    public static <T extends ResourcePackInfo> T getResourcePackInfo(String modId) {
        return (T)resourcePackList.getPackInfo(modId);
    }

    public static <T extends ResourcePackInfo> void loadResourcePacks(ResourcePackList<T> resourcePacks) {
        resourcePackList = resourcePacks;
        modResourcePacks = ModList.get().getModFiles().stream().
                map(mf -> new ModFileResourcePack(mf.getFile())).
                collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity()));
        forgePack = Files.isDirectory(FMLLoader.getForgePath()) ?
                new ForgeFolderPack(FMLLoader.getForgePath().toFile()) :
                new ForgeFilePack(FMLLoader.getForgePath().toFile());
        resourcePacks.addPackFinder(new ModPackFinder());
    }

    private static class ForgeFolderPack extends FolderPack {
        public ForgeFolderPack(final File folder) {
            super(folder);
        }

        public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
            if (location.getPath().startsWith("lang/")) {
                return super.getResourceStream(ResourcePackType.CLIENT_RESOURCES, location);
            } else {
                return super.getResourceStream(type, location);
            }
        }

        public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
            if (location.getPath().startsWith("lang/")) {
                return super.resourceExists(ResourcePackType.CLIENT_RESOURCES, location);
            } else {
                return super.resourceExists(type, location);
            }
        }
    }

    private static class ForgeFilePack extends FilePack {
        public ForgeFilePack(final File folder) {
            super(folder);
        }

        public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
            if (location.getPath().startsWith("lang/")) {
                return super.getResourceStream(ResourcePackType.CLIENT_RESOURCES, location);
            } else {
                return super.getResourceStream(type, location);
            }
        }

        public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
            if (location.getPath().startsWith("lang/")) {
                return super.resourceExists(ResourcePackType.CLIENT_RESOURCES, location);
            } else {
                return super.resourceExists(type, location);
            }
        }

    }
    private static class ModPackFinder implements IPackFinder
    {
        @Override
        public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packList, ResourcePackInfo.IFactory<T> factory)
        {
            packList.put("forge", ResourcePackInfo.func_195793_a("forge", true, ()->forgePack, factory, ResourcePackInfo.Priority.BOTTOM));
        }
    }
}
