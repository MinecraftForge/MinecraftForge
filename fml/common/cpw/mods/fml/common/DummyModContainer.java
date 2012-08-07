package cpw.mods.fml.common;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.discovery.ContainerType;
import cpw.mods.fml.common.versioning.ArtifactVersion;

public class DummyModContainer implements ModContainer
{
    private ModMetadata md;

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
    public List<ArtifactVersion> getRequirements()
    {
        return Collections.emptyList();
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
        return null;
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
}
