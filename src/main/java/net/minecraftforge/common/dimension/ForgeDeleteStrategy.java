package net.minecraftforge.common.dimension;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * Represents how Forge deletes a dimension's directory.
 */
public final class ForgeDeleteStrategy implements Runnable
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final DimensionDeletionManager.Entry entry;
    private final Predicate<File> directoryPredicate;

    protected ForgeDeleteStrategy(DimensionDeletionManager.Entry entry, Predicate<File> directoryPredicate)
    {
        this.entry = entry;
        this.directoryPredicate = directoryPredicate;
    }

    private void deleteDirectory(Path path)
    {
        if (directoryPredicate.test(path.toFile())) {
            try {
                FileUtils.forceDelete(path.toFile());
            } catch (IOException e) {
                LOGGER.error("Unable to delete a file or directory", e);
            }
        }
    }

    @Override
    public void run()
    {
        deleteDirectory(entry.getDimensionDirPath());
    }
}
