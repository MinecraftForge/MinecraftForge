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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.SetTag;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
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
public class ResourceKeyTags implements PreparableReloadListener
{
    public static final ResourceKeyTags INSTANCE = new ResourceKeyTags();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceKey<? extends Registry<?>>,String> REGISTRY_DIRECTORIES = new HashMap<>();
    
    private Map<ResourceKey<? extends Registry<?>>, TagCollection<? extends ResourceKey<?>>> data = new HashMap<>();
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
     * Resource Key Tags are server-only resources and will not be queryable (isDefaulted returns true)
     * while a server is not running. Resource Key Tags are not currently synced to clients and should not be
     * queried for clientside purposes.
     * 
     * @param <T> The type of the things the tag's registry keys are for
     * @param registryKey The key used to create the tag's registry keys. This also sets the name of the directory to load tags from.
     * @param tagID The id of the tag, defines the namespace and id of the json to load the tag from (can include subfolders)
     * @return A tag wrapper for a key tag that will be loaded from data/{tagID-namespace}/tags/resource_keys/{directory}/{tagID-path}.json
     * -- {directory} should be be the registry name but pluralized (e.g. the "biome" registry uses the "biomes" directory).
     */
    public static <T> IOptionalNamedTag<ResourceKey<T>> makeKeyTagWrapper(final ResourceKey<Registry<T>> registryKey, final ResourceLocation tagID)
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
    public synchronized static void registerResourceKeyTagDirectory(final ResourceKey<? extends Registry<?>> registryKey, final String directoryName)
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
     * May not return valid data if modloading hasn't completed yet/
     * @param registryKey The key to get the directory for
     * @return Returns null if the directory was not or has not been registered to the given registry key, otherwise returns the directory
     */
    public static @Nullable String getTagDirectory(final ResourceKey<? extends Registry<?>> registryKey)
    {
        return REGISTRY_DIRECTORIES.get(registryKey);
    }

    @Override
    public CompletableFuture<Void> reload(final PreparationBarrier stage, ResourceManager manager, final ProfilerFiller workerProfiler, final ProfilerFiller mainProfiler, final Executor workerExecutor,
        final Executor mainExecutor)
    {
        // we make a tag reader for each directory we want to load tags from,
        // each tag reader is able to produce a completablefuture that reads and merges tag jsons off-thread
        // when this concludes, the results of each of these futures can be fed back into the tag reader to produce the final tag collection
        final Map<ResourceKey<? extends Registry<?>>, CompletableFuture<? extends TagCollection<ResourceKey<?>>>> futureTagBuilders = new HashMap<>();
        for (Map.Entry<ResourceKey<? extends Registry<?>>,String> entry : REGISTRY_DIRECTORIES.entrySet())
        {
            final ResourceKey<? extends Registry<?>> registryKey = entry.getKey();
            final String directoryName = entry.getValue();
            @SuppressWarnings("unchecked") // cast makes javac happy, registrykey generics don't matter at runtime
            final Function<ResourceLocation, Optional<ResourceKey<?>>> tagEntryFactory =
                rl -> Optional.<ResourceKey<?>>of(ResourceKey.create((ResourceKey<? extends Registry<Object>>)registryKey, rl));
            final String directory = "tags/resource_keys/" + directoryName;
            final TagLoader<ResourceKey<?>> reader = new TagLoader<>(tagEntryFactory, directory);
            final CompletableFuture<? extends TagCollection<ResourceKey<?>>> future = CompletableFuture.supplyAsync(() ->reader.loadAndBuild(manager), workerExecutor);
            futureTagBuilders.put(registryKey, future);
        }
        Collection<CompletableFuture<? extends TagCollection<ResourceKey<?>>>> justTheFutures = futureTagBuilders.values();
        return CompletableFuture.allOf(justTheFutures.toArray(new CompletableFuture[justTheFutures.size()]))
            .thenCompose(stage::wait) // wait for all jsons to be parsed, then finish building tags on main thread
            .thenAcceptAsync(voidArg -> 
            {   // finalize tag registry on main thread after completeable futures complete
                ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, TagCollection<? extends ResourceKey<?>>> mapBuilder = ImmutableMap.builder();
                futureTagBuilders.forEach((key,completedParseData) -> mapBuilder.put(key, completedParseData.join()));
                this.updateTags(mapBuilder.build());
            }, mainExecutor);
    }
    
    /**
     * Gets the current collection of tags for a given registry key
     * @param <T> The type of the thing the tags are for
     * @param registryKey The registry key for the collection of tags being requested
     * @return Collection of tags. Returns null if tags are not currently loaded or if tags are not being loaded for the given registry key.
     * 
     * @implNote hides an unchecked cast for convenience (registry keys' generics are safe to cast to whatever as keys are just a few strings)
     */
    @SuppressWarnings("unchecked")
    public @Nullable <T> TagCollection<ResourceKey<T>> getTagCollection(final ResourceKey<Registry<T>> registryKey)
    {
        return (TagCollection<ResourceKey<T>>) this.data.get(registryKey);
    }
    
    /**
     * Updates the key tag registry.
     * 
     * @deprecated internal, called when tag registry is loaded/reloaded/discarded
     * @param newTags new tags
     */
    @Deprecated
    public void updateTags(final Map<ResourceKey<? extends Registry<?>>, TagCollection<? extends ResourceKey<?>>> newTags)
    {
        this.data = newTags;
        this.generation++;
        if (this.generation<0)
            this.generation=0;
    }

    private static class KeyTag<T> implements IOptionalNamedTag<ResourceKey<T>>
    {
        private final ResourceKey<Registry<T>> registryKey;
        private final ResourceLocation name;
        
        private int generation = Integer.MIN_VALUE;
        private boolean defaulted = true;
        @Nullable private Tag<ResourceKey<T>> proxy = null;
        
        private KeyTag(ResourceKey<Registry<T>> registryKey, ResourceLocation name)
        {
            this.registryKey = registryKey;
            this.name = name;
        }

        @Override
        public boolean contains(final ResourceKey<T> key)
        {
            this.ensureProxyUpToDate();
            return this.proxy.contains(key);
        }

        @Override
        public List<ResourceKey<T>> getValues()
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
                final TagCollection<ResourceKey<T>> tags = ResourceKeyTags.INSTANCE.getTagCollection(this.registryKey);
                this.defaulted = tags == null;
                this.proxy = this.defaulted ? SetTag.empty() : tags.getTagOrEmpty(this.name);
            }
        }
        
    }
}
