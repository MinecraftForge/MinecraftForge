package net.minecraftforge.common.dimension;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//TODO: Better name?
public class DimensionDeletionManager
{
    private static final Logger DIM_DEL = LogManager.getLogger();
    private static final Path PARENT_DIRECTORY = FMLPaths.GAMEDIR.get();
    private static Map<String, Predicate<File>> VALID_DIM_PREDICATE = new HashMap<>();

    public static void init(ForgeConfig.Server config)
    {

    }

    public static void startDeletionStage(MinecraftServer server)
    {
        List<String> worlds = ForgeConfig.SERVER.dimensionsToProcess.get();
        DIM_DEL.info("Dimensions to process: " + worlds.toString());
    }


    private static Set<String> getVanillaDimensionDirectories()
    {
        Set<DimensionType> vanillaDimensions = DimensionManager.getRegistry().stream()
                .filter(DimensionType::isVanilla)
                .collect(Collectors.toSet());
        return vanillaDimensions.stream()
                .map(d -> d.getDirectory(PARENT_DIRECTORY.toFile()).toString())
                .collect(Collectors.toSet());
    }

    public static class Entry
    {
        private Entry(DimensionType type)
        {
            dimensionTypeRegistryName = type.getRegistryName();
            isVanillaDimension = type.isVanilla();
            dimensionDirPath = type.getDirectory(PARENT_DIRECTORY.toFile()).toPath().toAbsolutePath().normalize();
        }

        private final ResourceLocation dimensionTypeRegistryName;
        private final boolean isVanillaDimension;
        private final Path dimensionDirPath;

        public ResourceLocation getDimensionTypeRegistryName()
        {
            return dimensionTypeRegistryName;
        }

        public boolean isVanillaDimension()
        {
            return isVanillaDimension;
        }

        public Path getDimensionDirPath()
        {
            return dimensionDirPath;
        }
    }
}
