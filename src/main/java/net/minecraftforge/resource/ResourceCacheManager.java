package net.minecraftforge.resource;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceCacheManager {

    private final boolean supportsReloading;
    private final boolean indexOffThread;
    private final BiFunction<PackType, String, Path> pathBuilder;
    private final Map<PackTypeAndNamespace, NamespacedResourceCacheManager> managersByNamespace = new ConcurrentHashMap<>();

    public ResourceCacheManager(final boolean supportsReloading, final boolean indexOffThread, final BiFunction<PackType, String, Path> pathBuilder) {
        this.supportsReloading = supportsReloading;
        this.indexOffThread = indexOffThread;
        this.pathBuilder = pathBuilder;
    }

    public void index(final String namespace) {
        for (final PackType packType : PackType.values()) {
            final PackTypeAndNamespace key = new PackTypeAndNamespace(packType, namespace);
            if (managersByNamespace.containsKey(key) && !supportsReloading) {
                return;
            }

            this.managersByNamespace.put(key, new NamespacedResourceCacheManager(packType, namespace, indexOffThread, pathBuilder, this::createWalkingStream));
            this.managersByNamespace.get(key).index();
        }
    }

    @SuppressWarnings("resource") //We close this automatically later on when the result is actually used. This is just a factory.
    private Stream<Path> createWalkingStream(final Path path) throws IOException {
        return Files.walk(path)
                .filter(resourcePath -> !resourcePath.toString().endsWith(".mcmeta"));
    }

    public Collection<ResourceLocation> getResources(final PackType type, final String resourceNamespace, final Path inputPath, final Predicate<ResourceLocation> filter) {
        final PackTypeAndNamespace key = new PackTypeAndNamespace(type, resourceNamespace);
        if (!managersByNamespace.containsKey(key)) {
            return Collections.emptyList();
        }

        return managersByNamespace.get(key).getResources(inputPath, filter);
    }

    public Set<String> getNamespaces(final PackType type) {
        return managersByNamespace.keySet().stream()
                .filter(key -> key.packType() == type)
                .map(PackTypeAndNamespace::namespace)
                .collect(Collectors.toSet());
    }

    public boolean hasCached(final PackType packType, final String namespace) {
        final PackTypeAndNamespace key = new PackTypeAndNamespace(packType, namespace);
        return managersByNamespace.containsKey(key) && managersByNamespace.get(key).cacheLoaded();
    }

    private record PackTypeAndNamespace(PackType packType, String namespace) {}

    private record ResourceCacheEntry(PackType packType, String namespace, Path path,
                                      ResourceLocation resourceLocation) {
    }

    private static class NamespacedResourceCacheManager {
        private static final Logger LOGGER = LogUtils.getLogger();

        private final PackType packType;
        private final String namespace;
        private final boolean indexOffThread;
        private final BiFunction<PackType, String, Path> pathBuilder;
        private final PathWalkerFactory pathFinder;

        private final Map<String, List<ResourceCacheEntry>> entriesByPathPrefix = Maps.newConcurrentMap();
        private final AtomicBoolean cacheLoaded = new AtomicBoolean(false);


        private NamespacedResourceCacheManager(final PackType packType, final String namespace, final boolean indexOffThread, BiFunction<PackType, String, Path> pathBuilder, final PathWalkerFactory pathFinder) {
            this.packType = packType;
            this.namespace = namespace;
            this.indexOffThread = indexOffThread;
            this.pathBuilder = pathBuilder;
            this.pathFinder = pathFinder;
        }

        public void index() {
            if (indexOffThread) {
                CompletableFuture.runAsync(
                        this::doIndex,
                        Util.backgroundExecutor()
                );
            }
            else  {
                doIndex();
            }
        }

        private void doIndex() {
            final Path rootPath = pathBuilder.apply(packType, namespace);
            try (final Stream<Path> paths = pathFinder.createWalkingStream(rootPath)) {
                paths
                        .parallel()
                        .map(rootPath::relativize)
                        .filter(path -> ResourceLocation.isValidPath(Joiner.on('/').join(path)))
                        .map(path -> new ResourceCacheEntry(packType, namespace, path, new ResourceLocation(namespace, Joiner.on('/').join(path))))
                        .forEach(this::injectIntoCache);
            } catch (NoSuchFileException noSuchFileException) {
                LOGGER.debug("Failed to cache resources, the directory does not exist!", noSuchFileException);
            } catch (IOException ioException) {
                LOGGER.error("Failed to cache resources, some stuff might be missing!", ioException);
            } catch (Exception exception) {
                LOGGER.error("Failed to cache resources, some stuff might be missing! Unknown exception!", exception);
            } finally {
                cacheLoaded.set(true);
            }
        }

        private void injectIntoCache(final ResourceCacheEntry entry) {
            injectIntoCache(entry.path().getParent(), entry);
        }

        private void injectIntoCache(@Nullable final Path parentPath, final ResourceCacheEntry entry) {
            String pathEntry;
            if (parentPath == null) {
                pathEntry = "";
            }
            else {
                pathEntry = Joiner.on("/").join(parentPath);
            }

            this.entriesByPathPrefix.putIfAbsent(pathEntry, new CopyOnWriteArrayList<>());
            this.entriesByPathPrefix.get(pathEntry).add(entry);

            if (parentPath != null && !pathEntry.isEmpty()) { //Recursively walk to the top, while preventing duplicate entries.
                this.injectIntoCache(parentPath.getParent(), entry);
            }
        }

        public Collection<ResourceLocation> getResources(final Path inputPath, final Predicate<ResourceLocation> filter) {
            final String pathEntry = Joiner.on('/').join(inputPath);
            if (!entriesByPathPrefix.containsKey(pathEntry)) {
                return Collections.emptyList();
            }

            return entriesByPathPrefix.get(pathEntry).stream()
                    .map(ResourceCacheEntry::resourceLocation)
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        public boolean cacheLoaded() {
            return cacheLoaded.get();
        }
    }

    @FunctionalInterface
    private interface PathWalkerFactory {
        Stream<Path> createWalkingStream(Path path) throws IOException;
    }
}
