/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fml.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import org.apache.commons.lang3.tuple.Pair;

public class MissingModsException extends EnhancedRuntimeException
{
    private static final long serialVersionUID = 1L;
    @Deprecated
    public final Set<ArtifactVersion> missingMods;
    private final List<Pair<ArtifactVersion, ArtifactVersion>> missingModsVersions; // needed version, have version (nullable)
    private final String modName;

    /**
     * @deprecated use {@link #MissingModsException(List, String, String)}
     */
    @Deprecated // TODO remove in 1.13
    public MissingModsException(Set<ArtifactVersion> missingMods, String id, String name)
    {
        super(String.format("Mod %s (%s) requires %s", id, name, missingMods));
        this.missingMods = missingMods;
        this.missingModsVersions = new ArrayList<>();
        for (ArtifactVersion artifactVersion : missingMods)
        {
            missingModsVersions.add(Pair.of(artifactVersion, null));
        }
        this.modName = name;
    }

    public MissingModsException(List<Pair<ArtifactVersion, ArtifactVersion>> missingModsVersions, String id, String name)
    {
        super(String.format("Mod %s (%s) requires %s", id, name, missingModsVersions));
        this.missingMods = new HashSet<>();
        for (Pair<ArtifactVersion, ArtifactVersion> missingModsVersion : missingModsVersions)
        {
            this.missingMods.add(missingModsVersion.getKey());
        }
        this.missingModsVersions = missingModsVersions;
        this.modName = name;
    }

    public String getModName()
    {
        return modName;
    }

    public List<Pair<ArtifactVersion, ArtifactVersion>> getMissingModsVersions()
    {
        return missingModsVersions;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        stream.println("Missing Mods:");
        for (Pair<ArtifactVersion, ArtifactVersion> entry : missingModsVersions)
        {
            ArtifactVersion needVersion = entry.getKey();
            ArtifactVersion haveVersion = entry.getValue();
            String haveString = haveVersion != null ? haveVersion.getVersionString() : "null";
            stream.println(String.format("\t%s : need %s: have %s", needVersion.getLabel(), needVersion.getRangeString(), haveString));
        }
        stream.println("");
    }
}
