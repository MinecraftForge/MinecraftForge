/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.resource;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cache manager for resources.
 * <p>
 * This class handles caching the resource listing calls on a pack, pack type and namespace level.
 */
public class ResourceCacheManager
{

    /**
     * Indicates if the underlying namespaced managers need to support reloading.
     * Disabled for the (in-jar / downloaded) default vanilla pack.
     */
    private final boolean supportsReloading;
    /**
     * Indicates if the indexing of the file-tree should happen off-thread or on-thread and block the process accordingly.
     */
    private final ForgeConfigSpec.BooleanValue indexOffThreadConfigOption;

    /**
     * The path builder (different users have different requirements for how they want to handle this)
     */
    private final BiFunction<PackType, String, Path> pathBuilder;
    /**
     * The individual sub managers by pack type and namespace
     */
    private final Map<PackTypeAndNamespace, NamespacedResourceCacheManager> managersByNamespace = Maps.newConcurrentMap();

    /**
     * Creates a new instance of a resource cache manager.
     *
     * @param supportsReloading    True to make the namespace specific managers support reloading the cache.
     * @param indexOffThreadConfig True to index the file-tree off-thread.
     * @param pathBuilder          The path builder to use.
     */
    public ResourceCacheManager(final boolean supportsReloading, final ForgeConfigSpec.BooleanValue indexOffThreadConfig, final BiFunction<PackType, String, Path> pathBuilder)
    {
        this.supportsReloading = supportsReloading;
        this.indexOffThreadConfigOption = indexOffThreadConfig;
        this.pathBuilder = pathBuilder;
    }

    public boolean shouldIndexOffThread()
    {
        return !getConfigValue(this.indexOffThreadConfigOption);
    }

    public static boolean shouldUseCache()
    {
        return getConfigValue(ForgeConfig.COMMON.cachePackAccess);
    }

    private static boolean getConfigValue(Supplier<Boolean> configValue)
    {
        // Yes we catch the early loading error on purpose.
        // Both the indexing off-thread and if caching is enabled are configurable, so we need to catch the error.
        // So when they are loaded early we preserve the default behaviour which is to not cache or index on thread.
        try
        {
            return configValue.get();
        } catch (IllegalStateException e)
        {
            return false;
        }
    }

    /**
     * Invoked to trigger indexing of a given namespaces resources.
     *
     * @param namespace The namespace to index.
     */
    public void index(final String namespace)
    {
        for (final PackType packType : PackType.values())
        {
            final PackTypeAndNamespace key = new PackTypeAndNamespace(packType, namespace); // Make a key.
            if (managersByNamespace.containsKey(key) && !supportsReloading)
            { // If we do not support reloading we just skip this.
                return;
            }

            // Create a new manager, overriding the previous one if it exists.
            final NamespacedResourceCacheManager newManager = new NamespacedResourceCacheManager(packType, namespace, this.shouldIndexOffThread(), pathBuilder, this::createWalkingStream);
            this.managersByNamespace.put(key, newManager);

            // Index the inner manager (which will for sure exist, and will know the pack type and namespace already)
            newManager.index();
        }
    }

    /**
     * Handling method to create a new stream of paths in the target directory, which can then be walked.
     * The caller needs to close the stream manually.
     *
     * @param path The path to walk.
     * @return A stream of paths which reside in the given path.
     * @throws IOException Thrown when the path does not exist or reading of path information failed.
     */
    @SuppressWarnings("resource")
    // We close this automatically later on when the result is actually used. This is just a factory.
    private Stream<Path> createWalkingStream(final Path path) throws IOException
    {
        return Files.walk(path)
                .filter(resourcePath -> !resourcePath.toString().endsWith(".mcmeta"));
    }

    /**
     * Returns the cached resources for the given pack type, namespace, path prefix and filter.
     *
     * @param type              The type of pack to look up in (data or resource)
     * @param resourceNamespace The namespace of the resources to look up.
     * @param inputPath         The input path prefix to check for.
     * @param filter            The ternary filter to apply.
     * @return A collection of resource locations which match the given filter, have the input path as prefix, are in the right pack type and have the given namespace as namespace.
     */
    public Collection<ResourceLocation> getResources(final PackType type, final String resourceNamespace, final Path inputPath, final Predicate<ResourceLocation> filter)
    {
        final PackTypeAndNamespace key = new PackTypeAndNamespace(type, resourceNamespace);
        final NamespacedResourceCacheManager manager = managersByNamespace.get(key);

        // Check if we even have a cached manager with the given namespace, else return an empty collection.
        if (manager == null)
        {
            return Collections.emptyList();
        }

        // Request the data from the manager.
        return manager.getResources(inputPath, filter);
    }

