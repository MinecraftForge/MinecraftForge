/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.resource;

import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cache manager for resources.
 * <p>
 * This class handles caching the resource listing calls on a pack, pack type and namespace level.
 */
@ApiStatus.Internal
public class ResourceCacheManager
{
    /**
     * Logging marker to handle the hiding of the entries from the debug file.
     */
    private static final Marker RESOURCE_CACHE = MarkerFactory.getMarker("RESOURCE-CACHE");

    /**
     * Indicates if the underlying namespaced managers need to support reloading.
     * Disabled for the (in-jar / downloaded) default vanilla pack.
     */
    private final boolean supportsReloading;

    /**
     * The config key which can be used to check if the indexing of the file-tree should happen off-thread or on-thread and block the process accordingly.
     */
    private final String indexOnThreadConfigurationKey;

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
    @Deprecated(since = "1.19.2", forRemoval = true)
    public ResourceCacheManager(final boolean supportsReloading, final ForgeConfigSpec.BooleanValue indexOffThreadConfig, final BiFunction<PackType, String, Path> pathBuilder)
    {
        this.supportsReloading = supportsReloading;
        this.indexOnThreadConfigurationKey = Iterators.getLast(indexOffThreadConfig.getPath().iterator());
        this.pathBuilder = pathBuilder;
    }

    public ResourceCacheManager(final boolean supportsReloading, final String indexOnThreadConfigurationKey, final BiFunction<PackType, String, Path> pathBuilder)
    {
        this.supportsReloading = supportsReloading;
        this.indexOnThreadConfigurationKey = indexOnThreadConfigurationKey;
        this.pathBuilder = pathBuilder;
    }

    /**
     * Indicates if the caching system is enabled or not.
     *
     * @return {@code true} if the caching system is enabled, {@code false} otherwise.
     */
    public static boolean shouldUseCache()
    {
        return ResourceManagerBootCacheConfigurationHandler.getInstance().getConfigValue("cachePackAccess", true);
    }

    /**
     * Indicates if this cache manager requires the indexing of the file-tree to happen on the main thread.
     *
     * @return {@code true} if the indexing of the file-tree should happen on the main thread, {@code false} otherwise.
     */
    public boolean shouldIndexOnThread()
    {
        return ResourceManagerBootCacheConfigurationHandler.getInstance().getConfigValue(this.indexOnThreadConfigurationKey, false);
    }

    /**
     * Indicates if this cache manager requires the indexing of the file-tree to happen off the main thread.
     *
     * @return {@code true} if the indexing of the file-tree should happen off the main thread, {@code true} otherwise.
     */
    @Deprecated(forRemoval = true, since = "1.19.2")
    public boolean shouldIndexOffThread()
    {
        return !this.shouldIndexOnThread();
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
            if (managersByNamespace.containsKey(key) && !supportsReloading) // If we do not support reloading we just skip this.
            {
                return;
            }

            // Create a new manager, overriding the previous one if it exists.
            final NamespacedResourceCacheManager newManager = new NamespacedResourceCacheManager(packType, namespace, this.shouldIndexOnThread(), pathBuilder, this::createWalkingStream);
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
         * Indicates if the indexing should run on-thread.
         */
        private final boolean indexOnThread;
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
         * @param packType      The pack type that this manager handles.
         * @param namespace     The namespace that this manager handles.
         * @param indexOnThread True to enable indexing on-thread.
         * @param pathBuilder   The path builder to use.
         * @param pathFinder    The path walker stream factory to use.
         */
        private NamespacedResourceCacheManager(final PackType packType, final String namespace, final boolean indexOnThread, BiFunction<PackType, String, Path> pathBuilder, final PathWalkerFactory pathFinder)
        {
            this.packType = packType;
            this.namespace = namespace;
            this.indexOnThread = indexOnThread;
            this.pathBuilder = pathBuilder;
            this.pathFinder = pathFinder;
        }

        /**
         * Triggers indexing of this manager.
         */
        public void index()
        {
            if (indexOnThread)
            {
                // Run on-thread
                doIndex();
            }
            else
            {
                // Run off-thread.
                CompletableFuture.runAsync(
                        this::doIndex,
                        Util.backgroundExecutor()
                );
            }
        }

        /**
         * Performs the actual indexing of this manager.
         * Can be invoked on- or off-thread.
         */
        private void doIndex()
        {
            LOGGER.debug(RESOURCE_CACHE, "Indexing resources for pack type {} and namespace {}, on thread: {}", packType, namespace, Thread.currentThread().getName());

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
            }
            catch (NoSuchFileException noSuchFileException)
            {
                LOGGER.debug(RESOURCE_CACHE, "Failed to cache resources, the directory does not exist!", noSuchFileException);
            }
            catch (IOException ioException)
            {
                LOGGER.error(RESOURCE_CACHE, "Failed to cache resources, some stuff might be missing!", ioException);
            }
            catch (Exception exception)
            {
                LOGGER.error(RESOURCE_CACHE, "Failed to cache resources, some stuff might be missing! Unknown exception!", exception);
            }
            finally
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
            }
            else
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
            if (!cacheLoaded())
            {
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
        /**
         * Create a new walkable stream of paths.
         * The stream will be closed by the caller.
         *
         * @param path The path to create the stream for.
         * @return A new stream of paths that are children (potentially several generations deep) of the given path.
         * @throws IOException If the stream can not be created.
         */
        Stream<Path> createWalkingStream(Path path) throws IOException;
    }

