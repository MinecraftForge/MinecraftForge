package cpw.mods.fml.common.functions;

import com.google.common.base.Function;

import cpw.mods.fml.common.ModContainer;

public final class ModIdFunction implements Function<ModContainer, String>
{
    public String apply(ModContainer container)
    {
        return container.getModId();
    }
}