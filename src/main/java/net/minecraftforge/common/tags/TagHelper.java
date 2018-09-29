package net.minecraftforge.common.tags;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.ITagEntry;
import net.minecraft.tags.Tag.TagEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.TagProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    public static <T extends IForgeRegistryEntry<T>> Ordering<T> registryNameComparator()
    {
        return Ordering.natural().nullsFirst().onResultOf(entry -> entry != null ? entry.getRegistryName() : null);
    }

    public static Ordering<Tag<?>> tagComparator()
    {
        return Ordering.natural().onResultOf((Function<Tag<?>, ResourceLocation>) input -> input != null ? input.getId() : null).<Tag<?>>nullsFirst();
    }

    public static <T extends IForgeRegistryEntry<T>> Supplier<IForgeRegistry<T>> lazyRegistrySupplier(ResourceLocation registry)
    {
        return () -> RegistryManager.ACTIVE.getRegistry(registry);
    }

    /**
     * The {@link UnaryOperator} returned by this Method creates {@link ImmutableTag}s from all Entries passed to it.
     * TagEntries with null Tag's will simply returned to the caller.
     * The copy Function used is {@link ImmutableTag#copyOf(Tag)}, which does not preserve any TagStructuring the Tag might have.
     *
     * @param <T> The type of the Tag
     * @return An UnaryOperator
     */
    public static <T extends IForgeRegistryEntry<T>> UnaryOperator<Tag.TagEntry<T>> immutableShallowCopyTransformer()
    {
        return (tagEntry ->
        {
            if (tagEntry.getTag() == null)
                return tagEntry;
            return new Tag.TagEntry<>(ImmutableTag.copyOf(tagEntry.getTag()));
        });
    }

    /**
     * The {@link UnaryOperator} returned by this Method creates {@link ImmutableTag}s from all Entries passed to it.
     * TagEntries with null Tag's will simply returned to the caller.
     * The copy Function used is {@link ImmutableTag#copyPreserveTagStructure(ResourceLocation, Collection)}, which does preserve the Tags TagStructure.
     *
     * @param <T> The type of the Tag
     * @return An UnaryOperator
     */
    public static <T extends IForgeRegistryEntry<T>> UnaryOperator<Tag.TagEntry<T>> immutableDeepCopyTransformer()
    {
        return (tagEntry ->
        {
            if (tagEntry.getTag() == null)
                return tagEntry;
            return new Tag.TagEntry<>(ImmutableTag.copyPreserveTagStructure(tagEntry.getTag()));
        });
    }

    public static <T> int tagHashCode(Tag<T> tag)
    {
        return tag.getId().hashCode();
    }

    public static <T> boolean tagEquals(Tag<T> tag1, Object o)
    {
        if (tag1 == o) return true;
        if (!(o instanceof Tag)) return false;

        Tag<?> tag = (Tag<?>) o;

        return tag1.getId().equals(tag.getId());
    }

    /**
     * Execute consumer on every Item in this tag's entries and enterTag on every {@link Tag.TagEntry}.
     * Will recursively be invoked on {@link Tag.TagEntry}'s if enterTag returns true.
     * Mainly for debug purposes.
     *
     * @param tag      The tag
     * @param consumer The consumer
     * @param enterTag Predicate deciding whether a Tag should be analyzed
     * @param <T>      The type
     */
    public static <T> void inspectTagStructure(Tag<T> tag, Consumer<? super T> consumer, Predicate<Tag<T>> enterTag)
    {
        for (Tag.ITagEntry<T> entry : tag.getEntries())
        {
            if (entry instanceof Tag.TagEntry && ((TagEntry<T>) entry).getTag() != null && enterTag.test(((TagEntry<T>) entry).getTag()))
            {
                inspectTagStructure(((TagEntry<T>) entry).getTag(), consumer, enterTag);
            } else
            {
                List<T> list = new LinkedList<>();
                entry.populate(list);
                for (T thing : list)
                {
                    consumer.accept(thing);
                }
            }
        }
    }

    /*
    public static void immutableTagTest()
    {
        StringBuilder builder = new StringBuilder("\n");
        Map<ResourceLocation, Tag<Item>> resolveMap = new HashMap<>();
        Tag<Item> bones = ImmutableTag.<Item>builder().add(Items.BONE_MEAL).add(Items.BONE).build(new ResourceLocation("bones"));
        resolveMap.put(new ResourceLocation("bones"), bones);
        Tag<Item> potato = ImmutableTag.<Item>builder().add(Items.BAKED_POTATO).add(Items.POTATO).build(new ResourceLocation("potatoes"));
        resolveMap.put(new ResourceLocation("potato"), potato);
        Tag.Builder<Item> compoundBuilder = ImmutableTag.<Item>builder().add(bones).add(potato).add(Items.BREAD);
        compoundBuilder.resolve(resolveMap::get);
        Tag<Item> compound = compoundBuilder.build(new ResourceLocation("compound"));
        resolveMap.put(new ResourceLocation("compound"), compound);
        Tag.Builder<Item> moreBuilder = ImmutableTag.<Item>builder().add(compound).add(Items.CARROT);
        moreBuilder.resolve(resolveMap::get);
        Tag<Item> someMore = moreBuilder.build(new ResourceLocation("more"));
        Tag<Item> someMoreUnresolved = ImmutableTag.<Item>builder().add(compound).add(Items.CARROT).build(new ResourceLocation("more")); //should
        Consumer<Item> printItem = item -> builder.append("Found item ").append(item.getRegistryName()).append("\n");
        Predicate<Tag<Item>> printTag = tag ->
        {
            builder.append("Found Tag #").append(tag.getId()).append("\n");
            return true;
        };
        builder.append("bones:\n").append(bones.toString()).append("\n");
        TagHelper.inspectTagStructure(bones, printItem, printTag);
        builder.append("potato:\n").append(potato.toString()).append("\n");
        TagHelper.inspectTagStructure(potato, printItem, printTag);
        builder.append("compound:\n").append(compound.toString()).append("\n");
        TagHelper.inspectTagStructure(compound, printItem, printTag);
        builder.append("more:\n").append(someMore.toString()).append("\n");
        TagHelper.inspectTagStructure(someMore, printItem, printTag);
        builder.append("moreUnresolved:\n").append(someMoreUnresolved.toString()).append("\n");
        TagHelper.inspectTagStructure(someMoreUnresolved, printItem, printTag);
        Tag<Item> oneLevelCopy = ImmutableTag.copyPreserveShallowTagStructure(someMore);
        builder.append("oneLevelCopy:\n").append(oneLevelCopy.toString()).append("\n");
        TagHelper.inspectTagStructure(oneLevelCopy, printItem, printTag);
        LOGGER.info(builder.toString());
    }*/

    public static <T, R> R collectTagEntries(Collector<? super T, ?, R> collector, Collection<Tag.ITagEntry<T>> collection)
    {
        return collection.stream().flatMap(tagentry ->
        {
            List<T> tagEntries = new ArrayList<>();
            if (!(tagentry instanceof Tag.TagEntry) || ((TagEntry<T>) tagentry).getTag() != null) //verify, so that populate doesn't cause IllegalStateException
                tagentry.populate(tagEntries);
            return tagEntries.stream();
        })
                .collect(collector);
    }

    /**
     * Sorts the entry Collection by there Registry names. Notice that {@link Tag.TagEntry#getSerializedId()} will be used for comparisons with the flattened out Elements of all other Entries.
     *
     * @param entries The Entries to sort
     * @param <T>     The type of the entries
     * @return Whatever the collector wants to have...
     */
    public static <T extends IForgeRegistryEntry<T>, R> R sortTagEntries(Collection<ITagEntry<T>> entries, UnaryOperator<Tag.TagEntry<T>> tagTransformer, Collector<? super Tag.ITagEntry<T>, ?, R> collector)
    {
        return entries.stream().flatMap(tagentry ->
        {
            if (tagentry instanceof Tag.TagEntry)
            {
                Tag.TagEntry<T> transformed = tagTransformer.apply((Tag.TagEntry<T>) tagentry);
                return Stream.of(new Tuple<>(transformed, transformed.getSerializedId()));
            } else if (tagentry != null)
            {
                List<T> list = new ArrayList<>();
                tagentry.populate(list);
                return list.stream().filter(entry -> entry.getRegistryName() != null).map(entry -> new Tuple<>(singletonEntry(entry), entry.getRegistryName()));
            }
            return Stream.of();
        }).sorted(Ordering.natural().onResultOf(Tuple::getB)).map(Tuple::getA).collect(collector);
    }

    /**
     * Sorts the entry Collection by there Registry names. Notice that {@link Tag.TagEntry#getSerializedId()} will be used for comparisons with the flattened out Elements of all other Entries.
     *
     * @param entries The Entries to sort
     * @param <T>     The type of the entries
     * @return A sorted Set which contains all {@link Tag.TagEntry} that were in entries, whilst all other Entries are flattened out with {{@link #singletonEntry(Object)}}
     */
    public static <T extends IForgeRegistryEntry<T>> Set<ITagEntry<T>> sortTagEntries(Collection<ITagEntry<T>> entries)
    {
        return sortTagEntries(entries, UnaryOperator.identity(), Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * @param object The Object to create
     * @param <T>    The type of the resulting TagEntry
     * @return a {@link Tag.ListEntry} containing only this Object
     */
    public static <T> Tag.ListEntry<T> singletonEntry(T object)
    {
        return new Tag.ListEntry<>(Collections.singleton(object));
    }

    public static <T> Tag.Builder<T> addAll(Tag.Builder<T> builder, Iterable<ResourceLocation> iterable)
    {
        for (ResourceLocation thing : iterable)
        {
            builder.add(thing);
        }
        return builder;
    }

    /**
     * @param registry The registry
     * @param <T>      The Type
     * @return The Registries {@link TagProvider}, or null if it doesn't support tagging
     */
    @Nullable
    public static <T extends IForgeRegistryEntry<T>> TagProvider<T> getTagsIfPresent(IForgeRegistry<T> registry)
    {
        if (registry.supportsTagging()) return registry.getTagProvider();
        return null;
    }

    public static <T extends IForgeRegistryEntry<T>> boolean isTagPresent(IForgeRegistry<T> registry, ResourceLocation object, ResourceLocation id)
    {
        if (!registry.supportsTagging()) return false;
        T obj = registry.getValue(object);
        return obj != null && registry.getTagProvider().getOrCreate(id).contains(obj);
    }

    public static <T extends IForgeRegistryEntry<T>> boolean isTagPresent(IForgeRegistry<T> registry, T object, ResourceLocation id)
    {
        if (!registry.supportsTagging()) return false;
        return registry.getTagProvider().getOrCreate(id).contains(object);
    }

    public static <T extends IForgeRegistryEntry<T>> boolean isTagPresent(IForgeRegistry<T> registry, ResourceLocation object, Tag<T> tag)
    {
        T obj = registry.getValue(object);
        return obj != null && tag.contains(obj);
    }

    public static <T extends IForgeRegistryEntry<T>> boolean isTagPresent(T object, Tag<T> tag)
    {
        return tag.contains(object);
    }

    public static <T extends IForgeRegistryEntry<T>> SortedSet<ResourceLocation> getTagIDsFor(IForgeRegistry<T> registry, ResourceLocation object)
    {
        if (!registry.supportsTagging()) return Collections.emptySortedSet();
        return registry.getTagProvider().getOwningTagIDs(object);
    }

    public static <T extends IForgeRegistryEntry<T>> SortedSet<ResourceLocation> getTagIDsFor(IForgeRegistry<T> registry, T object)
    {
        if (!registry.supportsTagging()) return Collections.emptySortedSet();
        return registry.getTagProvider().getOwningTagIDs(object);
    }

    public static <T extends IForgeRegistryEntry<T>> Collection<T> getTaggedObjects(IForgeRegistry<T> registry, ResourceLocation tagId)
    {
        if (!registry.supportsTagging()) return Collections.emptySortedSet();
        return registry.getTagProvider().getOrCreate(tagId).getAllElements();
    }

    public static <T extends IForgeRegistryEntry<T>> SortedSet<Tag<T>> getTagsFor(IForgeRegistry<T> registry, ResourceLocation object)
    {
        if (!registry.supportsTagging()) return Collections.emptySortedSet();
        return registry.getTagProvider().getOwningTags(object);
    }

    public static <T extends IForgeRegistryEntry<T>> SortedSet<Tag<T>> getTagsFor(IForgeRegistry<T> registry, T object)
    {
        if (!registry.supportsTagging()) return Collections.emptySortedSet();
        return registry.getTagProvider().getOwningTags(object);
    }

    public static <T extends IForgeRegistryEntry<T>> List<Tag<T>> getTagsMatching(IForgeRegistry<T> registry, Predicate<Tag<T>> predicate)
    {
        if (!registry.supportsTagging()) return Collections.emptyList();
        return registry.getTagProvider().getMatchingTags(predicate);
    }

    public static <T extends IForgeRegistryEntry<T>> boolean anyTagsMatching(IForgeRegistry<T> registry, Predicate<Tag<T>> predicate)
    {
        if (!registry.supportsTagging()) return false;
        return registry.getTagProvider().matchesTag(predicate);
    }

    public static <T extends IForgeRegistryEntry<T>> SortedSet<Tag<T>> getTagsMatchingOnItem(IForgeRegistry<T> registry, ResourceLocation id, Predicate<Tag<T>> predicate)
    {
        if (!registry.supportsTagging()) return Collections.emptySortedSet();
        return registry.getTagProvider().getMatchingTagsOnItem(id, predicate);
    }

    public static <T extends IForgeRegistryEntry<T>> boolean anyTagsMatchingOnItem(IForgeRegistry<T> registry, ResourceLocation id, Predicate<Tag<T>> predicate)
    {
        if (!registry.supportsTagging()) return false;
        return registry.getTagProvider().matchesTagOnItem(id, predicate);
    }

    public static <V> Predicate<Tag<V>> containsObjectPredicate(V object)
    {
        return tag -> tag.contains(object);
    }

    public static <T> Predicate<Tag<T>> idEqualsPredicate(ResourceLocation id)
    {
        return tag -> tag.getId().equals(id);
    }

    public static <T> Predicate<Tag<T>> pathEqualsPredicate(String path)
    {
        return tag -> tag.getId().getPath().equals(path);
    }

    public static <T> Predicate<Tag<T>> pathContainsPredicate(String path)
    {
        return tag -> tag.getId().getPath().contains(path);
    }

    public static <T> Predicate<Tag<T>> pathMatchesPattern(String pattern)
    {
        return tag -> tag.getId().getPath().matches(pattern);
    }

    public static <T> Predicate<Tag<T>> pathStartsWithPredicate(String s)
    {
        return tag -> tag.getId().getPath().startsWith(s);
    }

    public static <T> Predicate<Tag<T>> byModPredicate(String modId)
    {
        return tag -> tag.getId().getNamespace().equals(modId);
    }

    public static <T extends IForgeRegistryEntry<T>> SortedSet<Tag<T>> tagsWithExactPath(IForgeRegistry<T> registry, T object, String path)
    {
        return getTagsMatchingOnItem(registry, Objects.requireNonNull(object.getRegistryName()), pathEqualsPredicate(path));
    }

    public static <T extends IForgeRegistryEntry<T>> SortedSet<Tag<T>> tagsContainingPath(IForgeRegistry<T> registry, T object, String path)
    {
        return getTagsMatchingOnItem(registry, Objects.requireNonNull(object.getRegistryName()), pathContainsPredicate(path));
    }

    public static <T extends IForgeRegistryEntry<T>> SortedSet<Tag<T>> tagsStartingWithPath(IForgeRegistry<T> registry, T object, String path)
    {
        return getTagsMatchingOnItem(registry, Objects.requireNonNull(object.getRegistryName()), pathStartsWithPredicate(path));
    }

    public static <T extends IForgeRegistryEntry<T>> SortedSet<Tag<T>> tagsFromMod(IForgeRegistry<T> registry, T object, String modId)
    {
        return getTagsMatchingOnItem(registry, Objects.requireNonNull(object.getRegistryName()), byModPredicate(modId));
    }

    public static <T extends IForgeRegistryEntry<T>> boolean anyTagsWithExactPath(IForgeRegistry<T> registry, T object, String path)
    {
        return anyTagsMatchingOnItem(registry, Objects.requireNonNull(object.getRegistryName()), pathEqualsPredicate(path));
    }

    public static <T extends IForgeRegistryEntry<T>> boolean anyTagsContainingPath(IForgeRegistry<T> registry, T object, String path)
    {
        return anyTagsMatchingOnItem(registry, Objects.requireNonNull(object.getRegistryName()), pathContainsPredicate(path));
    }

    public static <T extends IForgeRegistryEntry<T>> boolean anyTagsStartingWithPath(IForgeRegistry<T> registry, T object, String path)
    {
        return anyTagsMatchingOnItem(registry, Objects.requireNonNull(object.getRegistryName()), pathStartsWithPredicate(path));
    }

    public static <T extends IForgeRegistryEntry<T>> boolean anyTagsFromMod(IForgeRegistry<T> registry, T object, String modId)
    {
        return anyTagsMatchingOnItem(registry, Objects.requireNonNull(object.getRegistryName()), byModPredicate(modId));
    }
}
