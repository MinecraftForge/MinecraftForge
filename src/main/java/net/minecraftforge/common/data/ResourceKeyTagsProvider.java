package net.minecraftforge.common.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ResourceKeyTags;

/**
 * The existing tag providers only work with tags for static registry objects,
 * so we need this parallel implementation for key tags
 **/
public abstract class ResourceKeyTagsProvider<T> implements IDataProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final DataGenerator generator;
    protected final Map<ResourceLocation,ITag.Builder> tagBuilders = new HashMap<>();
    protected final ExistingFileHelper existingFileHelper;
    protected final RegistryKey<Registry<T>> registryKey;
    protected final String sourceModid;
    protected final ExistingFileHelper.IResourceType resourceType;
    
    /**
     * @param generator Data generator from GatherDataEvent
     * @param existingFileHelper file helper from GatherDataEvent
     * @param sourceModid Your modid (used for logging, not tag namespaces)
     * @param registryKey The registry key to generate resource key tags for (registry keys for mods' custom directories must be registered to {@link ResourceKeyTags})
     */
    public ResourceKeyTagsProvider(final DataGenerator generator, final ExistingFileHelper existingFileHelper, final String sourceModid, RegistryKey<Registry<T>> registryKey)
    {
        final String directory = ResourceKeyTags.getTagDirectory(registryKey);
        if (directory == null)
            throw new IllegalArgumentException(String.format("No resource key tag directory registered to registry key %s (be sure to register custom directories)", registryKey.location()));
        this.generator = generator;
        this.existingFileHelper = existingFileHelper;
        this.sourceModid = sourceModid;
        this.registryKey = registryKey;
        this.resourceType = new ExistingFileHelper.ResourceType(ResourcePackType.SERVER_DATA, ".json", "tags/resource_keys/"+directory);
    }
    
    public abstract void addTags();
    
    public ResourceKeyTagsProvider.Builder<T> tag(ITag.INamedTag<RegistryKey<T>> tag)
    {
        ITag.Builder builder = this.tagBuilders.computeIfAbsent(tag.getName(), tagID ->
        {
            existingFileHelper.trackGenerated(tagID, this.resourceType);
            return new ITag.Builder();
        });
        return new ResourceKeyTagsProvider.Builder<>(builder, registryKey, this.sourceModid);
    }

    @Override
    public void run(DirectoryCache cache) throws IOException
    {
        this.tagBuilders.clear();
        this.addTags();
        ITag<RegistryKey<T>> tag = Tag.empty();
        Function<ResourceLocation, ITag<RegistryKey<T>>> tagFromID = id -> this.tagBuilders.containsKey(id) ? tag : null;
        Function<ResourceLocation, RegistryKey<T>> resourceKeyFromID = RegistryKey.elementKey(this.registryKey);
        this.tagBuilders.forEach((id,builder) ->{
           List<ITag.Proxy> missingReferences = builder.getUnresolvedEntries(tagFromID, resourceKeyFromID)
               .filter(this::missing)
               .collect(Collectors.toList());
           if (!missingReferences.isEmpty())
           {
               throw new IllegalArgumentException(String.format("%s resource key tag generator couldn't define tag %s as it is missing following references: %s",
                   this.registryKey.location(),
                   id,
                   missingReferences.stream().map(Objects::toString).collect(Collectors.joining(","))));
           }
           JsonObject tagJson = builder.serializeToJson();
           Path path = this.generator.getOutputFolder().resolve("data/"+id.getNamespace()+"/"+this.resourceType.getPrefix()+"/"+id.getPath()+this.resourceType.getSuffix());
           if (path == null)
               return; // run the data provider without writing this tag so other providers can still refer to tags
           try
           {
               String jsonAsString = GSON.toJson(tagJson);
               String hash = IDataProvider.SHA1.hashUnencodedChars(jsonAsString).toString();
               if(!hash.equals(cache.getHash(path)) || !Files.exists(path))
               {
                   Files.createDirectories(path.getParent());
                   try (BufferedWriter writer = Files.newBufferedWriter(path))
                   {
                       writer.write(jsonAsString);
                   }
               }
               cache.putNew(path, hash);
           }
           catch(IOException e)
           {
               LOGGER.error("{} resource key tag provider couldn't save tags to {}", this.registryKey.location(), path, e);
           }
        });
    }
    
    private boolean missing(ITag.Proxy reference)
    {
        ITag.ITagEntry entry = reference.getEntry();
        if (entry instanceof ITag.TagEntry) // only non-optional tag entries need to be validated
        {
            return !this.existingFileHelper.exists(((ITag.TagEntry)entry).getId(), this.resourceType);
        }
        return false;
    }

    @Override
    public String getName()
    {
        return this.sourceModid + " Resource Key Tags";
    }
    
    /**
     * Wrapper around raw tag builders so that resource keys and tags thereof can be added directly.
     * {@link net.minecraftforge.common.extensions.IForgeTagBuilder} is no good here either as its
     * default methods don't work with a tag-of-resource-keys
     * TODO add support for forge's "remove" list (needs a patch or extension of the raw tag builder)
     */
    public static class Builder<T>
    {
        private final ITag.Builder tagBuilder;
        private final String sourceModid;
        
        protected Builder(ITag.Builder tagBuilder, RegistryKey<Registry<T>> registryKey, String sourceModid)
        {
            this.tagBuilder = tagBuilder;
            this.sourceModid = sourceModid;
        }
        
        /**
         * Add one or more resource key entries to the tag builder
         * @param entries The resource keys to add
         * @return this
         */
        @SafeVarargs
        public final Builder<T> add(RegistryKey<T>... entries)
        {
            for (RegistryKey<T> entry : entries)
            {
                this.tagBuilder.addElement(entry.location(), sourceModid);
            }
            return this;
        }
        
        /**
         * Add one or more required tag entries to the tag builder
         * @param tagEntries tag entries
         * @return this
         */
        @SafeVarargs
        public final Builder<T> addTags(ITag.INamedTag<RegistryKey<T>>... tagEntries)
        {
            for (ITag.INamedTag<RegistryKey<T>> tagEntry : tagEntries)
            {
                this.tagBuilder.addTag(tagEntry.getName(), sourceModid);
            }
            return this;
        }
        
        /**
         * Add one or more optional tag entries to the tag builder.
         * Specified tags will not have to exist as jsons,
         * an optional tag will be used if it exists and not used if it doesn't.
         * e.g. use this to include another mod's tag in your tag without it being a hard dependency
         * (resource key tags don't need optional non-tag entries because individual resource keys are always able to exist)
         * @param location ID of the optional tag entry
         * @return this
         */
        public Builder<T> addOptionalTags(final ResourceLocation... locations)
        {
            for (ResourceLocation location : locations)
            {
                this.add(new ITag.OptionalTagEntry(location));
            }
            return this;
        }
        
        public Builder<T> add(ITag.ITagEntry tag)
        {
            this.tagBuilder.add(tag, this.sourceModid);
            return this;
        }
        
        /**
         * Specify that lower-priority datapack's tag jsons with the same ID should be ignored instead of being merged into your tag json.
         * (the default is replace=false, indicating that the tags should be merged -- invoke this to set replace=true)
         * @return this
         */
        public Builder<T> replace() {
            return replace(true);
        }

        public Builder<T> replace(boolean value) {
            this.getInternalBuilder().replace(value);
            return this;
        }
        
        public ITag.Builder getInternalBuilder()
        {
            return this.tagBuilder;
        }
        
        /** Gets the modid that created this tag (used for logging -- not necessarily the same as the tag's namespace) **/
        public String getSourceModID()
        {
            return this.sourceModid;
        }
    }

}
