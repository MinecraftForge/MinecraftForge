package net.minecraftforge.common.data;

import joptsimple.ValueConverter;
import net.minecraft.resources.ResourceLocation;
import org.intellij.lang.annotations.Language;

public class ResourceLocationConverter implements ValueConverter<ResourceLocation>
{
    @Override
    public ResourceLocation convert(String value)
    {
        return new ResourceLocation(value);
    }

    @Override
    public Class<? extends ResourceLocation> valueType()
    {
        return ResourceLocation.class;
    }

    @Override
    public String valuePattern()
    {
        // language=RegExp
        return "^[a-z0-9_.-]+:[a-z0-9/._-]+$";
    }
}
