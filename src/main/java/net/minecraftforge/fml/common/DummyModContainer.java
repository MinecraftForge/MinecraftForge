/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
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

public class DummyModContainer implements ModContainer
{
    private ModMetadata md;
    private ArtifactVersion processedVersion;
    private String label;

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
}
