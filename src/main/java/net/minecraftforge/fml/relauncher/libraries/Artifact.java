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

package net.minecraftforge.fml.relauncher.libraries;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.apache.maven.artifact.versioning.ComparableVersion;

public class Artifact implements Comparable<Artifact>
{
    private final Repository repo;
    private final String group;
    private final String artifact;
    private final String classifier;
    private final String extension;
    private final String value;
    private final ComparableVersion version;
    private final String timestamp;
    private final Date date;

    private String filename;
    private String folder;

    public Artifact(Repository repo, String value, String timestamp)
    {
        this.repo = repo;
        this.value = value;

        int idx = value.indexOf('@');
        if (idx > 0)
        {
            this.extension = value.substring(idx + 1);
            value = value.substring(0, idx);
        }
        else
            this.extension = "jar";

        String[] parts = value.split(":");
        this.group = parts[0];
        this.artifact = parts[1];
        this.version = new ComparableVersion(parts[2]);
        this.classifier = parts.length > 3 ? parts[3] : null;
        this.timestamp = isSnapshot() ? timestamp : null;
        try
        {
            this.date = this.timestamp == null ? null : SnapshotJson.TIMESTAMP.parse(this.timestamp);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e); //TODO: Better logging?
        }
    }

    public Artifact(Artifact other, Repository repo, String timestamp)
    {
        this.repo = repo;
        this.group = other.group;
        this.artifact = other.artifact;
        this.classifier = other.classifier;
        this.extension = other.extension;
        this.value = other.value;
        this.version = other.version;
        this.timestamp = timestamp;
        try
        {
            this.date = this.timestamp == null ? null : SnapshotJson.TIMESTAMP.parse(this.timestamp);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e); //TODO: Better logging?
        }
    }

    @Override
    public String toString()
    {
        return this.value;
    }

    @Override
    public int hashCode()
    {
        return this.value.hashCode();
    }

    public String getFilename()
    {
        if (this.filename == null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(artifact).append('-').append(version);
            if (isSnapshot() && timestamp != null)
                sb.append('-').append(getTimestamp());
            if (classifier != null)
                sb.append('-').append(classifier);
            sb.append('.').append(extension);
            this.filename = sb.toString();
        }
        return this.filename;
    }

    public String getFolder()
    {
        if (folder == null)
        {
            StringBuilder sb = new StringBuilder();
            for (String part : group.split("\\."))
                sb.append(part).append(File.separatorChar);
            sb.append(artifact).append(File.separatorChar);
            sb.append(version);
            folder = sb.toString();
        }
        return folder;
    }

    public String getPath()
    {
        return getFolder() + File.separatorChar + getFilename();
    }

    public File getFile()
    {
        return (repo != null ? repo : LibraryManager.getDefaultRepo()).getFile(getPath());
    }

    public File getSnapshotMeta()
    {
        if (!isSnapshot())
            throw new IllegalStateException("Attempted to call date suffix on non-snapshot");
        return (repo != null ? repo : LibraryManager.getDefaultRepo()).getFile(getFolder() + File.separatorChar + SnapshotJson.META_JSON_FILE);
    }

    public boolean isSnapshot()
    {
        return version.toString().toLowerCase(Locale.ENGLISH).endsWith("-snapshot");
    }

    public String getTimestamp()
    {
        if (!isSnapshot())
            throw new IllegalStateException("Attempted to call date suffix on non-snapshot");

        return timestamp;
    }

    public ComparableVersion getVersion()
    {
        return this.version;
    }

    public Repository getRepository()
    {
        return this.repo;
    }

    public boolean matchesID(Artifact o)
    {
        if (o == null)
            return false;
        return group.equals(o.group) && artifact.equals(o.artifact) && (o.classifier == null ? classifier == null : o.classifier.equals(classifier)); //TODO: Case sensitive?
    }

    public int compareVersion(Artifact o)
    {
        int ver = version.compareTo(o.version);
        if (ver != 0 || !isSnapshot()) return ver;
        return timestamp == null ? (o.timestamp == null ? 0 : -1) : o.timestamp == null ? 1 : date.compareTo(o.date);
    }

    @Override
    public int compareTo(Artifact o)
    {
        if (o == null) return 1;
        if (!group.equals(o.group)) return group.compareTo(o.group);
        if (!artifact.equals(o.artifact)) return artifact.compareTo(o.artifact);
        if (classifier == null && o.classifier != null) return -1;
        if (classifier != null && o.classifier == null) return 1;
        if (classifier != null && !classifier.equals(o.classifier)) return classifier.compareTo(o.classifier);
        return compareVersion(o);
    }
}
