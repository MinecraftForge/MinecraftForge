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

package net.minecraftforge.fml.common;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.GuiModsMissing;
import net.minecraftforge.fml.client.IDisplayableError;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MissingModsException extends EnhancedRuntimeException implements IDisplayableError
{
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    /** @deprecated use {@link #getMissingModInfos()} */
    @Deprecated // TODO remove in 1.13
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
        Set<ArtifactVersion> missingMods = missingModsInfos.stream().map(MissingModInfo::getAcceptedVersion).collect(Collectors.toSet());
        return String.format("Mod %s (%s) requires %s", id, name, missingMods);
    }

    public void addMissingMod(ArtifactVersion acceptedVersion, @Nullable ArtifactVersion currentVersion, boolean required)
    {
        MissingModInfo missingModInfo = new MissingModInfo(acceptedVersion, currentVersion, required);
        this.missingModsInfos.add(missingModInfo);
        this.missingMods.add(acceptedVersion);
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
            ArtifactVersion acceptedVersion = info.getAcceptedVersion();
            ArtifactVersion currentVersion = info.getCurrentVersion();
            String currentString = currentVersion != null ? currentVersion.getVersionString() : "missing";
            stream.println(String.format("\t%s : need %s: have %s", acceptedVersion.getVersionString(), acceptedVersion.getRangeString(), currentString));
        }
        stream.println("");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui()
    {
        return new GuiModsMissing(this);
    }

    public static class MissingModInfo
    {
        private final ArtifactVersion acceptedVersion;
        @Nullable
        private final ArtifactVersion currentVersion;
        private final boolean required;

        private MissingModInfo(ArtifactVersion acceptedVersion, @Nullable ArtifactVersion currentVersion, boolean required)
        {
            Preconditions.checkNotNull(acceptedVersion, "acceptedVersion");
            this.acceptedVersion = acceptedVersion;
            this.currentVersion = currentVersion;
            this.required = required;
        }

        @Nullable
        public ArtifactVersion getCurrentVersion()
        {
            return currentVersion;
        }

        public ArtifactVersion getAcceptedVersion()
        {
            return acceptedVersion;
        }

        public boolean isRequired()
        {
            return required;
        }
    }

}
