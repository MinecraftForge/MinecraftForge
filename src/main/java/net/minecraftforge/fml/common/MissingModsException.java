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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public class MissingModsException extends EnhancedRuntimeException
{
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    /** @deprecated use {@link #getMissingModInfos()} */
    @Deprecated
    public final Set<ArtifactVersion> missingMods;
    private final List<MissingModInfo> missingModsInfos;
    private final String modName;

    public MissingModsException(String id, String name)
    {
        this(new HashSet<>(), id, name);
    }

    /**
     * @deprecated use {@link #MissingModsException(String, String)}
     */
    @Deprecated // TODO remove in 1.13
    public MissingModsException(Set<ArtifactVersion> missingMods, String id, String name)
    {
        this.id = id;
        this.name = name;
        this.missingMods = missingMods;
        this.missingModsInfos = new ArrayList<>();
        for (ArtifactVersion artifactVersion : missingMods)
        {
            missingModsInfos.add(new MissingModInfo(artifactVersion, null, true));
        }
        this.modName = name;
    }

    @Override
    public String getMessage()
    {
        Set<ArtifactVersion> missingMods = missingModsInfos.stream().map(MissingModInfo::getNeededVersion).collect(Collectors.toSet());
        return String.format("Mod %s (%s) requires %s", id, name, missingMods);
    }

    public void addMissingMod(ArtifactVersion needVersion, @Nullable ArtifactVersion haveVersion, boolean required)
    {
        MissingModInfo missingModInfo = new MissingModInfo(needVersion, haveVersion, required);
        this.missingModsInfos.add(missingModInfo);
        this.missingMods.add(needVersion);
    }

    public String getModName()
    {
        return modName;
    }

    public List<MissingModInfo> getMissingModInfos()
    {
        return Collections.unmodifiableList(this.missingModsInfos);
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        stream.println("Missing Mods:");
        for (MissingModInfo info : this.missingModsInfos)
        {
            ArtifactVersion needVersion = info.getNeededVersion();
            ArtifactVersion haveVersion = info.getHaveVersion();
            String haveString = haveVersion != null ? haveVersion.getVersionString() : "null";
            stream.println(String.format("\t%s : need %s: have %s", needVersion.getVersionString(), needVersion.getRangeString(), haveString));
        }
        stream.println("");
    }

    public static class MissingModInfo
    {
        private final ArtifactVersion neededVersion;
        @Nullable
        private final ArtifactVersion haveVersion;
        private final boolean required;

        private MissingModInfo(ArtifactVersion neededVersion, @Nullable ArtifactVersion haveVersion, boolean required)
        {
            Preconditions.checkNotNull(neededVersion, "neededVersion");
            this.neededVersion = neededVersion;
            this.haveVersion = haveVersion;
            this.required = required;
        }

        @Nullable
        public ArtifactVersion getHaveVersion()
        {
            return haveVersion;
        }

        public ArtifactVersion getNeededVersion()
        {
            return neededVersion;
        }

        public boolean isRequired()
        {
            return required;
        }
    }
}
