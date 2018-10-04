package net.minecraftforge.fml.loading;

import net.minecraftforge.fml.ForgeI18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.Objects;
import java.util.stream.Collectors;

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
    
    public static String artifactVersionToString(final ArtifactVersion artifactVersion) {
        return artifactVersion.toString();
    }

    public static String versionRangeToString(final VersionRange range) {
        return range.getRestrictions().stream().map(MavenVersionAdapter::restrictionToString).collect(Collectors.joining(","));
    }
    public static String restrictionToString(final Restriction restriction) {
        if ( restriction.getLowerBound() == null && restriction.getUpperBound() == null )
        {
            return ForgeI18n.parseMessage("fml.messages.version.restriction.any");
        }
        else if ( restriction.getLowerBound() != null && restriction.getUpperBound() != null )
        {
            if (Objects.equals(artifactVersionToString(restriction.getLowerBound()), artifactVersionToString(restriction.getUpperBound())))
            {
                return artifactVersionToString(restriction.getLowerBound());
            }
            else
            {
                if (restriction.isLowerBoundInclusive() && restriction.isUpperBoundInclusive())
                {
                    return ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.inclusive", restriction.getLowerBound(), restriction.getUpperBound());
                }
                else if (restriction.isLowerBoundInclusive())
                {
                    return ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.upperexclusive", restriction.getLowerBound(), restriction.getUpperBound());
                }
                else if (restriction.isUpperBoundInclusive())
                {
                    return ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.lowerexclusive", restriction.getLowerBound(), restriction.getUpperBound());
                }
                else
                {
                    return ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.exclusive", restriction.getLowerBound(), restriction.getUpperBound());
                }
            }
        }
        else if ( restriction.getLowerBound() != null )
        {
            if ( restriction.isLowerBoundInclusive() )
            {
                return ForgeI18n.parseMessage("fml.messages.version.restriction.lower.inclusive", restriction.getLowerBound());
            }
            else
            {
                return ForgeI18n.parseMessage("fml.messages.version.restriction.lower.exclusive", restriction.getLowerBound());
            }
        }
        else
        {
            if ( restriction.isUpperBoundInclusive() )
            {
                return ForgeI18n.parseMessage("fml.messages.version.restriction.upper.inclusive", restriction.getUpperBound());
            }
            else
            {
                return ForgeI18n.parseMessage("fml.messages.version.restriction.upper.exclusive", restriction.getUpperBound());
            }
        }
    }
}
