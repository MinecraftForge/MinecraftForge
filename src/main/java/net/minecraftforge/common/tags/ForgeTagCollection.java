package net.minecraftforge.common.tags;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.TagProvider;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ForgeTagCollection<T extends IForgeRegistryEntry<T>> extends NetworkTagCollection<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final int JSON_EXTENSION_LENGTH = ".json".length();

    private final ResourceLocation regName;

    private ForgeTagCollection(Predicate<ResourceLocation> isValueKnownPredicateIn, Function<ResourceLocation, T> resourceLocationToItemIn, String resourceLocationPrefixIn, boolean preserveOrderIn, ResourceLocation registryId)
    {
        super(isValueKnownPredicateIn, resourceLocationToItemIn, resourceLocationPrefixIn, preserveOrderIn, registryId.toString());
        regName = registryId;
    }

    public ForgeTagCollection(Predicate<ResourceLocation> isValueKnownPredicateIn, Function<ResourceLocation, T> resourceLocationToItemIn, String resourceLocationPrefixIn, ResourceLocation registryId)
    {
        this(isValueKnownPredicateIn, resourceLocationToItemIn, resourceLocationPrefixIn, true, registryId);
    }

    @Override
    public void reload(IResourceManager resourceManager)
    {
        Map<ResourceLocation, Tag.Builder<T>> map = deserializeTags(getLoadingParameters(resourceManager), resourceManager);
        collectToMinecraftTags(map);
        registerUnbuildTags(map);
    }

    @Override
    public void write(PacketBuffer buf)
    {
        throw new AssertionError("Network write access to the ForgeTagCollection should be routed through the TagProvider! Failed to write to PacketBuffer!");
    }

    @Override
    public void read(PacketBuffer buf)
    {
        throw new AssertionError("Network write access to the ForgeTagCollection should be routed through the TagProvider! Failed to read from PacketBuffer!");
    }

    public List<Tag<T>> getOwningTagObjects(final T thing)
    {
        //with possibly large amounts of forge tags, consider a reverse Mapping of T->List<Tag<T>>? (Multimap)
        //especially if they might be used to control gameplay related stuff?
        return getMatchingTags(tag -> tag.contains(thing));
    }

    public List<Tag<T>> getMatchingTags(Predicate<Tag<T>> predicate)
    {
        return getTagMap().values().stream().filter(predicate).collect(Collectors.toList());
    }

    public boolean tagMatch(Predicate<Tag<T>> tagPredicate)
    {
        return getTagMap().values().stream().anyMatch(tagPredicate);
    }

    private ImmutableList<String> getLocationPrefixes()
    {
        String regPath = regName.getNamespace() + '/' + regName.getPath();
        return regName.getNamespace().equals("minecraft") ? ImmutableList.of(TagProvider.TAG_FOLDER + regName.getPath(), TagProvider.TAG_FOLDER + regPath, TagProvider.TAG_FOLDER + TagProvider.FORGE_TAG_SUBFOLDER + regPath) : ImmutableList.of(TagProvider.TAG_FOLDER + TagProvider.FORGE_TAG_SUBFOLDER + regPath);
    }

    private Stream<TagLoadingParameter> getLoadingParameters(IResourceManager resourceManager)
    {
        ImmutableList<String> prefixes = getLocationPrefixes();
        Stream<TagLoadingParameter> resources = Stream.of();
        for (String s : prefixes)
        {
            resources = Stream.concat(resources, resourceManager.getAllResourceLocations(s, s1 -> s1.endsWith(".json")).stream().map(location -> new TagLoadingParameter(s, location)));
        }
        return resources;
    }

    private Map<ResourceLocation, Tag.Builder<T>> deserializeTags(Stream<TagLoadingParameter> stream, IResourceManager resourceManager)
    {
        Map<ResourceLocation, Tag.Builder<T>> map = Maps.newHashMap();
        stream.forEach(tagLoadingParameter ->
        {
            try
            {
                for (IResource iresource : resourceManager.getAllResources(tagLoadingParameter.getLocation()))
                {
                    try
                    {
                        JsonObject jsonobject = (JsonObject) JsonUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                        if (jsonobject == null)
                        {
                            LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.regName, tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), iresource.getPackName());
                        } else
                        {
                            Tag.Builder<T> builder = map.getOrDefault(tagLoadingParameter.getTagId(), Tag.Builder.create());
                            builder.deserialize(this.isValueKnownPredicate, this.resourceLocationToItem, jsonobject);
                            map.put(tagLoadingParameter.getTagId(), builder);
                        }
                    } catch (RuntimeException | IOException ioexception)
                    {
                        LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.regName, tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), iresource.getPackName(), ioexception);
                    } finally
                    {
                        IOUtils.closeQuietly((Closeable) iresource);
                    }
                }
            } catch (IOException ioexception1)
            {
                LOGGER.error("Couldn't read {} tag list {} from {}", this.regName, tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), ioexception1);
            }
        });
        return map;
    }

    private void collectToMinecraftTags(Map<ResourceLocation,Tag.Builder<T>> map) {
        Map<String, Set<ResourceLocation>> flattener = Maps.newHashMap();
        for (Map.Entry<ResourceLocation,Tag.Builder<T>> unbuildTag: map.entrySet())
        {
            if (!unbuildTag.getKey().getNamespace().equals("minecraft"))
            {
                Set<ResourceLocation> current = flattener.getOrDefault(unbuildTag.getKey().getPath(),new HashSet<>());
                current.add(unbuildTag.getKey());
                flattener.put(unbuildTag.getKey().getPath(), current);
            }
        }
        for (Map.Entry<String,Set<ResourceLocation>> flattenedEntry:flattener.entrySet())
        {
            ResourceLocation collected = new ResourceLocation("minecraft:"+flattenedEntry.getKey());
            Tag.Builder<T> builder = map.get(collected); //ensure that we don't delete existing minecraft tags
            map.put(collected, TagHelper.addAll(builder!=null?builder:Tag.Builder.create(),flattenedEntry.getValue()));
        }
    }

    private void registerUnbuildTags(Map<ResourceLocation, Tag.Builder<T>> map)
    {
        while (!map.isEmpty())
        {
            boolean flag = false;
            Iterator<Map.Entry<ResourceLocation, Tag.Builder<T>>> iterator = map.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<ResourceLocation, Tag.Builder<T>> entry1 = iterator.next();
                if ((entry1.getValue()).resolve(this::get))
                {
                    flag = true;
                    this.register((entry1.getValue()).build(entry1.getKey()));
                    iterator.remove();
                }
            }

            if (!flag)
            {
                for (Map.Entry<ResourceLocation, Tag.Builder<T>> entry2 : map.entrySet())
                {
                    LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.regName, entry2.getKey());
                }
                break;
            }
        }
        //I have no idea why vanilla does this - it's the only reason preserveOrder is protected
        //if everything is well, the tag-list is empty, if not then some tag 'either references another tag that doesn't exist, or ultimately references itself'
        for (Map.Entry<ResourceLocation, Tag.Builder<T>> entry : map.entrySet())
        {  //so why register tags that are invalid?!?
            this.register((entry.getValue()).ordered(this.preserveOrder).build(entry.getKey()));
        }
    }

    private static class TagLoadingParameter { //value class, so that we don't need to use Triples...
        private final String prefix;
        private final ResourceLocation location;
        private final ResourceLocation tagId;

        public TagLoadingParameter(String prefix, ResourceLocation location)
        {
            this.prefix = prefix;
            this.location = location;
            String path = location.getPath();
            //this creates a ResourceLocation which is stripped of the prefixPath
            tagId = new ResourceLocation(location.getNamespace(), path.substring(prefix.length() + 1, path.length() - JSON_EXTENSION_LENGTH));
        }

        public String getPrefix()
        {
            return prefix;
        }

        public ResourceLocation getLocation()
        {
            return location;
        }

        public ResourceLocation getTagId()
        {
            return tagId;
        }
    }
}
