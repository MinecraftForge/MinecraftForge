package net.minecraftforge.fml.loading;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import static net.minecraftforge.fml.Logging.CORE;

public final class MavenVersionAdapter {
    private static final Logger LOGGER = LogManager.getLogger();
    private MavenVersionAdapter() {}

    public static VersionRange createFromVersionSpec(final String spec) {
        try {
            return VersionRange.createFromVersionSpec(spec);
        } catch (InvalidVersionSpecificationException e) {
            LOGGER.error(CORE, "Failed to parse version spec {}", spec, e);
            throw new RuntimeException("Failed to parse spec", e);
        }
    }

}
