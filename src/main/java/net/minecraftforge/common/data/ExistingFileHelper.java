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

package net.minecraftforge.common.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import com.google.common.annotations.VisibleForTesting;

import java.util.Collections;
import java.util.Set;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.packs.ModFileResourcePack;

/**
 * Enables data providers to check if other data files currently exist. The
 * instance provided in the {@link GatherDataEvent} utilizes the standard
 * resources (via {@link VanillaPack}), forge's resources, as well as any
 * extra resource packs passed in via the {@code --existing} argument,
 * or mod resources via the {@code --existing-mod} argument.
 */
public class ExistingFileHelper {

    private final SimpleReloadableResourceManager clientResources, serverData;
    private final boolean enable;

    @Deprecated//TODO: Remove in 1.17
    public ExistingFileHelper(Collection<Path> existingPacks, boolean enable) {
        this(existingPacks, Collections.emptySet(), enable);
    }

    public ExistingFileHelper(Collection<Path> existingPacks, Set<String> existingMods, boolean enable) {
        this.clientResources = new SimpleReloadableResourceManager(ResourcePackType.CLIENT_RESOURCES);
        this.serverData = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
        this.clientResources.addResourcePack(new VanillaPack("minecraft", "realms"));
        this.serverData.addResourcePack(new VanillaPack("minecraft"));
        for (Path existing : existingPacks) {
            File file = existing.toFile();
            IResourcePack pack = file.isDirectory() ? new FolderPack(file) : new FilePack(file);
            this.clientResources.addResourcePack(pack);
            this.serverData.addResourcePack(pack);
        }
        for (String existingMod : existingMods) {
            ModFileInfo modFileInfo = ModList.get().getModFileById(existingMod);
            if (modFileInfo != null) {
                IResourcePack pack = new ModFileResourcePack(modFileInfo.getFile());
                this.clientResources.addResourcePack(pack);
                this.serverData.addResourcePack(pack);
            }
        }
        this.enable = enable;
    }

    private IResourceManager getManager(ResourcePackType type) {
        return type == ResourcePackType.CLIENT_RESOURCES ? clientResources : serverData;
    }

    private ResourceLocation getLocation(ResourceLocation base, String suffix, String prefix) {
        return new ResourceLocation(base.getNamespace(), prefix + "/" + base.getPath() + suffix);
    }

    /**
     * Check if a given resource exists in the known resource packs.
     *
     * @param loc        the base location of the resource, e.g.
     *                   {@code "minecraft:block/stone"}
     * @param type       the type of resources to check
     * @return {@code true} if the resource exists in any pack, {@code false}
     *         otherwise
     */
    public boolean exists(ResourceLocation loc, ResourcePackType type) {
        if (!enable) {
            return true;
        }
        return getManager(type).hasResource(loc);
    }

    /**
     * Check if a given resource exists in the known resource packs.
     * 
     * @param loc        the base location of the resource, e.g.
     *                   {@code "minecraft:block/stone"}
     * @param type       the type of resources to check
     * @param pathSuffix a string to append after the path, e.g. {@code ".json"}
     * @param pathPrefix a string to append before the path, before a slash, e.g.
     *                   {@code "models"}
     * @return {@code true} if the resource exists in any pack, {@code false}
     *         otherwise
     */
    public boolean exists(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix) {
        return exists(getLocation(loc, pathSuffix, pathPrefix), type);
    }

    @VisibleForTesting
    public IResource getResource(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix) throws IOException {
        return getResource(getLocation(loc, pathSuffix, pathPrefix), type);
    }

    @VisibleForTesting
    public IResource getResource(ResourceLocation loc, ResourcePackType type) throws IOException {
        return getManager(type).getResource(loc);
    }

    /**
     * @return {@code true} if validation is enabled, {@code false} otherwise
     */
    public boolean isEnabled() {
        return enable;
    }
}
