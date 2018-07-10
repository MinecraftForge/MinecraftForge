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

import java.io.File;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

import javax.annotation.Nullable;

public class DummyModContainer implements ModContainer
{
    private ModMetadata md;
    private ArtifactVersion processedVersion;
    private String label;
    private int classVersion;

    public DummyModContainer(ModMetadata md)
    {
        this.md = md;
    }

    public DummyModContainer(String label)
    {
        this.label = label;
    }
    public DummyModContainer()
    {
    }

    @Override
    public void bindMetadata(MetadataCollection mc)
    {
    }

    @Override
    public List<ArtifactVersion> getDependants()
    {
        return Collections.emptyList();
    }

    @Override
    public List<ArtifactVersion> getDependencies()
    {
        return Collections.emptyList();
    }

    @Override
    public Set<ArtifactVersion> getRequirements()
    {
        return Collections.emptySet();
    }

    @Override
    public ModMetadata getMetadata()
    {
        return md;
    }

    @Override
    public Object getMod()
    {
        return null;
    }

    @Override
    public String getModId()
    {
        return md.modId;
    }

    @Override
    public String getName()
    {
        return md.name;
    }

    @Override
    public String getSortingRules()
    {
        return "";
    }

    @Override
    public File getSource()
    {
        return null;
    }

    @Override
    public String getVersion()
    {
        return md.version;
    }

    @Override
    public boolean matches(Object mod)
    {
        return false;
    }

    @Override
    public void setEnabledState(boolean enabled)
    {
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        return false;
    }

    @Override
    public ArtifactVersion getProcessedVersion()
    {
        if (processedVersion == null)
        {
            processedVersion = new DefaultArtifactVersion(getModId(), getVersion());
        }
        return processedVersion;
    }

    @Override
    public boolean isImmutable()
    {
        return false;
    }

    @Override
    public String getDisplayVersion()
    {
        return md.version;
    }
    @Override
    public VersionRange acceptableMinecraftVersionRange()
    {
        return Loader.instance().getMinecraftModContainer().getStaticVersionRange();
    }

    @Override
    @Nullable
    public Certificate getSigningCertificate()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return md != null ? getModId() : "Dummy Container ("+label+") @" + System.identityHashCode(this);
    }

    @Override
    public Map<String, String> getCustomModProperties()
    {
        return EMPTY_PROPERTIES;
    }
    @Override
    public Class<?> getCustomResourcePackClass()
    {
        return null;
    }

    @Override
    public Map<String, String> getSharedModDescriptor()
    {
        return null;
    }

    @Override
    public Disableable canBeDisabled()
    {
        return Disableable.NEVER;
    }

    @Override
    public String getGuiClassName()
    {
        return null;
    }

    @Override
    public List<String> getOwnedPackages()
    {
        return ImmutableList.of();
    }

    @Override
    public boolean shouldLoadInEnvironment()
    {
        return true;
    }

    @Override
    public URL getUpdateUrl()
    {
        return null;
    }

    @Override
    public void setClassVersion(int classVersion)
    {
        this.classVersion = classVersion;
    }

    @Override
    public int getClassVersion()
    {
        return this.classVersion;
    }
}