    /**
     * Class to handle the reading, initial creation, and watching of the boot configuration file, for the resource cache manager.
     */
    private static final class ResourceManagerBootCacheConfigurationHandler
    {
        /**
         * The logger for the {@link ResourceManagerBootCacheConfigurationHandler}
         */
        private static final Logger LOGGER = LogUtils.getLogger();

        /**
         * The path to the boot configuration file.
         */
        private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("forge-resource-caching.toml");

        /**
         * The configuration specification for the resource cache configuration.
         */
        private static final ConfigSpec configSpec = new ConfigSpec();
        static {
            configSpec.define("cacheResources", Boolean.TRUE);
            configSpec.define("indexVanillaPackCachesOnThread", Boolean.FALSE);
            configSpec.define("indexModPackCachesOnThread", Boolean.FALSE);
        }

        /**
         * The current instance of the handler.
         * @implNote This needs to be the last static field (or at least below the config spec and the config path, else some of them create NPEs because they are not initialized yet)
         */
        private static final ResourceManagerBootCacheConfigurationHandler INSTANCE = new ResourceManagerBootCacheConfigurationHandler();

        /**
         * The configuration handle for the resource cache configuration.
         */
        private final CommentedFileConfig configurationHandle;

        /**
         * Creates a new instance of the handler.
         * Registers the watchdog thread.
         */
        private ResourceManagerBootCacheConfigurationHandler()
        {
            this.configurationHandle = createConfiguration();
        }

        /**
         * Creates the configuration handle for the resource cache configuration.
         */
        private static CommentedFileConfig createConfiguration()
        {
            final CommentedFileConfig configData = CommentedFileConfig.builder(CONFIG_PATH).sync()
                    .onFileNotFound(ResourceManagerBootCacheConfigurationHandler::onConfigFileNotFound)
                    .autosave()
                    .autoreload()
                    .concurrent()
                    .writingMode(WritingMode.REPLACE)
                    .build();
            try
            {
                configData.load();
            }
            catch (ParsingException e)
            {
                throw new RuntimeException("Failed to load Force Resource Cache Configuration from %s".formatted(CONFIG_PATH), e);
            }
            if (!configSpec.isCorrect(configData)) {
                LOGGER.warn("Configuration file {} is not correct. Correcting", CONFIG_PATH);
                configSpec.correct(configData, (action, path, incorrectValue, correctedValue) ->
                        LOGGER.warn("Incorrect key {} was corrected from {} to {}", path, incorrectValue, correctedValue));
            }

            configData.save();

            return configData;
        }

        private static boolean onConfigFileNotFound(Path file, ConfigFormat<?> configFormat) throws IOException {
            Files.write(file, ImmutableList.of(
                            "# This TOML configuration file controls the resource caching system which is used before the mod loading environment starts.",
                            "# This file is read by the Forge boot loader, and is not used by the game itself.",
                            "#",
                            "# Set this to false to disable the resource cache. This will cause the game to scan the resource packs everytime it needs a list of resources.",
                            "cacheResources=true",
                            "",
                            "# Set this to true to force the caching of vanilla resources to happen on the main thread.",
                            "indexVanillaPackCachesOnThread=false",
                            "",
                            "# Set this to true to force the caching of mod resources to happen on the main thread.",
                            "indexModPackCachesOnThread=false"
                    ),
                    StandardOpenOption.CREATE_NEW);

            return true;
        }

        /**
         * Gives access to the current singleton instance of the handler.
         *
         * @return The instance of this handler.
         */
        public static ResourceManagerBootCacheConfigurationHandler getInstance()
        {
            return INSTANCE;
        }

        /**
         * Reads a config value from the current configuration file.
         *
         * @param configKey    The key of the config value to read.
         * @param defaultValue The default value to return if the config value is not present.
         * @return The value of the config value, or the default value if it is not present.
         */
        private boolean getConfigValue(final String configKey, final boolean defaultValue)
        {
            return this.configurationHandle.<Boolean>getOptional(configKey).orElse(defaultValue);
        }
    }
}
