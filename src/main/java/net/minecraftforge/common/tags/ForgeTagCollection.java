package net.minecraftforge.common.tags;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ForgeTagCollection<T extends IForgeRegistryEntry<T>> extends NetworkTagCollection<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final int JSON_EXTENSION_LENGTH = ".json".length();

    private final ResourceLocation regName;
    //with possibly large amounts of Tags iterating over these all the time will be costly
    //therefore cache them, so that using these every tick, doesn't hurt so much
    private Map<ResourceLocation, SortedSet<Tag<T>>> owningTags;
    private BiFunction<ResourceLocation, ResourceLocation, ? extends Tag<T>> wrapperFactory;

    private ForgeTagCollection(BiFunction<ResourceLocation, IForgeRegistry<T>, ? extends Tag<T>> wrapperFactory, String resourceLocationPrefixIn, boolean preserveOrderIn, ResourceLocation registryId)
    {
        super(null, null, resourceLocationPrefixIn, preserveOrderIn, registryId.toString());
        regName = Objects.requireNonNull(registryId);
        setWrapperFactory(wrapperFactory);
        owningTags = new HashMap<>();
    }

    public ForgeTagCollection(BiFunction<ResourceLocation, IForgeRegistry<T>, ? extends Tag<T>> wrapperFactory, String resourceLocationPrefixIn, ResourceLocation registryId)
    {
        this(wrapperFactory, resourceLocationPrefixIn, true, registryId);
    }

    private void setWrapperFactory(BiFunction<ResourceLocation, IForgeRegistry<T>, ? extends Tag<T>> wrapperFactory)
    {
        this.wrapperFactory = (BiFunction<ResourceLocation, ResourceLocation, Tag<T>>) (location, location2) -> wrapperFactory.apply(location, TagHelper.<T>lazyRegistrySupplier(location2).get());
    }

    @Override
    public void reload(IResourceManager resourceManager)
    {
        LOGGER.info("Loading Tags for Registry {}.",regName);
        Map<ResourceLocation, UnbakedTag<T>> map = deserializeTags(getLoadingParameters(resourceManager), resourceManager);
        collectToMinecraftTags(map);
        registerUnbakedTags(map);
        updateOwningTags();
        LOGGER.info("Finished loading {} Tags for Registry {}.",getTagMap().size(),regName);
    }

    @Override
    public SortedSet<ResourceLocation> getOwningTags(T itemIn)
    {
        if (itemIn.getRegistryName() == null) return Collections.emptySortedSet();
        return getOwningTags(itemIn.getRegistryName());
    }

    public SortedSet<ResourceLocation> getOwningTags(ResourceLocation id)
    {
        return owningTags.getOrDefault(id, Collections.emptySortedSet()).stream().map(Tag::getId).collect(Collectors.toCollection(TreeSet::new));
    }

    public SortedSet<Tag<T>> getOwningTagObjects(ResourceLocation id)
    {
        return Collections.unmodifiableSortedSet(owningTags.getOrDefault(id, Collections.emptySortedSet()));
    }

    public SortedSet<Tag<T>> getMatchingTagsForItem(ResourceLocation id, Predicate<Tag<T>> predicate)
    {
        return getOwningTagObjects(id).stream().filter(predicate).collect(Collectors.toCollection(TreeSet::new));
    }

    public boolean tagMatchForItem(ResourceLocation id, Predicate<Tag<T>> tagPredicate)
    {
        return getOwningTagObjects(id).stream().anyMatch(tagPredicate);
    }

    public List<Tag<T>> getMatchingTags(Predicate<Tag<T>> predicate)
    {
        return getTagMap().values().stream().filter(predicate).map((tag) -> new UnmodifiableTagWrapper<>(tag, regName)).collect(Collectors.toList());
    }

    public boolean tagMatch(Predicate<Tag<T>> tagPredicate)
    {
        return getTagMap().values().stream().anyMatch(tagPredicate);
    }

    @Override
    public void write(PacketBuffer buf)
    {
        ForgeRegistry<T> reg = Objects.requireNonNull(RegistryManager.ACTIVE.getRegistry(Objects.requireNonNull(regName, "Cannot write without Registry data, which requires an identifier")), "Cannot write without Registry data.");

        buf.writeVarInt(getTagMap().size());

        for (Map.Entry<ResourceLocation, Tag<T>> entry : this.getTagMap().entrySet())
        {
            buf.writeResourceLocation(entry.getKey());
            buf.writeVarInt(((Tag) entry.getValue()).getAllElements().size());

            for (T t : (entry.getValue()).getAllElements())
            {
                buf.writeVarInt(reg.getID(t));
            }
        }
    }

    @Override
    public void read(PacketBuffer buf)
    {
        ForgeRegistry<T> reg = Objects.requireNonNull(RegistryManager.ACTIVE.getRegistry(Objects.requireNonNull(regName, "Cannot write without Registry data, which requires an identifier")), "Cannot write without Registry data.");

        clear();
        int i = buf.readVarInt();

        for (int j = 0; j < i; ++j)
        {
            ResourceLocation resourcelocation = buf.readResourceLocation();
            int k = buf.readVarInt();
            List<T> list = Lists.newArrayList();

            for (int l = 0; l < k; ++l)
            {
                list.add(reg.getValue(buf.readVarInt()));
            }

            register(ImmutableTag.<T>builder().addAll(list).build(resourcelocation));
        }

        updateOwningTags();
    }

    @Override
    public Map<ResourceLocation, Tag<T>> getTagMap()
    {
        return Collections.unmodifiableMap(super.getTagMap());
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

    private Map<ResourceLocation, UnbakedTag<T>> deserializeTags(Stream<TagLoadingParameter> stream, IResourceManager resourceManager)
    {
        LOGGER.debug("Parsing {} Tag Data.",regName);
        Map<ResourceLocation, UnbakedTag<T>> map = Maps.newHashMap();
        ForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(regName);
        final Predicate<ResourceLocation> isValueKnownPredicate = registry::containsKey;
        final Function<ResourceLocation, T> resourceLocationToItem = registry::getValue;
        stream.forEach(tagLoadingParameter ->
        {
            try
            {
                for (IResource iresource : resourceManager.getAllResources(tagLoadingParameter.getLocation()))
                {
                    try
                    {
                        JsonObject jsonobject = JsonUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                        if (jsonobject == null)
                        {
                            LOGGER.error("Couldn't load {} Tag list {} from {} in data pack {} as it's empty or null", this.regName.toString(), tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), iresource.getPackName());
                        } else
                        {
                            UnbakedTag<T> builder = map.getOrDefault(tagLoadingParameter.getTagId(), new UnbakedTag<>(tagLoadingParameter));
                            builder.deserialize(isValueKnownPredicate, resourceLocationToItem, jsonobject);
                            map.put(tagLoadingParameter.getTagId(), builder);
                        }
                    } catch (MissingEntriesException e)
                    {
                        printMissingEntriesError(tagLoadingParameter,e);
                    } catch (RuntimeException | IOException ioexception)
                    {
                        LOGGER.error("Couldn't read {} Tag {} from {} in Data Pack {}", this.regName.toString(), tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), iresource.getPackName(), ioexception);
                    } finally
                    {
                        IOUtils.closeQuietly((Closeable) iresource);
                    }
                }
            } catch (IOException ioexception1)
            {
                LOGGER.error("Couldn't read {} Tag {} from {}", this.regName.toString(), tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), ioexception1);
            }
        });
        return map;
    }

    private void printMissingEntriesError(TagLoadingParameter tagLoadingParameter, MissingEntriesException e)
    {
        LOGGER.error("Failed to parse tag {} because the Registry didn't contain required Entries.", tagLoadingParameter.getTagId());
        Set<ResourceLocation> missingEntries = e.getMissingEntries();
        LOGGER.warn("###################################");
        LOGGER.warn("Missing {} {}",missingEntries.size(),missingEntries.size() == 1 ? "Entry":"Entries");
        for (ResourceLocation missingEntry: missingEntries)
        {
            LOGGER.warn(" - Could not resolve "+missingEntry.toString());
        }
        LOGGER.warn("###################################");
    }

    private void collectToMinecraftTags(Map<ResourceLocation, UnbakedTag<T>> map)
    {
        LOGGER.debug("Injecting minecraft dummy Tags for {}.",regName);
        Map<String, Set<ResourceLocation>> flattener = Maps.newHashMap();
        for (Map.Entry<ResourceLocation, UnbakedTag<T>> unbuildTag : map.entrySet())
        {
            if (!unbuildTag.getKey().getNamespace().equals("minecraft"))
            {
                Set<ResourceLocation> current = flattener.getOrDefault(unbuildTag.getKey().getPath(), new HashSet<>());
                current.add(unbuildTag.getKey());
                flattener.put(unbuildTag.getKey().getPath(), current);
                LOGGER.trace("Prepared to add #{} to Dummy-Tag #minecraft:{}.",unbuildTag.getKey(),unbuildTag.getKey().getPath());
            }
        }
        for (Map.Entry<String, Set<ResourceLocation>> flattenedEntry : flattener.entrySet())
        {
            ResourceLocation collected = new ResourceLocation("minecraft:" + flattenedEntry.getKey());
            UnbakedTag<T> builder = map.getOrDefault(collected, new UnbakedTag<>(collected)); //ensure that we don't delete existing minecraft tags
            map.put(collected, builder.addAllDummyEntries(flattenedEntry.getValue()));
            if (builder.replacesDummyAdds())
                LOGGER.trace("Loaded replacement for #{} prevented insertion of dummy entries.",builder.getId());
            else
                LOGGER.trace("Added Dummy-Tag #{}.",builder.getId());
        }
    }

    private void registerUnbakedTags(Map<ResourceLocation, UnbakedTag<T>> map)
    {
        LOGGER.debug("Baking {} {} for Registry with name {}.",map.size(),map.size() == 1? "UnbakedTag":"UnbakedTags",regName.toString());
        while (!map.isEmpty())
        {
            boolean registeredAny = false;
            Iterator<Map.Entry<ResourceLocation, UnbakedTag<T>>> iterator = map.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<ResourceLocation, UnbakedTag<T>> entry1 = iterator.next();
                if ((entry1.getValue()).resolve(this::get))
                {
                    registeredAny = true;
                    this.register((entry1.getValue()).bake());
                    iterator.remove();
                }
            }

            if (!registeredAny)
            {
                for (Map.Entry<ResourceLocation, UnbakedTag<T>> entry2 : map.entrySet())
                {
                    LOGGER.error("Couldn't load {} tag #{} as it either references another tag that doesn't exist, or ultimately references itself", this.regName, entry2.getKey());
                }
                break;
            }
        }
        /* Vanilla does this inorder to make Tag.TagEntry#populate throw an not precious IllegalStateException
         * so let's just throw our own nicer Exception if it fails
        for (Map.Entry<ResourceLocation, UnbakedTag<T>> entry : map.entrySet())
        {
            this.register((entry.getValue()).bake(this.preserveOrder));
        }*/
        if (!map.isEmpty())
        {
            Map<ResourceLocation, Set<ResourceLocation>> failedTags = new HashMap<>();
            for (Map.Entry<ResourceLocation, UnbakedTag<T>> entry : map.entrySet())
            {
                failedTags.put(entry.getKey(), entry.getValue().getFailingTags(this::get));
            }
            //throw new MissingTagsException(regName,failedTags);
            LOGGER.error("debug purpose",new MissingTagsException(regName,failedTags));
        }
        LOGGER.debug("Successfully baked Tags. {} failures remaining.",map.size());
    }

    public void updateOwningTags()
    {
        LOGGER.debug("Indexing {} Tag Data.",regName);
        owningTags.clear();
        for (Map.Entry<ResourceLocation, Tag<T>> entry : getTagMap().entrySet())
        {
            Tag<T> tag = entry.getValue();
            for (T element : tag.getAllElements())
            {
                if (element == null) continue;
                if (element.getRegistryName() == null)
                {
                    LOGGER.warn("Could not update Tag Data for {} (Element of type {}) because RegistryName wasn't set. Failed to add to owningTagCache.", element, element.getClass().getName());
                    continue;
                }
                SortedSet<Tag<T>> set = owningTags.getOrDefault(element.getRegistryName(), new TreeSet<>(TagHelper.tagComparator()));
                set.add(wrapperFactory.apply(tag.getId(), regName)); //use Wrappers, so that Wrappers are returned
                owningTags.put(element.getRegistryName(), set);
            }
            //DEBUG: dump Tags
            LOGGER.trace("Indexed #{}",tag.toString());
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

    private static class UnbakedTag<T extends IForgeRegistryEntry<T>> {
        private final ImmutableTag.Builder<T> builder; //Use Immutable Tags - this should ensure that we always have a quite fast contains check
        private final ResourceLocation id;
        private boolean replacesDummyAdds; //prevents dummy Adds to be performed on replace=true minecraft Tags

        private UnbakedTag(TagLoadingParameter descriptor)
        {
            this(descriptor.getTagId());
        }

        private UnbakedTag(ResourceLocation id)
        {
            this.builder = ImmutableTag.builder();
            this.id = id;
            this.replacesDummyAdds = false;
        }

        private ImmutableTag.Builder<T> getBuilder()
        {
            return builder;
        }

        private ResourceLocation getId()
        {
            return id;
        }

        private boolean replacesDummyAdds()
        {
            return replacesDummyAdds;
        }

        private Set<ResourceLocation> getFailingTags(Function<ResourceLocation, Tag<T>> resourceLocationToTagFunction)
        {
            return getBuilder().findResolveFailures(resourceLocationToTagFunction);
        }

        private UnbakedTag<T> deserialize(Predicate<ResourceLocation> isValueKnownPredicate, Function<ResourceLocation, T> objectGetter, JsonObject json)
        {
            if (JsonUtils.getBoolean(json, "replace", false) && getId().getNamespace().equals("minecraft"))
                this.replacesDummyAdds = true;
            getBuilder().deserialize(isValueKnownPredicate, objectGetter, json);
            return this;
        }

        private UnbakedTag<T> addAllDummyEntries(Iterable<ResourceLocation> tagEntries)
        {
            if (!replacesDummyAdds())
                getBuilder().addAllTags(tagEntries);
            return this;
        }

        private boolean resolve(Function<ResourceLocation, Tag<T>> resourceLocationToTag)
        {
            return getBuilder().resolve(resourceLocationToTag);
        }

        private Tag<T> bake()
        {
            return getBuilder().build(getId());
        }

        private Tag<T> bake(boolean preserveOrder)
        {
            return getBuilder().ordered(preserveOrder).build(getId());
        }
    }
}
