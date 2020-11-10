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
