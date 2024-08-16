/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

@ApiStatus.Internal // since 1.21.1, will be made non-public in a later MC version
public class VersionSupportMatrix {
    private static final Map<String, List<ArtifactVersion>> OVERRIDE_VERSIONS;

    static {
        if (FMLLoader.versionInfo().mcVersion().equals("1.21.1")) {
            OVERRIDE_VERSIONS = Map.ofEntries(
                    // 1.21.1 is compatible with 1.21
                    entry("languageloader.javafml", "51"),
                    entry("mod.minecraft",          "1.21"),
                    entry("mod.forge",              "51.0.33")
            );
        } else {
            OVERRIDE_VERSIONS = Collections.emptyMap();
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
        if (OVERRIDE_VERSIONS.isEmpty()) return false;
        List<ArtifactVersion> custom = OVERRIDE_VERSIONS.get(type + "." + lookupId);
        return custom != null && custom.stream().anyMatch(declaredRange::containsVersion);
    }

    private static Map.Entry<String, List<ArtifactVersion>> entry(String typeAndLookupId, String declaredRange) {
        return Map.entry(typeAndLookupId, List.of(new DefaultArtifactVersion(declaredRange)));
    }

    private static Map.Entry<String, List<ArtifactVersion>> entry(String typeAndLookupId, List<String> declaredRanges) {
        return Map.entry(typeAndLookupId, declaredRanges.stream().map(DefaultArtifactVersion::new).map(it -> (ArtifactVersion) it).toList());
    }
}
