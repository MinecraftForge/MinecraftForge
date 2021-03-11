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

package net.minecraftforge.fml;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.Objects;
import java.util.stream.Collectors;

public class MavenVersionStringHelper {
    public static String artifactVersionToString(final ArtifactVersion artifactVersion) {
        return artifactVersion.toString();
    }

    public static String versionRangeToString(final VersionRange range) {
        return range.getRestrictions().stream().map(MavenVersionStringHelper::restrictionToString).collect(Collectors.joining(", "));
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

    public static void parseVersionRange(final String formatString, final StringBuffer stringBuffer, final Object range) {
        stringBuffer.append(versionRangeToString((VersionRange) range));
    }
}
