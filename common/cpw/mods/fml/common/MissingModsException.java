package cpw.mods.fml.common;

import java.util.Set;

import com.google.common.collect.Sets.SetView;

import cpw.mods.fml.common.versioning.ArtifactVersion;

public class MissingModsException extends RuntimeException
{

    public Set<ArtifactVersion> missingMods;

    public MissingModsException(Set<ArtifactVersion> missingMods)
    {
        this.missingMods = missingMods;
    }
}
