/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.loading;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Convert a maven coordinate into a Path.
 *
 * This is gradle standard not maven standard coordinate formatting
 * {@code <groupId>:<artifactId>[:<classifier>]:<version>[@extension]}, must not be {@code null}.
 */

public class MavenCoordinateResolver {
    public static Path get(final String coordinate) {
        final String[] parts = coordinate.split(":");
        final String groupId = parts[0];
        final String artifactId = parts[1];
        final String classifier = parts.length > 3 ? parts[2] : "";
        final String[] versext = parts[parts.length-1].split("@");
        final String version = versext[0];
        final String extension = versext.length > 1 ? versext[1] : "";
        return get(groupId, artifactId, extension, classifier, version);
    }

    public static Path get(final String groupId, final String artifactId, final String extension, final String classifier, final String version)
    {
        final String fileName = artifactId + "-" + version +
                (!classifier.isEmpty() ? "-" + classifier : "") +
                (!extension.isEmpty() ? "." + extension : ".jar");

        String[] groups = groupId.split("\\.");
        Path result = Paths.get(groups[0]);
        for (int i = 1; i < groups.length; i++) {
            result = result.resolve(groups[i]);
        }

        return result.resolve(artifactId).resolve(version).resolve(fileName);
    }
}
