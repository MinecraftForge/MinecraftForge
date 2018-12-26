package net.minecraftforge.common.tags;

import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Set;

public class MissingTagsException extends TagException
{
    private final String regName;
    private final Map<ResourceLocation, Set<ResourceLocation>> failedTags;

    public MissingTagsException(String regName, Map<ResourceLocation, Set<ResourceLocation>> failedTags)
    {
        super("Failed to build Tags because of unresolved References");
        this.failedTags = failedTags;
        this.regName = regName;
    }

    public MissingTagsException(String regName, Map<ResourceLocation, Set<ResourceLocation>> failedTags, Throwable cause)
    {
        super("Failed to build Tags because of unresolved References", cause);
        this.failedTags = failedTags;
        this.regName = regName;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        stream.println(" ");
        stream.println("###################################");
        Set<Map.Entry<ResourceLocation, Set<ResourceLocation>>> entries = failedTags.entrySet();
        stream.println("Failed to build " + entries.size() + getTagType() + " " + (entries.size() == 1 ? " Tag" : " Tags"));
        for (Map.Entry<ResourceLocation, Set<ResourceLocation>> entry : entries)
        {
            stream.println(entry.getKey() + " failed. It is missing " + entry.getValue().size() + (entry.getValue().size() == 1 ? " Tag:" : " Tags:"));
            for (ResourceLocation id : entry.getValue())
            {
                stream.println(" - " + id);
            }
        }
        stream.println("###################################");
    }

    public Map<ResourceLocation, Set<ResourceLocation>> getFailedTags()
    {
        return failedTags;
    }

    public String getTagType()
    {
        return regName;
    }
}
