package net.minecraftforge.client.model.generators;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;

public class ExistingFileHelper {

    private final IResourceManager resources;
    private final boolean enable;

    public ExistingFileHelper(Collection<Path> existingPacks, boolean enable) {
        this.resources = new SimpleReloadableResourceManager(ResourcePackType.CLIENT_RESOURCES, Thread.currentThread());
        this.resources.addResourcePack(new VanillaPack("minecraft", "realms"));
        for (Path existing : existingPacks) {
            File file = existing.toFile();
            this.resources.addResourcePack(file.isDirectory() ? new FolderPack(file) : new FilePack(file));
        };
        this.enable = enable;
    }

    public boolean exists(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix) {
        if (!enable) {
            return true;
        }
        return resources.hasResource(new ResourceLocation(loc.getNamespace(), pathPrefix + "/" + loc.getPath() + pathSuffix));
    }

    public boolean isEnabled() {
        return enable;
    }
}
