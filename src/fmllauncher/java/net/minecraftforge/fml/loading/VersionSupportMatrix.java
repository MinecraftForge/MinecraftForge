/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.HashMap;
import java.util.function.BiPredicate;

public class VersionSupportMatrix {
    private static final HashMap<String, ArtifactVersion> overrideVersions = new HashMap<>();
    static {
        final ArtifactVersion version = new DefaultArtifactVersion(FMLLoader.mcVersion);
        if (MavenVersionAdapter.createFromVersionSpec("[1.16.4]").containsVersion(version)) {
            overrideVersions.put("languageloader.javafml", new DefaultArtifactVersion("34")); // we also work with javafml 34
            overrideVersions.put("mod.minecraft", new DefaultArtifactVersion("1.16.3")); // we work with anything declaring 1.16.3
            overrideVersions.put("mod.forge", new DefaultArtifactVersion("34.1.42")); // we work with anything that supports forge 34.1.42
        }
    }
    public static <T> boolean testVersionSupportMatrix(VersionRange declaredRange, String lookupId, String type, BiPredicate<String, VersionRange> standardLookup) {
        return standardLookup.test(lookupId, declaredRange) || overrideVersions.containsKey(type +"." +lookupId) && declaredRange.containsVersion(overrideVersions.get(type +"." +lookupId));
    }
}
