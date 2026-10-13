/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class VersionSupportMatrix {
    private static final Map<String, List<ArtifactVersion>> overrideVersions = Map.of(
            "mod.forge", List.of(new DefaultArtifactVersion("47.1.79"))
    );

    public static <T> boolean testVersionSupportMatrix(VersionRange declaredRange, String lookupId, String type, BiPredicate<String, VersionRange> standardLookup) {
        if (standardLookup.test(lookupId, declaredRange)) return true;
        return testVersionSupportMatrix(declaredRange, lookupId, type);
    }

    public static boolean testVersionSupportMatrix(VersionRange declaredRange, String lookupId, String type) {
        List<ArtifactVersion> custom = overrideVersions.get(type + '.' + lookupId);
        return custom != null && custom.stream().anyMatch(declaredRange::containsVersion);
    }
}
