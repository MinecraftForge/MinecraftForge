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

package cpw.mods.fml.common;

import java.io.File;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;

public class InjectedModContainer implements ModContainer
{
    private File source;
    public final ModContainer wrappedContainer;

    public InjectedModContainer(ModContainer mc, File source)
    {
        this.source = source;
        this.wrappedContainer = mc;
    }

    public String getModId()
    {
        return wrappedContainer.getModId();
    }

    public String getName()
    {
        return wrappedContainer.getName();
    }

    public String getVersion()
    {
        return wrappedContainer.getVersion();
    }

    public File getSource()
    {
        return source;
    }

    public ModMetadata getMetadata()
    {
        return wrappedContainer.getMetadata();
    }

    public void bindMetadata(MetadataCollection mc)
    {
        wrappedContainer.bindMetadata(mc);
    }

    public void setEnabledState(boolean enabled)
    {
        wrappedContainer.setEnabledState(enabled);
    }

    public Set<ArtifactVersion> getRequirements()
    {
        return wrappedContainer.getRequirements();
    }

    public List<ArtifactVersion> getDependencies()
    {
        return wrappedContainer.getDependencies();
    }

    public List<ArtifactVersion> getDependants()
    {
        return wrappedContainer.getDependants();
    }

    public String getSortingRules()
    {
        return wrappedContainer.getSortingRules();
    }

    public boolean registerBus(EventBus bus, LoadController controller)
    {
        return wrappedContainer.registerBus(bus, controller);
    }

    public boolean matches(Object mod)
    {
        return wrappedContainer.matches(mod);
    }

    public Object getMod()
    {
        return wrappedContainer.getMod();
    }

    public ArtifactVersion getProcessedVersion()
    {
        return wrappedContainer.getProcessedVersion();
    }

    @Override
    public boolean isNetworkMod()
    {
        return wrappedContainer.isNetworkMod();
    }
    @Override
    public boolean isImmutable()
    {
        return true;
    }

    @Override
    public String getDisplayVersion()
    {
        return wrappedContainer.getDisplayVersion();
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange()
    {
        return wrappedContainer.acceptableMinecraftVersionRange();
    }

    public WorldAccessContainer getWrappedWorldAccessContainer()
    {
        if (wrappedContainer instanceof WorldAccessContainer)
        {
            return (WorldAccessContainer) wrappedContainer;
        }
        else
        {
            return null;
        }
    }

    @Override
    public Certificate getSigningCertificate()
    {
        return wrappedContainer.getSigningCertificate();
    }

    @Override
    public String toString()
    {
        return "Wrapped{"+wrappedContainer.toString()+"}";
    }

    @Override
    public Map<String, String> getCustomModProperties()
    {
        return wrappedContainer.getCustomModProperties();
    }

    @Override
    public Class<?> getCustomResourcePackClass()
    {
        return wrappedContainer.getCustomResourcePackClass();
    }
}
