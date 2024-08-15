/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class VersionSupportMatrix {
    private static final boolean ENABLED = false;
    private static final Map<String, List<ArtifactVersion>> overrideVersions;

    static {
        if (ENABLED) {
            overrideVersions = new HashMap<>();
            final ArtifactVersion version = new DefaultArtifactVersion(FMLLoader.versionInfo().mcVersion());
            final ArtifactVersion version = new DefaultArtifactVersion(FMLLoader.versionInfo().mcVersion());
            if (MavenVersionAdapter.createFromVersionSpec("[1.21.1]").containsVersion(version)) {
                // 1.21.1 is Compatible with 1.21
                add("languageloader.javafml", "51");
                add("mod.minecraft",          "1.21");
                add("mod.forge",              "51.0.33");
            }
        } else {
            overrideVersions = null;
        }
    }

    private static void add(String key, String value) {
        overrideVersions.computeIfAbsent(key, k -> new ArrayList<>()).add(new DefaultArtifactVersion(value));
    }

    public static boolean testVersionSupportMatrix(VersionRange declaredRange, String lookupId, String type, BiPredicate<String, VersionRange> standardLookup) {
        if (standardLookup.test(lookupId, declaredRange)) return true;
        List<ArtifactVersion> custom = overrideVersions.get(type + "." + lookupId);
        return custom == null ? false  : custom.stream().anyMatch(declaredRange::containsVersion);
    }

    static boolean isEnabled() {
        return ENABLED;
    }
}
