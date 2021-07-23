/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollectionReader;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * Class that handles loading of tags for resource keys for Dynamic Registries and similar data.
 * Dynamic registries load after reloadable data and do not support merging of
 * data from different datapacks, so tags for Dynamic Registry Objects must be
 * loaded in reloadable data (before the dynamic registries load), and can only
 * exist as sets of resource keys (rather than having direct references to the
 * things they are tags of).
 * 
 * Furthermore, they must be resolvable before "normal" tags are currently
 * resolved at in order to be useful during biome modification, so
 * rather than refactor the perfectly-fine-as-is "regular tags", we create a
 * separate set of infrastructure here for managing resource key tags
 */
public class ResourceKeyTags implements IFutureReloadListener
{
    public static final ResourceKeyTags INSTANCE = new ResourceKeyTags();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<RegistryKey<? extends Registry<?>>,String> REGISTRY_DIRECTORIES = new HashMap<>();
    
    private Map<RegistryKey<? extends Registry<?>>, ITagCollection<? extends RegistryKey<?>>> data = new HashMap<>();
    private int generation = -1;

    /**
     * Makes a wrapper tag for a registry key tag. The wrapper tag will be sensitive to server data reloads.
     * Key tag wrappers can be created at any time.
     * 
     * Key tag wrappers become queryable once key tags are loaded prior to a server starting;
     * they continue to be available until the server stops.
     * 
     * If the specified tag ID is not available when the tag is queried, {@link IOptionalNamedTag#isDefaulted()} will return true.
     * If the tag is defaulted, the tag will be empty when queried.
     * 
     * @param <T> The type of the things the tag's registry keys are for
     * @param registryKey The key used to create the tag's registry keys. This also sets the name of the directory to load tags from.
     * @param tagID The id of the tag, defines the namespace and id of the json to load the tag from (can include subfolders)
     * @return A tag wrapper for a key tag that will be loaded from data/{tagID-namespace}/tags/resource_keys/{directory}/{tagID-path}.json
     * -- {directory} should be be the registry name but pluralized (e.g. the "biome" registry uses the "biomes" directory).* 
     * 
     * @apiNote Resource Key Tags are server-only resources and will not be queryable (isDefaulted returns true)
     * while a server is not running. Resource Key Tags are not currently synced to clients and should not be
     * queried for clientside purposes.
     */
    public static <T> IOptionalNamedTag<RegistryKey<T>> makeKeyTagWrapper(final RegistryKey<Registry<T>> registryKey, final ResourceLocation tagID)
    {
        return new ResourceKeyTags.KeyTag<>(registryKey, tagID);
    }
    
    /**
     * Notify the key tag manager that key tags tags should be loaded for a given registry.
     * e.g. Forge adds key tags for Level resource keys, using the "dimensions" directory;
     * these will be loaded from "tags/resource_keys/dimensions"
     * 
     * Must be called before the first load of key tags (mod constructor is the best time to call it).
     * Safe to call during parallel modloading.
     * 
     * @param registryKey The registry resource key to load resource key tags for
     * @param directoryName The directory to load resource key tags from. Should be plural.
     * Mods that add custom dynamic registries specific to their own mod should namespace this directory to avoid collisions
     * with other mods' directories, e.g. "jimscheesemod/cheeses" instead of just "cheeses"
     */
    public synchronized static void registerResourceKeyTagDirectory(final RegistryKey<? extends Registry<?>> registryKey, final String directoryName)
    {
        // mods registering tag directories should be logged for debug purposes
        final String modid = ModLoadingContext.get().getActiveContainer().getModId();
        final String previousDirectory = REGISTRY_DIRECTORIES.put(registryKey, directoryName);
        if (previousDirectory != null && !Objects.equals(directoryName, previousDirectory))
        {
            LOGGER.error("Mod {} registered resource key tag directory {} for registry key {}, overriding previous directory {}. This will probably break resource key tag loading.",
                modid, directoryName, registryKey.location(), previousDirectory);
        }
        else
        {
            LOGGER.debug("Mod {} registered resource key tag directory {} for registry key {}",
                modid, directoryName, registryKey.location());
        }
    }
    
    /**
     * Returns the tag directory registered to the given registry key.
     * @param registryKey The key to get the directory for
     * @return Returns null if the directory was not or has not been registered to the given registry key, otherwise returns the directory
     * 
     * @apiNote May not return valid data if modloading hasn't completed yet
     */
    public static @Nullable String getTagDirectory(final RegistryKey<? extends Registry<?>> registryKey)
    {
        return REGISTRY_DIRECTORIES.get(registryKey);
    }

