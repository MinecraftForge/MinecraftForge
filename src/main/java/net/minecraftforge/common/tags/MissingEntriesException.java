package net.minecraftforge.common.tags;

import net.minecraft.util.ResourceLocation;

import java.util.Set;

public class MissingEntriesException extends TagException {
    private final Set<ResourceLocation> missingEntries;
    public MissingEntriesException(Set<ResourceLocation> missingEntries)
    {
        super("Failed to load Tag, because Registry did not contain required entries");
        this.missingEntries = missingEntries;
    }

    public MissingEntriesException(Set<ResourceLocation> missingEntries, Throwable cause)
    {
        super("Failed to load Tag, because Registry did not contain required entries", cause);
        this.missingEntries = missingEntries;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        stream.println(" ");
        stream.println("###################################");
        stream.println("Missing "+getMissingEntries().size()+ (getMissingEntries().size() == 1?" Entry":" Entries"));
        for (ResourceLocation entry: getMissingEntries())
        {
            stream.println(" - Could not resolve "+entry);

        }
        stream.println("###################################");
    }

    public Set<ResourceLocation> getMissingEntries()
    {
        return missingEntries;
    }
}
