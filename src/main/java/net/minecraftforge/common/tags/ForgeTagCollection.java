package net.minecraftforge.common.tags;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.CallbackI.I;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ForgeTagCollection<T> extends NetworkTagCollection<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    private final ImmutableList<String> resourceLocationPrefixes;
    private final Map<T, SortedSet<Tag<T>>> owningTags;
    private boolean collectDummyTags;
    private Supplier<ForgeTagBuilder<T>> builderFactory;
    public static <T extends IForgeRegistryEntry<T>> ForgeTagCollection<T> fromForgeRegistry(IForgeRegistry<T> registry, List<String> resourceLocationPrefixes, String itemTypeName)
    {
        return fromForgeRegistry(registry,resourceLocationPrefixes,itemTypeName,false);
    }
    //cannot add this as a constructor, cause Java cannot infer the addtional Bound on T in a constructor
    public static <T extends IForgeRegistryEntry<T>> ForgeTagCollection<T> fromForgeRegistry(IForgeRegistry<T> registryIn, List<String> resourceLocationPrefixes, String itemTypeName, boolean preserveOrder)
    {
        ForgeRegistry<T> registry = (ForgeRegistry<T>) registryIn;
        return new ForgeTagCollection<>(registry::containsKey,registry::getValue,registry::getID,registry::getValue,resourceLocationPrefixes,preserveOrder,itemTypeName);
    }

    public static List<String> getDefaultLoadingLocation(ResourceLocation id)
    {
        return ImmutableList.of("tags/" + id.getPath(), "tags/" + id.getNamespace() + "/" + id.getPath());
    }

    public ForgeTagCollection(Predicate<ResourceLocation> isValueKnownPredicateIn, Function<ResourceLocation, T> resourceLocationToItemIn, Function<T, Integer> itemToId, Function<Integer, T> idToItem, List<String> resourceLocationPrefixes, boolean preserveOrderIn, String itemTypeNameIn)
    {
        super(isValueKnownPredicateIn, resourceLocationToItemIn, itemToId, idToItem, resourceLocationPrefixes.get(0), preserveOrderIn, itemTypeNameIn);
        this.resourceLocationPrefixes = ImmutableList.copyOf(resourceLocationPrefixes);
        this.owningTags = new HashMap<>();
        this.collectDummyTags = true;
        this.builderFactory = ForgeTagBuilder::immutableTagBuilder;
    }

    public ForgeTagCollection(RegistryNamespaced<ResourceLocation, T> registryIn, List<String> resourceLocationPrefixes, String itemTypeName)
    {
        super(registryIn, resourceLocationPrefixes.get(0), itemTypeName);
        this.resourceLocationPrefixes = ImmutableList.copyOf(resourceLocationPrefixes);
        this.owningTags = new HashMap<>();
        this.collectDummyTags = true;
        this.builderFactory = ForgeTagBuilder::immutableTagBuilder;
    }

    public ForgeTagCollection<T> setCollectDummyTags(boolean collectDummyTags)
    {
        this.collectDummyTags = collectDummyTags;
        return this;
    }

    public Supplier<ForgeTagBuilder<T>> getBuilderFactory()
    {
        return builderFactory;
    }

    public ForgeTagCollection<T> setBuilderFactory(Supplier<ForgeTagBuilder<T>> builderFactory)
    {
        this.builderFactory = builderFactory;
        return this;
    }

    public boolean isCollectingDummyTags() {
        return collectDummyTags;
    }

    @Override
    public void reload(IResourceManager resourceManager)
    {
        LOGGER.info("Loading {} Tags.", itemTypeName);
        Map<ResourceLocation, UnbakedTag<T>> map = deserializeTags(getLoadingParameters(resourceManager), resourceManager);
        clear(); //clear existing Tag Data before registering new, so that removed tags aren't kept after reload. Vanilla bug?
        if (!map.isEmpty())
        {
            if (isCollectingDummyTags()) collectDummyTags(map);
            registerUnbakedTags(map);
        } else
        {
            LOGGER.debug("Skipping Registration because there were no {} Tags added", itemTypeName);
        }
        updateOwningTags();
        LOGGER.info("Finished loading {} {} Tags.", getTagMap().size(), itemTypeName);
    }

    /**
     * This is mainly here for Vanilla compat.
     * @see #getOwningTagsForItem(Object)
     * @param itemIn The item For which to find Tags containing it
     * @return The ID's of the Tags containing the specified Item
     */
    @Override
    public SortedSet<ResourceLocation> getOwningTags(T itemIn)
    {
        return getOwningTagsForItem(itemIn).stream().map(Tag::getId).collect(ImmutableSortedSet.toImmutableSortedSet(Comparator.naturalOrder()));
    }

    /**
     * @implNote This is more efficient than the Vanilla Implementation
     * @param itemIn The item For which to find Tags containing it
     * @return The Tag's containing the specifying Item. Ordered by their ID's.
     */
    public SortedSet<Tag<T>> getOwningTagsForItem(T itemIn)
    {
        return Collections.unmodifiableSortedSet(owningTags.getOrDefault(itemIn,Collections.emptySortedSet()));
    }

    protected Map<T, SortedSet<Tag<T>>> getOwningTagMapModifiable()
    {
        return owningTags;
    }

    @Override
    public Map<ResourceLocation, Tag<T>> getTagMap()
    {
        return Collections.unmodifiableMap(super.getTagMap());
    }

    protected Map<ResourceLocation, Tag<T>> getTagMapModifiable()
    {
        return super.getTagMap();
    }

    protected ImmutableList<String> getLocationPrefixes()
    {
        return this.resourceLocationPrefixes;
    }

    protected Stream<TagLoadingParameter> getLoadingParameters(IResourceManager resourceManager)
    {
        ImmutableList<String> prefixes = getLocationPrefixes();
        Stream<TagLoadingParameter> resources = Stream.of();
        for (String s : prefixes)
        {
            resources = Stream.concat(resources, resourceManager.getAllResourceLocations(s, s1 -> s1.endsWith(".json")).stream().map(location -> new TagLoadingParameter(s, location)));
        }
        return resources;
    }

    protected Map<ResourceLocation, UnbakedTag<T>> deserializeTags(Stream<TagLoadingParameter> stream, IResourceManager resourceManager)
    {
        LOGGER.debug("Parsing {} Tag Data.", itemTypeName);
        Map<ResourceLocation, UnbakedTag<T>> map = Maps.newHashMap();
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
                            LOGGER.error("Couldn't load {} Tag list {} from {} in data pack {} as it's empty or null", this.itemTypeName, tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), iresource.getPackName());
                        } else
                        {
                            UnbakedTag<T> builder = map.getOrDefault(tagLoadingParameter.getTagId(), new UnbakedTag<>(tagLoadingParameter,getBuilderFactory()));
                            builder.deserialize(isValueKnownPredicate, resourceLocationToItem, jsonobject);
                            map.put(tagLoadingParameter.getTagId(), builder);
                        }
                    } catch (MissingEntriesException e)
                    {
                        printMissingEntriesError(tagLoadingParameter, e);
                    } catch (RuntimeException | IOException ioexception)
                    {
                        LOGGER.error("Couldn't read {} Tag {} from {} in Data Pack {}", this.itemTypeName, tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), iresource.getPackName(), ioexception);
                    } finally
                    {
                        IOUtils.closeQuietly((Closeable) iresource);
                    }
                }
            } catch (IOException ioexception1)
            {
                LOGGER.error("Couldn't read {} Tag {} from {}", this.itemTypeName, tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), ioexception1);
            }
        });
        return map;
    }

    protected void printMissingEntriesError(TagLoadingParameter tagLoadingParameter, MissingEntriesException e)
    {
        Set<ResourceLocation> missingEntries = e.getMissingEntries();
        LOGGER.error("Failed to parse tag {} because 1 or more Entries could not be resolved", tagLoadingParameter.getTagId());
        LOGGER.warn("###################################");
        LOGGER.warn("Missing {} {}", missingEntries.size(), missingEntries.size() == 1 ? "Entry" : "Entries");
        for (ResourceLocation missingEntry : missingEntries)
        {
            LOGGER.warn(" - Could not resolve " + missingEntry.toString());
        }
        LOGGER.warn("###################################");
    }

    protected void collectDummyTags(Map<ResourceLocation, UnbakedTag<T>> map)
    {
        LOGGER.debug("Injecting minecraft dummy {} Tags", itemTypeName);
        Map<String, Set<ResourceLocation>> flattener = Maps.newHashMap();
        for (Map.Entry<ResourceLocation, UnbakedTag<T>> unbuildTag : map.entrySet())
        {
            if (!unbuildTag.getKey().getNamespace().equals("minecraft"))
            {
                Set<ResourceLocation> current = flattener.getOrDefault(unbuildTag.getKey().getPath(), new HashSet<>());
                current.add(unbuildTag.getKey());
                flattener.put(unbuildTag.getKey().getPath(), current);
                LOGGER.trace("Prepared to add #{} to Dummy-Tag #minecraft:{}.", unbuildTag.getKey(), unbuildTag.getKey().getPath());
            }
        }
        for (Map.Entry<String, Set<ResourceLocation>> flattenedEntry : flattener.entrySet())
        {
            ResourceLocation collected = new ResourceLocation("minecraft:" + flattenedEntry.getKey());
            UnbakedTag<T> builder = map.getOrDefault(collected, new UnbakedTag<>(collected,getBuilderFactory())); //ensure that we don't delete existing minecraft tags
            map.put(collected, builder.addAllDummyEntries(flattenedEntry.getValue()));
            if (builder.replacesDummyAdds())
                LOGGER.trace("Loaded replacement for #{} prevented insertion of dummy entries.", builder.getId());
            else
                LOGGER.trace("Added Dummy-Tag #{}.", builder.getId());
        }
    }

    protected void registerUnbakedTags(Map<ResourceLocation, UnbakedTag<T>> map)
    {
        LOGGER.debug("Baking {} {} {}.", map.size(), itemTypeName, map.size() == 1 ? "UnbakedTag" : "UnbakedTags");
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
                    this.register((entry1.getValue()).bake(preserveOrder));
                    iterator.remove();
                }
            }

            if (!registeredAny)
            {
                for (Map.Entry<ResourceLocation, UnbakedTag<T>> entry2 : map.entrySet())
                {
                    LOGGER.error("Couldn't load {} Tag #{} as it either references another tag that doesn't exist, or ultimately references itself", itemTypeName, entry2.getKey());
                }
                break;
            }
        }
        /* Vanilla does this inorder to make Tag.TagEntry#populate throw an not precious IllegalStateException
         * so let's just throw our own nicer Exception if it fails.
         * Also this allows as to collect all failed Tags, instead of just displaying a single one
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
            throw new MissingTagsException(itemTypeName, failedTags);
        }
        LOGGER.debug("Successfully baked Tags. {} failures remaining.", map.size());
    }


    public void updateOwningTags()
    {
        LOGGER.debug("Indexing {} Tag Data.", itemTypeName);
        owningTags.clear();
        for (Map.Entry<ResourceLocation, Tag<T>> entry : getTagMap().entrySet())
        {
            Tag<T> tag = entry.getValue();
            for (T element : tag.getAllElements())
            {
                if (element == null) continue;
                SortedSet<Tag<T>> set = owningTags.getOrDefault(element, new TreeSet<>(ImmutableTag.tagIdComparator()));
                owningTags.put(element, set);
            }
            //DEBUG: dump Tags
            LOGGER.trace("Indexed #{}", tag.toString());
        }
    }

    protected static class TagLoadingParameter { //value class, so that we don't need to use Triples...
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

    protected static class UnbakedTag<T> {
        private final ForgeTagBuilder<T> builder; //Use Immutable Tags - this should ensure that we always have a quite fast contains check
        private final ResourceLocation id;
        private boolean replacesDummyAdds; //prevents dummy Adds to be performed on replace=true minecraft Tags

        protected UnbakedTag(TagLoadingParameter descriptor, Supplier<ForgeTagBuilder<T>> builderSupplier)
        {
            this(descriptor.getTagId(),builderSupplier);
        }

        protected UnbakedTag(ResourceLocation id, Supplier<ForgeTagBuilder<T>> builderSupplier)
        {
            this.builder = builderSupplier.get();
            this.id = id;
            this.replacesDummyAdds = false;
        }

        protected ForgeTagBuilder<T> getBuilder()
        {
            return builder;
        }

        protected ResourceLocation getId()
        {
            return id;
        }

        protected boolean replacesDummyAdds()
        {
            return replacesDummyAdds;
        }

        protected Set<ResourceLocation> getFailingTags(Function<ResourceLocation, Tag<T>> resourceLocationToTagFunction)
        {
            return getBuilder().findResolveFailures(resourceLocationToTagFunction);
        }

        protected UnbakedTag<T> deserialize(Predicate<ResourceLocation> isValueKnownPredicate, Function<ResourceLocation, T> objectGetter, JsonObject json)
        {
            if (JsonUtils.getBoolean(json, "replace", false) && getId().getNamespace().equals("minecraft"))
                this.replacesDummyAdds = true;
            getBuilder().deserialize(isValueKnownPredicate, objectGetter, json);
            return this;
        }

        protected UnbakedTag<T> addAllDummyEntries(Iterable<ResourceLocation> tagEntries)
        {
            if (!replacesDummyAdds())
                getBuilder().addAllTags(tagEntries);
            return this;
        }

        protected boolean resolve(Function<ResourceLocation, Tag<T>> resourceLocationToTag)
        {
            return getBuilder().resolve(resourceLocationToTag);
        }

        protected Tag<T> bake()
        {
            return getBuilder().build(getId());
        }

        protected Tag<T> bake(boolean preserveOrder)
        {
            return getBuilder().ordered(preserveOrder).build(getId());
        }
    }
}
