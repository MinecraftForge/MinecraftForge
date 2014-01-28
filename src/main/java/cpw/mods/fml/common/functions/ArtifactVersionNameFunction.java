package cpw.mods.fml.common.functions;

import com.google.common.base.Function;

import cpw.mods.fml.common.versioning.ArtifactVersion;

public class ArtifactVersionNameFunction implements Function<ArtifactVersion, String> {
    @Override
    public String apply(ArtifactVersion v)
    {
        return v.getLabel();
    }
}