package net.minecraftforge.client.model.generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import com.google.common.annotations.VisibleForTesting;

import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * Enables data providers to check if other data files currently exist. The
 * instance provided in the {@link GatherDataEvent} utilizes the standard
 * resources (via {@link VanillaPack}), as well as any extra resource packs
 * passed in via the {@code --existing} argument.
 */
public class ExistingFileHelper {

    private final IResourceManager clientResources, serverData;
    private final boolean enable;

    public ExistingFileHelper(Collection<Path> existingPacks, boolean enable) {
        this.clientResources = new SimpleReloadableResourceManager(ResourcePackType.CLIENT_RESOURCES, Thread.currentThread());
        this.serverData = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA, Thread.currentThread());
        this.clientResources.addResourcePack(new VanillaPack("minecraft", "realms"));
        this.serverData.addResourcePack(new VanillaPack("minecraft"));
        for (Path existing : existingPacks) {
            File file = existing.toFile();
            IResourcePack pack = file.isDirectory() ? new FolderPack(file) : new FilePack(file);
            this.clientResources.addResourcePack(pack);
            this.serverData.addResourcePack(pack);
        };
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
     * @param pathSuffix a string to append after the path, e.g. {@code ".json"}
     * @param pathPrefix a string to append before the path, before a slash, e.g.
     *                   {@code "models"}
     * @return {@code true} if the resource exists in any pack, {@code false}
     *         otherwise
     */
    public boolean exists(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix) {
        if (!enable) {
            return true;
        }
        return getManager(type).hasResource(getLocation(loc, pathSuffix, pathPrefix));
    }

    @VisibleForTesting
    public IResource getResource(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix) throws IOException {
        return getManager(type).getResource(getLocation(loc, pathSuffix, pathPrefix));
    }

    /**
     * @return {@code true} if validation is enabled, {@code false} otherwise
     */
    public boolean isEnabled() {
        return enable;
    }
}
