package net.minecraftforge.fml.server;

import net.minecraft.resources.*;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ModFileResourcePack;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

import java.nio.file.Files;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataPackLoader {
    private static Map<ModFile, ModFileResourcePack> modResourcePacks;
    private static AbstractResourcePack forgePack;
    private static ResourcePackList<?> resourcePackList;

    public static IResourcePack getResourcePackFor(String modId)
    {
        if (modId.equals("forge")) return forgePack;
        else return modResourcePacks.get(ModList.get().getModFileById(modId).getFile());
    }

    @SuppressWarnings("unchecked")
    public static <T extends ResourcePackInfo> T getResourcePackInfo(String modId) {
        return (T)resourcePackList.getPackInfo(modId);
    }

    public static <T extends ResourcePackInfo> void loadDataPacks(ResourcePackList<T> resourcePacks) {
        resourcePackList = resourcePacks;
        modResourcePacks = ModList.get().getModFiles().stream().
                map(mf -> new ModFileResourcePack(mf.getFile())).
                collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity()));
        forgePack = Files.isDirectory(FMLLoader.getForgePath()) ?
                new FolderPack(FMLLoader.getForgePath().toFile()) :
                new FilePack(FMLLoader.getForgePath().toFile());
        resourcePacks.addPackFinder(new ModPackFinder());
    }

    private static class ModPackFinder implements net.minecraft.resources.IPackFinder
    {
        @Override
        public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packList, ResourcePackInfo.IFactory<T> factory)
        {
            packList.put("forge", ResourcePackInfo.func_195793_a("forge", true, ()->forgePack, factory, ResourcePackInfo.Priority.BOTTOM));
        }
    }
}
