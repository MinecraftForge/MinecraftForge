package net.minecraftforge.common.tags;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.EnhancedRuntimeException;

import java.util.Map;
import java.util.Set;

public class TagBuildingException extends EnhancedRuntimeException {
    private final Map<ResourceLocation, Set<ResourceLocation>> failedTags;
    public TagBuildingException(Map<ResourceLocation, Set<ResourceLocation>> failedTags)
    {
        super("Failed to build tags because of unresolved References");
        this.failedTags = failedTags;
    }

    public TagBuildingException(Map<ResourceLocation, Set<ResourceLocation>> failedTags, Throwable cause)
    {
        super("Failed to build tags because of unresolved References", cause);
        this.failedTags = failedTags;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        stream.println("###################################");
        Set<Map.Entry<ResourceLocation, Set<ResourceLocation>>> entries = failedTags.entrySet();
        stream.println("Failed to build "+entries.size()+ (entries.size() == 1?" Tag:":" Tags:"));
        stream.println(" ");
        for (Map.Entry<ResourceLocation, Set<ResourceLocation>> entry: entries)
        {
            stream.println(entry.getKey() + " failed. It is missing"+entry.getValue().size()+(entry.getValue().size() == 1?" Tag:":" Tags:"));
            for (ResourceLocation id: entry.getValue())
            {
                stream.println(" - "+id);
            }
        }
        stream.println("###################################");
    }
}
