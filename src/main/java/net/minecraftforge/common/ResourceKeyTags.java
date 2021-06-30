package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionReader;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;

/**
 * Class that handles loading and syncing of tags for Dynamic Registry Keys.
 * Dynamic registries load after reloadable data and do not support merging of
 * data from different datapacks, so tags for Dynamic Registry Objects must be
 * loaded in reloadable data (before the dynamic registries load), and can only
 * exist as sets of resource keys (rather than having direct references to the
 * things they are tags of).
 * 
 * Furthermore, they must be resolvable before "normal" tags are currently
 * resolved at in order to be useful during biome loading and modification, so
 * rather than refactor the perfectly-fine-as-is "regular tags", we create a
 * separate set of infrastructure here for managing resource key tags
 */
public class ResourceKeyTags implements IFutureReloadListener
{
    public static final ResourceKeyTags INSTANCE = new ResourceKeyTags();
    private static final Set<RegistryKey<? extends Registry<?>>> EXTRA_REGISTRIES = new HashSet<>();
    
    private Map<RegistryKey<? extends Registry<?>>, ITagCollection<? extends RegistryKey<?>>> data = new HashMap<>();
    private int generation = -1;

    /**
     * Makes a wrapper tag for a registry key tag. The wrapper tag will be sensitive to 
     * @param <T> The type of the things the tag's registry keys are for
     * @param registryKey The key used to create the tag's registry keys. This also sets the name of the directory to load tags from.
     * @param tagID The id of the tag, defines the namespace and id of the json to load the tag from (can include subfolders)
     * @return A tag wrapper for a key tag that will be loaded from data/{tagID-namespace}/tags/resource_keys/{registry}/{tagID-path}.json
     * 
     * @apiSpec Can be created at any time. Key tag wrappers become queryable as soon as key tags have been loaded at least once.
     */
    public static <T> ITag.INamedTag<RegistryKey<T>> makeKeyTagWrapper(RegistryKey<Registry<T>> registryKey, ResourceLocation tagID)
    {
        return new ResourceKeyTags.KeyTag<>(registryKey, tagID);
    }
    
    /**
     * Notify the key tag manager that key tags tags should be loaded for a given registry.
     * All actual dynamic registries will automatically have tags loaded, additional registries that are loaded from datapacks
     * but not "actual" dynamic registries can be marked for having key tags loaded via this method.
     * e.g. Forge adds key tags for Level registrykeys -- these will be loaded from "tags/resource_keys/dimension" 
     * 
     * Static registries should use standard tags or be made to work with standard tags.
     * 
     * @param registryKey The key for the registry that key tags should be loaded for
     * 
     * @apiSpec Must be called before the first load of key tags (mod constructor or common setup is fine).
     * Safe to call during parallel modloading.
     */
    public synchronized static void markRegistryForNeedingKeyTagsLoaded(RegistryKey<? extends Registry<?>> registryKey)
    {
        EXTRA_REGISTRIES.add(registryKey);
    }

    @Override
    public CompletableFuture<Void> reload(IStage stage, IResourceManager manager, IProfiler workerProfiler, IProfiler mainProfiler, Executor workerExecutor,
        Executor mainExecutor)
    {
        // load tags for each dynamic registry
        // (we do this ensuring we don't classload the dynamic registries too early by getting the worldgen registries,
        // each of which the dynamic registries will make a copy of later)
        
        // we make a tag reader for each dynamic registry key,
        // each tag reader is able to produce a completablefuture that reads and merges tag jsons off-thread
        // when this concludes, the results of each of these futures can be fed back into the tag reader to produce the final tag collection
        final Map<RegistryKey<? extends Registry<?>>, Pair<TagCollectionReader<RegistryKey<?>>, CompletableFuture<Map<ResourceLocation,ITag.Builder>>>> futureTagBuilders = new HashMap<>();
        final List<CompletableFuture<Map<ResourceLocation,ITag.Builder>>> justTheFutures = new ArrayList<>();
        final Consumer<RegistryKey<? extends Registry<?>>> keyConsumer = registryKey ->
        {
            final Pair<TagCollectionReader<RegistryKey<?>>, CompletableFuture<Map<ResourceLocation, ITag.Builder>>> pair = getFutureTagBuilder(registryKey, manager, workerExecutor);
            futureTagBuilders.put(registryKey, pair);
            justTheFutures.add(pair.getSecond());
        };
        for (Registry<?> registry : WorldGenRegistries.REGISTRY)
        {
            keyConsumer.accept(registry.key());
        }
        // there are some things registry keys are used for (worlds and dimensions mostly) that aren't in the dynamic registries proper,
        // we need to load tags for those as well if they're marked as needing tags for
        for (RegistryKey<? extends Registry<?>> registryKey : EXTRA_REGISTRIES)
        {
            keyConsumer.accept(registryKey);
        }
        return CompletableFuture.allOf(justTheFutures.toArray(new CompletableFuture[justTheFutures.size()]))
            .thenCompose(stage::wait) // wait for all jsons to be parsed, then finish building tags on main thread
            .thenAcceptAsync(voidArg -> this.finishLoadingOnMainThread(futureTagBuilders), mainExecutor);
    }
    
