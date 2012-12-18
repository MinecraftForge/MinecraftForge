package cpw.mods.fml.common;

import java.io.File;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;

public class DummyModContainer implements ModContainer
{
    private ModMetadata md;
    private ArtifactVersion processedVersion;

    public DummyModContainer(ModMetadata md)
    {
        this.md = md;
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
    public boolean isNetworkMod()
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
}
