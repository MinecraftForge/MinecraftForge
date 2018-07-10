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
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;

import com.google.common.eventbus.EventBus;

import javax.annotation.Nullable;

public class InjectedModContainer implements ModContainer
{
    private File source;
    public final ModContainer wrappedContainer;

    public InjectedModContainer(ModContainer mc, File source)
    {
        this.source = source != null ? source : new File("minecraft.jar");
        this.wrappedContainer = mc;
    }

    @Override
    public String getModId()
    {
        return wrappedContainer.getModId();
    }

    @Override
    public String getName()
    {
        return wrappedContainer.getName();
    }

    @Override
    public String getVersion()
    {
        return wrappedContainer.getVersion();
    }

    @Override
    public File getSource()
    {
        return source;
    }

    @Override
    public ModMetadata getMetadata()
    {
        return wrappedContainer.getMetadata();
    }

    @Override
    public void bindMetadata(MetadataCollection mc)
    {
        wrappedContainer.bindMetadata(mc);
    }

    @Override
    public void setEnabledState(boolean enabled)
    {
        wrappedContainer.setEnabledState(enabled);
    }

    @Override
    public Set<ArtifactVersion> getRequirements()
    {
        return wrappedContainer.getRequirements();
    }

    @Override
    public List<ArtifactVersion> getDependencies()
    {
        return wrappedContainer.getDependencies();
    }

    @Override
    public List<ArtifactVersion> getDependants()
    {
        return wrappedContainer.getDependants();
    }

    @Override
    public String getSortingRules()
    {
        return wrappedContainer.getSortingRules();
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        return wrappedContainer.registerBus(bus, controller);
    }

    @Override
    public boolean matches(Object mod)
    {
        return wrappedContainer.matches(mod);
    }

    @Override
    public Object getMod()
    {
        return wrappedContainer.getMod();
    }

    @Override
    public ArtifactVersion getProcessedVersion()
    {
        return wrappedContainer.getProcessedVersion();
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

    @Nullable
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
    @Nullable
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

    @Override
    public Map<String, String> getSharedModDescriptor()
    {
        return wrappedContainer.getSharedModDescriptor();
    }

    @Override
    public Disableable canBeDisabled()
    {
        return wrappedContainer.canBeDisabled();
    }

    @Override
    public String getGuiClassName()
    {
        return wrappedContainer.getGuiClassName();
    }

    @Override
    public List<String> getOwnedPackages()
    {
        return wrappedContainer.getOwnedPackages();
    }

    @Override
    public boolean shouldLoadInEnvironment()
    {
        return true;
    }

    @Override
    public URL getUpdateUrl()
    {
        return wrappedContainer.getUpdateUrl();
    }

    @Override
    public void setClassVersion(int classVersion)
    {
        wrappedContainer.setClassVersion(classVersion);
    }

    @Override
    public int getClassVersion()
    {
        return wrappedContainer.getClassVersion();
    }
}