    /**
     * Looks up all already indexed namespaces.
     * Note even though a namespace might be returned from this method, it does not mean that the indexing has completed
     * on that namespace if it is running off-thread.
     *
     * @param type The type of the pack to get the namespaces for.
     * @return The namespaces which are known for the given pack type.
     */
    public Set<String> getNamespaces(final PackType type)
    {
        return managersByNamespace.keySet().stream()
                .filter(key -> key.packType() == type)
                .map(PackTypeAndNamespace::namespace)
                .collect(Collectors.toSet());
    }

    /**
     * Indicates if the given namespace is completely cached for the given pack type.
     *
     * @param packType  The pack type to check.
     * @param namespace The namespace to check.
     * @return True if the namespace is completely cached for the given pack type.
     */
    public boolean hasCached(final PackType packType, final String namespace)
    {
        final PackTypeAndNamespace key = new PackTypeAndNamespace(packType, namespace);
        final NamespacedResourceCacheManager manager = managersByNamespace.get(key);
        return manager != null && manager.cacheLoaded();
    }

    /**
     * Record for the delegated namespaced manager map.
     *
     * @param packType  The pack type.
     * @param namespace The namespace.
     */
    private record PackTypeAndNamespace(PackType packType, String namespace)
    {
    }

    /**
     * Record for the individual cache entries.
     *
     * @param packType         The pack type that the resource resides in.
     * @param namespace        The namespace that the resource resides in.
     * @param path             The path in the pack that the resource resides in.
     * @param resourceLocation The resource location for the resource.
     */
    private record ResourceCacheEntry(PackType packType, String namespace, Path path,
                                      ResourceLocation resourceLocation)
    {
    }

    /**
     * A namespaced and pack type specific cache which the {@link ResourceCacheManager} can delegate the handling to.
     */
    private static class NamespacedResourceCacheManager
    {
        private static final Logger LOGGER = LogUtils.getLogger();

        /**
         * The pack type this manager is responsible for.
         */
        private final PackType packType;
        /**
         * The namespace this manager is responsible for.
         */
        private final String namespace;
        /**
         * Indicates if the indexing should run off-thread.
         */
        private final boolean indexOffThread;
        /**
         * The path builder to use.
         */
        private final BiFunction<PackType, String, Path> pathBuilder;
        /**
         * The path walker stream factory to use.
         */
        private final PathWalkerFactory pathFinder;

        /**
         * The cache entries for this manager.
         */
        private final Map<String, List<ResourceCacheEntry>> entriesByPathPrefix = Maps.newConcurrentMap();
        /**
         * Indicates if the cache has been loaded successfully.
         */
        private final AtomicBoolean cacheLoaded = new AtomicBoolean(false);

        /**
         * Creates a new namespaced resource cache manager.
         *
         * @param packType       The pack type that this manager handles.
         * @param namespace      The namespace that this manager handles.
         * @param indexOffThread True to enable indexing off-thread.
         * @param pathBuilder    The path builder to use.
         * @param pathFinder     The path walker stream factory to use.
         */
        private NamespacedResourceCacheManager(final PackType packType, final String namespace, final boolean indexOffThread, BiFunction<PackType, String, Path> pathBuilder, final PathWalkerFactory pathFinder)
        {
            this.packType = packType;
            this.namespace = namespace;
            this.indexOffThread = indexOffThread;
            this.pathBuilder = pathBuilder;
            this.pathFinder = pathFinder;
        }

        /**
         * Triggers indexing of this manager.
         */
        public void index()
        {
            if (indexOffThread)
            {
                // Run off-thread.
                CompletableFuture.runAsync(
                        this::doIndex,
                        Util.backgroundExecutor()
                );
            } else
            {
                // Run on-thread
                doIndex();
            }
        }

