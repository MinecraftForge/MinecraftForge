/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class VersionSupportMatrix {
    private static final boolean ENABLED = FMLLoader.versionInfo().mcVersion().equals("1.21.1");
    private static final Map<String, List<ArtifactVersion>> overrideVersions;

    static {
        if (ENABLED) {
            overrideVersions = Map.ofEntries(
                    // 1.21.1 is Compatible with 1.21
                    entry("languageloader.javafml", "51"),
                    entry("mod.minecraft",          "1.21"),
                    entry("mod.forge",              "51.0.33")
            );
        } else {
            overrideVersions = Collections.emptyMap();
        }
    }

    /**
     * @deprecated Use {@link #testVersionSupportMatrix(VersionRange, String, String)} instead, unwrapping your BiPredicate.
     */
    @Deprecated(forRemoval = true, since = "1.21.1")
    public static boolean testVersionSupportMatrix(VersionRange declaredRange, String lookupId, String type, BiPredicate<String, VersionRange> standardLookup) {
        if (standardLookup.test(lookupId, declaredRange)) return true;
        return testVersionSupportMatrix(declaredRange, lookupId, type);
    }

    public static boolean testVersionSupportMatrix(VersionRange declaredRange, String lookupId, String type) {
        List<ArtifactVersion> custom = overrideVersions.get(type + "." + lookupId);
        return custom != null && custom.stream().anyMatch(declaredRange::containsVersion);
    }

    static boolean isEnabled() {
        return ENABLED;
    }

    private static Map.Entry<String, List<ArtifactVersion>> entry(String typeAndLookupId, String declaredRange) {
        return Map.entry(typeAndLookupId, List.of(new DefaultArtifactVersion(declaredRange)));
    }

    private static Map.Entry<String, List<ArtifactVersion>> entry(String typeAndLookupId, List<String> declaredRanges) {
        return Map.entry(typeAndLookupId, declaredRanges.stream().map(DefaultArtifactVersion::new).map(it -> (ArtifactVersion) it).toList());
    }
}
