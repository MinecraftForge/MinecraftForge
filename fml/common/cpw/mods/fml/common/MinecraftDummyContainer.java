package cpw.mods.fml.common;

import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;

public class MinecraftDummyContainer extends DummyModContainer
{

    private VersionRange staticRange;
    public MinecraftDummyContainer(String actualMCVersion)
    {
        super(new ModMetadata());
        getMetadata().modId = "Minecraft";
        getMetadata().name = "Minecraft";
        getMetadata().version = actualMCVersion;
        staticRange = VersionParser.parseRange("["+actualMCVersion+"]");
    }


    public VersionRange getStaticVersionRange()
    {
        return staticRange;
    }
}
