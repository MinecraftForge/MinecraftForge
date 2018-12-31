package net.minecraftforge.fml.loading;

import com.google.common.collect.ObjectArrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class LibraryFinder {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Path libsPath;
    static Path findLibsPath() {
        if (libsPath == null) {
            final Path asm = findJarPathFor("org/objectweb/asm/Opcodes.class", "asm");
            // go up SIX parents to find the libs dir
            final Path libs = asm.getParent().getParent().getParent().getParent().getParent().getParent();
            LOGGER.debug(CORE, "Found probable library path {}", libs);
            libsPath = libs;
        }
        return libsPath;
    }

    static Path findJarPathFor(final String className, final String jarName) {
        final URL resource = LibraryFinder.class.getClassLoader().getResource(className);
        try {
            Path path;
            final URI uri = resource.toURI();
            if (uri.getRawSchemeSpecificPart().contains("!")) {
                path = Paths.get(new URI(uri.getRawSchemeSpecificPart().split("!")[0]));
            } else {
                path = Paths.get(new URI("file://"+uri.getRawSchemeSpecificPart().substring(0, uri.getRawSchemeSpecificPart().length()-className.length())));
            }
            LOGGER.debug(CORE, "Found JAR {} at path {}", jarName, path.toString());
            return path;
        } catch (NullPointerException | URISyntaxException e) {
            LOGGER.error(CORE, "Failed to find JAR for class {} - {}", className, jarName);
            throw new RuntimeException("Unable to locate "+className+" - "+jarName, e);
        }
    }
    static Path[] commonLibPaths(Path[] extras) {
        final Path realms = findJarPathFor("com/mojang/realmsclient/RealmsVersion.class", "realms");
        return ObjectArrays.concat(extras, realms);
    }

    static Path getForgeLibraryPath(final String mcVersion, final String forgeVersion) {
        Path forgePath = findLibsPath().resolve(MavenCoordinateResolver.get("net.minecraftforge", "forge", "", "", mcVersion+"-"+forgeVersion));
        LOGGER.debug(CORE, "Found forge path {} is {}", forgePath, pathStatus(forgePath));
        return forgePath;
    }

    static String pathStatus(final Path path) {
        return Files.exists(path) ? "present" : "missing";
    }
    static Path[] getMCPaths(final String mcVersion, final String forgeVersion, final String type) {
        Path srgMcPath = findLibsPath().resolve(MavenCoordinateResolver.get("net.minecraft", type, "", "srg", mcVersion));
        Path patchedBinariesPath = findLibsPath().resolve(MavenCoordinateResolver.get("net.minecraftforge", "forge", "", type, mcVersion+"-"+forgeVersion));
        LOGGER.info("SRG MC at {} is {}", srgMcPath.toString(), pathStatus(srgMcPath));
        LOGGER.info("Forge patches at {} is {}", patchedBinariesPath.toString(), pathStatus(patchedBinariesPath));
        return new Path[] { srgMcPath, patchedBinariesPath };
    }
}
