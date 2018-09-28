package net.minecraftforge.common.tags;

import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Set;

public class MissingTagsException extends TagException {
    private final ResourceLocation regId;
    private final Map<ResourceLocation, Set<ResourceLocation>> failedTags;
    public MissingTagsException(ResourceLocation regId, Map<ResourceLocation, Set<ResourceLocation>> failedTags)
    {
        super("Failed to build Tags because of unresolved References");
        this.failedTags = failedTags;
        this.regId = regId;
    }

    public MissingTagsException(ResourceLocation regId, Map<ResourceLocation, Set<ResourceLocation>> failedTags, Throwable cause)
    {
        super("Failed to build Tags because of unresolved References", cause);
        this.failedTags = failedTags;
        this.regId = regId;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        stream.println(" ");
        stream.println("###################################");
        Set<Map.Entry<ResourceLocation, Set<ResourceLocation>>> entries = failedTags.entrySet();
        stream.println("Failed to build "+entries.size()+ (entries.size() == 1?" Tag":" Tags")+" from Registry "+ getRegistryId()+":");
        for (Map.Entry<ResourceLocation, Set<ResourceLocation>> entry: entries)
        {
            stream.println(entry.getKey() + " failed. It is missing "+entry.getValue().size()+(entry.getValue().size() == 1?" Tag:":" Tags:"));
            for (ResourceLocation id: entry.getValue())
            {
                stream.println(" - "+id);
            }
        }
        stream.println("###################################");
    }

    public Map<ResourceLocation, Set<ResourceLocation>> getFailedTags()
    {
        return failedTags;
    }

    public ResourceLocation getRegistryId()
    {
        return regId;
    }
}
