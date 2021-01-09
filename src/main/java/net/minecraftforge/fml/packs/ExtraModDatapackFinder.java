package net.minecraftforge.fml.packs;

import net.minecraft.resources.*;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ExtraModDatapackFinder implements IPackFinder
{
    /**
     * Copied from {@link FolderPackFinder}
     */
    private static final FileFilter FILE_FILTER = (file) ->
    {
        boolean isZip = file.isFile() && file.getName().endsWith(".zip");
        boolean isDirWithMcMeta = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
        return isZip || isDirWithMcMeta;
    };

    @Override
    public void findPacks(Consumer<ResourcePackInfo> consumer, ResourcePackInfo.IFactory factory)
    {
        ModList.get().getModFiles().stream().map(ModFileInfo::getFile).forEach(mf ->
        {
            File packs = mf.getLocator().findPath(mf, "packs").toFile();
            if (!packs.exists() || !packs.isDirectory())
                return;

            for (File f : packs.listFiles(FILE_FILTER))
            {
                Supplier<IResourcePack> pack = () -> f.isDirectory() ? new FolderPack(f) : new FilePack(f);
                ResourcePackInfo info = ResourcePackInfo.createResourcePack("file/" + f.getName(), false, pack, factory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.PLAIN);
                if (info != null)
                    consumer.accept(info);
            }
        });
    }
}
