package net.minecraftforge.fml.common.functions;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import com.google.common.base.Function;

public class ArtifactVersionNameFunction implements Function<ArtifactVersion, String> {
    @Override
    public String apply(ArtifactVersion v)
    {
        return v.getLabel();
    }
}
