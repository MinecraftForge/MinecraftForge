/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.common.versioning;

public class DefaultArtifactVersion implements ArtifactVersion
{
    private ComparableVersion comparableVersion;
    private String label;
    private boolean unbounded;
    private VersionRange range;

    public DefaultArtifactVersion(String versionNumber)
    {
        comparableVersion = new ComparableVersion(versionNumber);
        range = VersionRange.createFromVersion(versionNumber, this);
    }

    public DefaultArtifactVersion(String label, VersionRange range)
    {
        this.label = label;
        this.range = range;
    }
    public DefaultArtifactVersion(String label, String version)
    {
        this(version);
        this.label = label;
    }

    public DefaultArtifactVersion(String string, boolean unbounded)
    {
        this.label = string;
        this.unbounded = true;
    }

    @Override
    public boolean equals(Object obj)
    {
        return ((DefaultArtifactVersion)obj).containsVersion(this);
    }

    @Override
    public int compareTo(ArtifactVersion o)
    {
        return unbounded ? 0 : this.comparableVersion.compareTo(((DefaultArtifactVersion)o).comparableVersion);
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public boolean containsVersion(ArtifactVersion source)
    {
        if (source.getLabel() != null && !source.getLabel().equals(getLabel()))
        {
            return false;
        }
        if (unbounded)
        {
            return true;
        }
        if (range != null)
        {
            return range.containsVersion(source);
        }
        else
        {
            return false;
        }
    }

    @Override
    public String getVersionString()
    {
        return comparableVersion == null ? "unknown" : comparableVersion.toString();
    }

    @Override
    public String getRangeString()
    {
        return range == null ? "any" : range.toString();
    }
    @Override
    public String toString()
    {
        if (label == null)
        {
            return getVersionString();
        }
        return label + (unbounded ? "" : "@" + range);
    }

    public VersionRange getRange()
    {
        return range;
    }
}
