package net.minecraftforge.fml.loading.moddiscovery;

import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public abstract class AbstractJarFileProvider implements IModProvider
{
    private static final Logger LOGGER = LogManager.getLogger();

    public void scanFile(final IModFile file, final Consumer<Path> pathConsumer)
    {
        LOGGER.debug(SCAN, "Scan started: {}", file);
        final Function<Path, SecureJar.Status> status = p -> file.getSecureJar().verifyPath(p);
        try (Stream<Path> files = Files.find(file.getSecureJar().getRootPath(), Integer.MAX_VALUE, (p, a) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class")))
        {
            file.setSecurityStatus(files.peek(pathConsumer)
                                     .map(status)
                                     .reduce((s1, s2) -> SecureJar.Status.values()[Math.min(s1.ordinal(), s2.ordinal())])
                                     .orElse(SecureJar.Status.INVALID));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        LOGGER.debug(SCAN, "Scan finished: {}", file);
    }

    public boolean isValid(final IModFile modFile)
    {
        return true;
    }
}