    @Override
    public CompletableFuture<Void> reload(final IStage stage, IResourceManager manager, final IProfiler workerProfiler, final IProfiler mainProfiler, final Executor workerExecutor,
        final Executor mainExecutor)
    {
        // load tags for each dynamic registry
        // (we do this ensuring we don't classload the dynamic registries too early by getting the worldgen registries,
        // each of which the dynamic registries will make a copy of later)
        
        // we make a tag reader for each dynamic registry key,
        // each tag reader is able to produce a completablefuture that reads and merges tag jsons off-thread
        // when this concludes, the results of each of these futures can be fed back into the tag reader to produce the final tag collection
        final Map<RegistryKey<? extends Registry<?>>, Pair<TagCollectionReader<RegistryKey<?>>, CompletableFuture<Map<ResourceLocation,ITag.Builder>>>> futureTagBuilders = new HashMap<>();
        final List<CompletableFuture<Map<ResourceLocation,ITag.Builder>>> justTheFutures = new ArrayList<>();
        for (Map.Entry<RegistryKey<? extends Registry<?>>,String> entry : REGISTRY_DIRECTORIES.entrySet())
        {
            RegistryKey<? extends Registry<?>> registryKey = entry.getKey();
            String directoryName = entry.getValue();
            final Pair<TagCollectionReader<RegistryKey<?>>, CompletableFuture<Map<ResourceLocation, ITag.Builder>>> pair = getFutureTagBuilder(registryKey, directoryName, manager, workerExecutor);
            futureTagBuilders.put(registryKey, pair);
            justTheFutures.add(pair.getSecond());
        }
        return CompletableFuture.allOf(justTheFutures.toArray(new CompletableFuture[justTheFutures.size()]))
            .thenCompose(stage::wait) // wait for all jsons to be parsed, then finish building tags on main thread
            .thenAcceptAsync(voidArg -> this.finishLoadingOnMainThread(futureTagBuilders), mainExecutor);
    }
    
    private static Pair<TagCollectionReader<RegistryKey<?>>, CompletableFuture<Map<ResourceLocation, ITag.Builder>>> getFutureTagBuilder(final RegistryKey<? extends Registry<?>> registryKey, final String directoryName, final IResourceManager manager, final Executor workerExecutor)
    {
        @SuppressWarnings("unchecked") // cast makes javac happy, registrykey generics don't matter at runtime
        final Function<ResourceLocation, Optional<RegistryKey<?>>> tagEntryFactory =
            rl -> Optional.<RegistryKey<?>>of(RegistryKey.create((RegistryKey<? extends Registry<Object>>)registryKey, rl));
        final ResourceLocation registryName = registryKey.location();
        final String directory = "tags/resource_keys/" + REGISTRY_DIRECTORIES.computeIfAbsent(registryKey, key -> key.location().getPath() + "s");
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
     * Gets the current collection of tags for a given registry key
     * @param <T> The type of the thing the tags are for
     * @param registryKey The registry key for the collection of tags being requested
     * @return Collection of tags. Returns null if tags are not currently loaded or if tags are not being loaded for the given registry key.
     * 
     * @implNote hides an unchecked cast for convenience (registry keys' generics are safe to cast to whatever as keys are just a few strings)
     */
    public @Nullable <T> ITagCollection<RegistryKey<T>> getTagCollection(final RegistryKey<Registry<T>> registryKey)
    {
        return (ITagCollection<RegistryKey<T>>) this.data.get(registryKey);
    }
    
    /**
     * Updates the key tag registry
     * @param newTags new tags
     * 
     * @apiNote internal, called when tag registry is loaded/reloaded/discarded
     */
    public void updateTags(final Map<RegistryKey<? extends Registry<?>>, ITagCollection<? extends RegistryKey<?>>> newTags)
    {
        this.data = newTags;
        this.generation++;
        if (this.generation<0)
            this.generation=0;
    }

    private static class KeyTag<T> implements IOptionalNamedTag<RegistryKey<T>>
    {
        private final RegistryKey<Registry<T>> registryKey;
        private final ResourceLocation name;
        
        private int generation = Integer.MIN_VALUE;
        private boolean defaulted = true;
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

        @Override
        public boolean isDefaulted()
        {
            this.ensureProxyUpToDate();
            return this.defaulted;
        }
        
        private void ensureProxyUpToDate()
        {
            final int keyTagsGeneration = ResourceKeyTags.INSTANCE.generation;
            if (this.generation != keyTagsGeneration || this.proxy == null)
            {
                this.generation = keyTagsGeneration;
                final ITagCollection<RegistryKey<T>> tags = ResourceKeyTags.INSTANCE.getTagCollection(this.registryKey);
                this.defaulted = tags == null;
                this.proxy = this.defaulted ? Tag.empty() : tags.getTagOrEmpty(this.name);
            }
        }
        
    }
}
