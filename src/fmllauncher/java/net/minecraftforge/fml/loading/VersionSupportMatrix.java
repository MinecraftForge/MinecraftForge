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

import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;

public class VersionSupportMatrix {
    private static final HashMap<String, List<ArtifactVersion>> overrideVersions = new HashMap<>();
    static {
        final ArtifactVersion version = new DefaultArtifactVersion(FMLLoader.mcVersion);
        if (MavenVersionAdapter.createFromVersionSpec("[1.16.4,1.16.5]").containsVersion(version)) {
            // 1.16.4 is Compatible with 1.16.3
            add("languageloader.javafml", "34");
            add("mod.minecraft",          "1.16.3");
            add("mod.forge",              "34.1.42");
            // 1.16.5 is Compatible with 1.16.4, and thus 1.16.3
            add("languageloader.javafml", "35");
            add("mod.minecraft",          "1.16.4");
            add("mod.forge",              "35.1.37");
        }
    }
    private static void add(String key, String value) {
        overrideVersions.computeIfAbsent(key, k -> new ArrayList<>()).add(new DefaultArtifactVersion(value));
    }
    public static <T> boolean testVersionSupportMatrix(VersionRange declaredRange, String lookupId, String type, BiPredicate<String, VersionRange> standardLookup) {
        if (standardLookup.test(lookupId, declaredRange)) return true;
        List<ArtifactVersion> custom = overrideVersions.get(type +"." +lookupId);
        return custom == null ? false  : custom.stream().anyMatch(declaredRange::containsVersion);
    }
}
