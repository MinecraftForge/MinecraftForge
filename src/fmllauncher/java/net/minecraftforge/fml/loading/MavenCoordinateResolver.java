package net.minecraftforge.fml.loading;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Convert a maven coordinate into a Path.
 *
 * {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}, must not be {@code null}.
 */

public class MavenCoordinateResolver {
    public static Path get(final String coordinate) {
        final String[] parts = coordinate.split(":");
        final String groupId = parts[0];
        final String artifactId = parts[1];
        final String extension = parts.length > 3 ? parts[2] : "";
        final String classifier = parts.length > 4 ? parts[3] : "";
        final String version = parts[parts.length-1];
        return get(groupId, artifactId, extension, classifier, version);
    }

    public static Path get(final String groupId, final String artifactId, final String extension, final String classifier, final String version)
    {
        final String fileName = artifactId + "-" + version +
                (!classifier.isEmpty() ? "-"+ classifier : "") +
                (!extension.isEmpty() ? "." + extension : ".jar");

        String[] groups = groupId.split("\\.");
        Path result = Paths.get(groups[0]);
        for (int i = 1; i < groups.length; i++) {
            result = result.resolve(groups[i]);
        }

        return result.resolve(artifactId).resolve(version).resolve(fileName);
    }
}
