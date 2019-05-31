package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import cpw.mods.modlauncher.serviceapi.ITransformerDiscoveryService;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

public class ModDirTransformerDiscoverer implements ITransformerDiscoveryService {
    @Override
    public List<Path> candidates(final Path gameDirectory) {
        final Path modsDir = gameDirectory.resolve(FMLPaths.MODSDIR.relative());
        List<Path> paths = new ArrayList<>();
        try {
            Files.walk(modsDir, 1).forEach(p -> {
                if (!Files.isRegularFile(p)) return;
                if (!p.toString().endsWith(".jar")) return;
                if (LamdbaExceptionUtils.uncheck(()->Files.size(p)) == 0) return;
                try (ZipFile zf = new ZipFile(new File(p.toUri()))) {
                    if (zf.getEntry("META-INF/services/cpw.mods.modlauncher.api.ITransformationService") != null) {
                        paths.add(p);
                    }
                } catch (IOException ioe) {
                    LogManager.getLogger().error("Zip Error when loading jar file {}", p, ioe);
                }
            });
        } catch (IOException | IllegalStateException ioe) {
            LogManager.getLogger().error("Error during early discovery", ioe);
        }
        return paths;
    }
}