        /**
         * Performs the actual indexing of this manager.
         * Can be invoked on- or off-thread.
         */
        private void doIndex()
        {
            // Get the path to the root of the namespace in the current pack.
            final Path rootPath = pathBuilder.apply(packType, namespace);

            // Stream element resource that combines a normalized path and a joined path using the "/" as separator.
            record PathWithLocationPath(Path path, String locationPath)
            {
            }

            // Build a walkable stream, process it
            try (final Stream<Path> paths = pathFinder.createWalkingStream(rootPath))
            {
                paths.parallel() // Run the stream in parallel
                        .map(rootPath::relativize) // Relative to the given root.
                        .map(path -> new PathWithLocationPath(path, Joiner.on('/').join(path))) // Create a common hierarchy.
                        .filter(path -> ResourceLocation.isValidPath(path.locationPath())) // Only process valid paths
                        .map(path -> new ResourceCacheEntry(packType, namespace, path.path(), new ResourceLocation(namespace, path.locationPath()))) // Create a cache entry.
                        .forEach(this::injectIntoCache); // Inject the entry into the cache.
            } catch (NoSuchFileException noSuchFileException)
            {
                LOGGER.debug("Failed to cache resources, the directory does not exist!", noSuchFileException);
            } catch (IOException ioException)
            {
                LOGGER.error("Failed to cache resources, some stuff might be missing!", ioException);
            } catch (Exception exception)
            {
                LOGGER.error("Failed to cache resources, some stuff might be missing! Unknown exception!", exception);
            } finally
            {
                cacheLoaded.set(true);
            }
        }

        /**
         * Injects the given cache entry into the cache.
         * By first injecting it into the parent of the entry (so the directory it resides in) and then recursively walking up.
         *
         * @param entry The entry to inject into the cache, must not be null.
         */
        private void injectIntoCache(final ResourceCacheEntry entry)
        {
            injectIntoCache(entry.path().getParent(), entry);
        }

        /**
         * Injects the given cache entry into the cache, recursively into all of its parent paths.
         *
         * @param parentPath The parent path to inject into the cache, can be null. Null will be treated as the root path.
         * @param entry      The entry to inject into the cache, must not be null.
         */
        private void injectIntoCache(@Nullable final Path parentPath, final ResourceCacheEntry entry)
        {
            String pathEntry;
            if (parentPath == null)
            {
                // We have null, generally this only happens on the root directory.
                pathEntry = "";
            } else
            {
                // Use the parent paths, we can not depend on a toString call here, since we need the / as separator.
                pathEntry = Joiner.on("/").join(parentPath);
            }

            // Inject into the cache, we can use a normal array list here since we have guarded against cache access later on.
            this.entriesByPathPrefix.computeIfAbsent(pathEntry, e -> new CopyOnWriteArrayList<>()).add(entry);

            // Recursively walk to the top, while preventing duplicate entries.
            if (parentPath != null && !pathEntry.isEmpty())
            {
                this.injectIntoCache(parentPath.getParent(), entry);
            }
        }

        /**
         * Looks up resources in the given input path prefix as well as those that match the filter.
         *
         * @param inputPath The input path prefix to look in.
         * @param filter    The filter which the resources must match.
         * @return A collection of resource location which match the given path prefix and who match the filter.
         */
        public Collection<ResourceLocation> getResources(final Path inputPath, final Predicate<ResourceLocation> filter)
        {
            // We only have a cache once the atomic flag has been set.
            if (!cacheLoaded()) {
                // We need to return a mutable object here because the callers actually mutate the collection.
                return new ArrayList<>();
            }

            // Use the input path prefix, we can not depend on a toString call here, since we need the / as separator.
            final String pathEntry = Joiner.on('/').join(inputPath);

            // Since we inject into the cache recursively we can now just grab the map entry that is the prefix and loop over its values.
            // We use getOrDefault with a stream combination since the returned list needs to be mutable.
            return entriesByPathPrefix.getOrDefault(pathEntry, Collections.emptyList()).stream()
                    .map(ResourceCacheEntry::resourceLocation)
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        /**
         * Indicates if the cache of this manager has been loaded.
         *
         * @return True if the cache has been loaded, false otherwise.
         */
        public boolean cacheLoaded()
        {
            return cacheLoaded.get();
        }
    }

    /**
     * Functional callback interface to get a walkable stream of paths.
     * Supports throwing {@link IOException} if the stream can not be created.
     */
    @FunctionalInterface
    private interface PathWalkerFactory
    {
        Stream<Path> createWalkingStream(Path path) throws IOException;
    }
}