    private static Pair<TagCollectionReader<RegistryKey<?>>, CompletableFuture<Map<ResourceLocation, ITag.Builder>>> getFutureTagBuilder(final RegistryKey<? extends Registry<?>> registryKey, final IResourceManager manager, final Executor workerExecutor)
    {
        final Function<ResourceLocation, Optional<RegistryKey<?>>> tagEntryFactory =
            rl -> Optional.<RegistryKey<?>>of(RegistryKey.create(registryKey, rl));
        final ResourceLocation registryName = registryKey.location();
        final String directory = "tags/resource_keys/" + registryName.getPath();
        final String nameForLogger = registryName.toString();
        final TagCollectionReader<RegistryKey<?>> reader = new TagCollectionReader<>(tagEntryFactory, directory, nameForLogger);
        return Pair.of(reader, reader.prepare(manager, workerExecutor));
    }
    
    private void finishLoadingOnMainThread(final Map<RegistryKey<? extends Registry<?>>, Pair<TagCollectionReader<RegistryKey<?>>, CompletableFuture<Map<ResourceLocation,ITag.Builder>>>> completedTagBuilders)
    {
        ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, ITagCollection<? extends RegistryKey<?>>> mapBuilder = ImmutableMap.builder();
        completedTagBuilders.forEach((key,readerAndParsedData) ->
        {
            final TagCollectionReader<RegistryKey<?>> reader = readerAndParsedData.getFirst();
            final CompletableFuture<Map<ResourceLocation,ITag.Builder>> completedParseData = readerAndParsedData.getSecond();
            mapBuilder.put(key, reader.load(completedParseData.join()));
        });
        
        // update data, mark data as being different to observers
        this.updateTags(mapBuilder.build());
    }
    
    /**
     * @return an immutable view of the current tag registry
     * 
     * @apiNote internal, used for syncing to client
     */
    public Map<RegistryKey<? extends Registry<?>>, ITagCollection<? extends RegistryKey<?>>> getTags()
    {
        return this.data;
    }
    
    /**
     * Gets the current collection of tags for a given registry key
     * @param <T> The type of the thing the tags are for
     * @param registryKey The registry key for the collection of tags being requested
     * @return collection of tags
     * 
     * @implNote hides an unchecked cast for convenience (registry keys are safe to cast to whatever as they're just a few strings)
     */
    public @Nullable <T> ITagCollection<RegistryKey<T>> getTagCollection(final RegistryKey<Registry<T>> registryKey)
    {
        return (ITagCollection<RegistryKey<T>>) this.data.get(registryKey);
    }
    
    /**
     * Updates the key tag registry
     * @param newTags new tags
     * 
     * @apiNote internal, called when tags are loaded or when tags are received from the server
     */
    public void updateTags(final Map<RegistryKey<? extends Registry<?>>, ITagCollection<? extends RegistryKey<?>>> newTags)
    {
        this.data = newTags;
        this.generation++;
        if (this.generation<0)
            this.generation=0;
    }
    
    private static class KeyTag<T> implements ITag.INamedTag<RegistryKey<T>>
    {
        private final RegistryKey<Registry<T>> registryKey;
        private final ResourceLocation name;
        
        private int generation = Integer.MIN_VALUE;
        @Nullable private ITag<RegistryKey<T>> proxy = null;
        
        private KeyTag(RegistryKey<Registry<T>> registryKey, ResourceLocation name)
        {
            this.registryKey = registryKey;
            this.name = name;
        }

        @Override
        public boolean contains(final RegistryKey<T> key)
        {
            this.ensureProxyUpToDate();
            return this.proxy.contains(key);
        }

        @Override
        public List<RegistryKey<T>> getValues()
        {
            this.ensureProxyUpToDate();
            return this.proxy.getValues();
        }

        @Override
        public ResourceLocation getName()
        {
            return this.name;
        }
        
        private void ensureProxyUpToDate()
        {
            final int keyTagsGeneration = ResourceKeyTags.INSTANCE.generation;
            if (this.proxy == null || this.generation != keyTagsGeneration)
            {
                if (keyTagsGeneration < 0)
                {
                    throw new IllegalStateException(String.format("%s resource key tag %s cannot be resolved before key tags load", this.registryKey.location(), this.name));
                }
                
                this.generation = keyTagsGeneration;
                final ITagCollection<RegistryKey<T>> tags = ResourceKeyTags.INSTANCE.getTagCollection(this.registryKey);
                if (tags == null)
                {
                    throw new IllegalStateException(String.format("Failed to resolve resource key tag %s -- no tag collection found for registry key %s (be sure to include a tag json with your datapack)", this.name, this.registryKey.location()));
                }
                
                this.proxy = tags.getTagOrEmpty(this.name);
            }
        }
        
    }
}
