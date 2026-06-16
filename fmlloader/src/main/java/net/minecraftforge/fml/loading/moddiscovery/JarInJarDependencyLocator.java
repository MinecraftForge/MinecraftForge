/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.mojang.logging.LogUtils;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.TypesafeMap;
import net.minecraftforge.fml.loading.EarlyLoadingException;
import net.minecraftforge.fml.loading.EarlyLoadingException.ExceptionData;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModFile.Type;
import net.minecraftforge.jarjar.metadata.ContainedJarIdentifier;
import net.minecraftforge.jarjar.metadata.ContainedJarMetadata;
import net.minecraftforge.jarjar.metadata.ContainedVersion;
import net.minecraftforge.jarjar.metadata.MetadataIOHandler;
import net.minecraftforge.jarjar.selection.JarSelector;

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.module.ModuleDescriptor;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class JarInJarDependencyLocator extends AbstractModProvider implements IDependencyLocator {
    private static final String ROIMFS = "roimfs";
    private static final String COLOR_CODE = "\u00a7";
    private static final String RESET  = COLOR_CODE + "r";
    private static final String YELLOW = COLOR_CODE + "e";
    private static final String RED    = COLOR_CODE + "4";
    private static final String GREEN  = COLOR_CODE + "2";

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Marker MARKER = MarkerFactory.getMarker("JAR-JAR"); // LogMarkers.SCAN;

    private static final Map<Path, Option> OPTIONS = new HashMap<>();
    private static volatile boolean optionsLoaded = false;

    private static final Attributes.Name MIXIN_CONFIGS_ATTR = new Attributes.Name("MixinConfigs");
    private static final Attributes.Name MIXIN_CONNECTOR_ATTR = new Attributes.Name("MixinConnector");
    private static final ContainedJarMetadata MIXIN_EXTRAS_DEPENDENCY = makeMixinExtraDependency();

    private static ContainedJarMetadata makeMixinExtraDependency() {
        VersionRange range = null;
        try {
            range = VersionRange.createFromVersionSpec("[0,)");
        } catch (InvalidVersionSpecificationException e) {
            throw new RuntimeException("Failed to create version range for mixinextras-forge dependency", e);
        }

        return new ContainedJarMetadata(
            new ContainedJarIdentifier("io.github.llamalad7", "mixinextras-forge"),
            new ContainedVersion(range, null),
            null, false
        );
    }

    @Override
    public String name() {
        return "JarInJar";
    }

    private int fsID = 0;
    private synchronized int nextId() {
        return fsID++;
    }

    @Override
    public List<IModFile> scanMods(Iterable<IModFile> loadedMods) {
        var mods = new ArrayList<IModFile>();
        loadedMods.forEach(mods::add);

        var selector = new Selector(mods);

        // Add a synthetic Mixin-Extras dependency if we detect anyone using Mixin
        if (anyMixinsLoaded(mods)) {
            if (!selector.isRequired(MIXIN_EXTRAS_DEPENDENCY.identifier()))
                selector.addRequirement(MIXIN_EXTRAS_DEPENDENCY);
        } else if (selector.entries.size() == mods.size()) {
            LOGGER.info("No dependencies to load found. Skipping!");
            return Collections.emptyList();
        }

        loadOptions();

        // Ideally, the json would be in order so we only have to loop once. But its written this way just in case.
        var seen = new HashSet<Option>();
        var added = false;
        do {
            added = false;
            for (var entry : OPTIONS.values()) {
                if (seen.contains(entry) || !selector.isRequired(entry.meta.identifier()))
                    continue;

                seen.add(entry);

                entry.addTo(selector);

                if (entry.deps != null && !entry.deps.isEmpty()) {
                    added = true;
                    for (var dep : entry.deps)
                        selector.addRequirement(dep);
                }
            }
        } while (added);

        var selected = selector.select();

        if (LOGGER.isDebugEnabled()) {
            var ids = new TreeMap<String, List<Entry>>();
            for (var entry : selector.entries.values()) {
                if (entry != FAILED && entry.coord != null)
                    ids.computeIfAbsent(entry.coord, _ -> new ArrayList<>()).add(entry);
            }

            for (var entry : ids.entrySet()) {
                LOGGER.info(MARKER, "JarJar Candidated for {}", entry.getKey());
                Collections.sort(entry.getValue());
                for (var option : entry.getValue())
                    LOGGER.info(MARKER, "\t{}{}", selected.contains(option) ? '*' : ' ', option);
            }
        }

        if (selected.isEmpty()) {
            LOGGER.info("No dependencies to load found. Skipping!");
            return Collections.emptyList();
        }

        /* Since Java 11, the JRE decompresses all zip files to memory when getting a ByteChannel for them.
         * JarJar only targets Minecraft 1.18+ which is Java17+ Which means we can exploit the fact that
         * these are Disjointed file systems, and close all unselected libraries.
         *
         * https://github.com/openjdk/jdk11u/blob/25a9b9e5594e88c9e1e66f95c7ca45d5a9da8854/src/jdk.zipfs/share/classes/jdk/nio/zipfs/ZipFileSystem.java#L703
         *
         * Combined with the fact that the JarJarFileSystem has always simply called into FileSystems.newFileSystem for its wrapped targets.
         * https://github.com/MinecraftForge/JarJar/blame/b39b2051439c81519d826d38898778bf3081356f/src/main/java/net/minecraftforge/jarjar/nio/pathfs/PathFileSystem.java#L38
         *
         * This effectively means that JarJar has ALWAYS been a wrapper around in-memory copies of every jar it scans.
         *
         * It also never closes these FileSystems, which potentially means they are leaked if the GC can't inherently GC them.
         *
         * Ideally we would extract these contained jars to a temp folder, so that we can use the nice optimizations Java has for ZipFile/JarFile.
         * But in order to do that we would need to write the file to a place that could potentially have conflicts.
         * To resolve those conflicts we should take a hint out of maven/gradle/minecraft's book and key the filepath based on a hash of the file being extracted.
         * However, because that hash is no available in existing metadata we would need to re-calculate it.
         *
         * To do it in an efficient way we I needed to be able to share the single opened FileSystem with both the SecureJar implementation, and with our recursive search.
         * So I use my new ReadOnlyInMemoryFileSystem which is specifically designed to use the same backing array for the JarInJar search as it does with the resulting dependency
         * selected SecureJars.
         *
         * This means that if we wanted to we could rather efficiently calculate a hash for extracting the file. However, it also means that we don't have to extract to file to
         * support ZipFileSystem caching properly. So as long as we are okay with not using the JRE's optimized ZipFile implementation, or we want to support legacy JarInJar metadata,
         * using this efficient in memory file system is the best option performance wise. Extracting the files during launch without an efficient caching/unique identifier system
         * would incur a lot of Disc IO which can be super slow depending on your setup.
         *
         * So, it is a stated goal of our future re-write to extract files into a cache directory to gain the benefits of JRE optimizations, using a FileSystem much simpler then
         * the existing JarInJarFileSystem is good enough.
         */

        // Lets close all unselected FileSystems
        var wasSelected = new HashSet<>(selected);
        for (var entry : selector.entries.values())
            entry.cleanup(wasSelected.contains(entry));

        LOGGER.info(MARKER, "Found {} dependencies adding them to mods collection", selected.size());
        var ret = new ArrayList<IModFile>(selected.size());

        // Now lets build the actual mod files
        for (var entry : selected) {
            try {
                var root = entry.getFinalPath();
                var mod = createMod(root, false, entry.type.name());

                if (mod.ex() != null)
                    throw fail("Failed to load JarInJar file " + entry, mod.ex());

                ret.add(mod.file());
            } catch (EarlyLoadingException e) {
                throw e;
            } catch (Throwable e) {
                throw fail("Failed to load JarInJar file " + entry, e);
            }
        }

        return ret;
    }

    private static Boolean anyMixinsLoaded = null;
    private static boolean anyMixinsLoaded(List<IModFile> mods) {
        var ret = anyMixinsLoaded;
        if (ret != null) return ret;

        if (!FMLEnvironment.production
                && Launcher.INSTANCE.blackboard().get(TypesafeMap.Key.getOrCreate(Launcher.INSTANCE.blackboard(), "isMixinEnabledInDev", Boolean.class)).orElse(false)) {
            anyMixinsLoaded = true;
            return true;
        }

        for (var mod : mods) {
            var attributes = mod.getSecureJar().moduleDataProvider().getManifest().getMainAttributes();
            if (attributes.getValue(MIXIN_CONFIGS_ATTR) != null || attributes.getValue(MIXIN_CONNECTOR_ATTR) != null) {
                anyMixinsLoaded = true;
                return true;
            }
        }

        anyMixinsLoaded = false;
        return false;
    }

    private record Options(List<OptionMetadata> options) {}
    private record OptionMetadata(String resource, Type layer, String id, List<ContainedJarMetadata> deps, ContainedJarMetadata meta, boolean nested) {}

    private static void loadOptions() {
        if (optionsLoaded)
            return;

        synchronized (OPTIONS) {
            if (optionsLoaded)
                return;

            optionsLoaded = true;

            var self = JarInJarDependencyLocator.class;

            try (var stream = self.getModule().getResourceAsStream("/jarjar_options.json")) {
                if (stream == null) {
                    LOGGER.error(MARKER, "Failed to find jarjar_options.json");
                    return;
                }

                var meta = MetadataIOHandler.getGson().fromJson(new InputStreamReader(stream), Options.class);
                //var meta = MetadataIOHandler.fromStream(stream).orElse(null);
                if (meta == null) {
                    LOGGER.error(MARKER, "Corrupted jarjar_options.json");
                    return;
                }

                for (var jar : meta.options()) {
                    Path path = ClasspathLocator.getPathFromResource(self.getClassLoader(), jar.resource());
                    if (path == null) {
                        LOGGER.error(MARKER, "Failed to find JarJar option for {}", jar.resource());
                        continue;
                    }

                    LOGGER.debug(MARKER, "Found JarJar Option: {}", path.toAbsolutePath());
                    if (jar.nested())
                        OPTIONS.put(path, new NestedOption(jar.id(), path, jar.layer(), jar.meta(), jar.deps()));
                    else
                        OPTIONS.put(path, new Option(jar.id(), path, jar.layer(), jar.meta(), jar.deps()));
                }
            } catch (Throwable e) {
                LOGGER.error(MARKER, "Failed to read JarJar Options file", e);
            }
        }
    }

    static boolean isOption(Path path) {
        loadOptions();
        return OPTIONS.containsKey(path);
    }

    private static EarlyLoadingException fail(String message, @Nullable Throwable cause) {
        LOGGER.error(MARKER, message);
        return new EarlyLoadingException(message, cause, Collections.emptyList());
    }

    @Override
    protected String getDefaultJarModType() {
        return IModFile.Type.GAMELIBRARY.name();
    }

    protected static String identifyMod(IModFile modFile) {
        if (modFile.getModFileInfo() != null && !modFile.getModInfos().isEmpty())
            return modFile.getModInfos().stream().map(IModInfo::getModId).collect(Collectors.joining());

        var module = modFile.getSecureJar().moduleDataProvider().name();
        if (module != null && !module.isEmpty())
            return module;

        return modFile.getFileName();
    }

    private static final Entry FAILED = new Entry("FAILED ENTRY", Path.of(""), Type.LIBRARY, null);
    private static sealed class Entry implements Comparable<Entry> {
        final String id;
        final Path path;
        final Type type;
        final @Nullable String coord;

        private Entry(String id, Path path, Type type, @Nullable String coord) {
            this.id = id;
            this.path = path;
            this.type = type;
            this.coord = coord;
        }

        Path getFinalPath() {
            return path;
        }

        void cleanup(boolean selected) {
        }

        @Override
        public String toString() {
            if (path.getFileName() == null)
                return path.toUri().toString();
            return path.getFileName().toString();
        }

        @Nullable
        Path getResource(String path) {
            return null;
        }

        @Override
        public int compareTo(Entry o) {
            return o.toString().compareTo(toString());
        }
    }

    private static final class ModEntry extends Entry {
        private final IModFile mod;

        ModEntry(IModFile mod) {
            super(identifyMod(mod), mod.getFilePath(), mod.getType(), null);
            this.mod = mod;
        }

        @Override
        @Nullable
        Path getResource(String path) {
            return mod.findResource(path);
        }
    }

    private static sealed class Option extends Entry {
        final ContainedJarMetadata meta;
        final List<ContainedJarMetadata> deps;

        private Option(String id, Path path, Type type, ContainedJarMetadata meta, List<ContainedJarMetadata> deps) {
            super(id, path, type, meta.identifier().group() + ':' + meta.identifier().artifact());
            this.meta = meta;
            this.deps = deps;
        }

        public void addTo(Selector selector) {
            selector.option(this, meta);
        }
    }

    private static final class NestedOption extends Option {
        private FileSystem zip;
        private Path root;

        private NestedOption(String id, Path path, Type type, ContainedJarMetadata meta, List<ContainedJarMetadata> deps) {
            super(id, path, type, meta, deps);
        }

        @Override
        public void addTo(Selector selector) {
            super.addTo(selector);
            selector.add(this);
        }

        @Override
        void cleanup(boolean selected) {
            if (this.zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                    LOGGER.error(MARKER, "Failed to close unselected FileSystem {}", this, e);
                }
            }
        }

        @Nullable
        Path getResource(String path) {
            if (zip == null) {
                try {
                    this.zip = FileSystems.newFileSystem(this.path);
                    this.root = zip.getRootDirectories().iterator().next();
                } catch (IOException e) {
                    LOGGER.error(MARKER, "Failed to open FileSystem for option root {}", this, e);
                    return null;
                }
            }

            var target = root.resolve(path);
            if (Files.exists(target))
                return target;

            return null;
        }
    }


    private static final class Nested extends Entry {
        private final Entry parent;
        private final String nestedPath;
        private final Path zipPath;

        Nested(String id, Path path, Type type, String coords, Entry parent, String nestedPath, Path zipPath) {
            super(id, path, type, coords);
            this.parent = parent;
            this.nestedPath = nestedPath;
            this.zipPath = zipPath;
        }

        /*
         * Due to a bug in Mixin, anything that is not on the DefaultFileSystem MUST be a directory.
         * Or else it can't resolve resources correctly. So for anything we put on the roimfs we need
         * to return the nested zip file system.
         *
         * This will be an annoying to backport, but we can cross that bridge when we come to it.
         *
         * See: https://github.com/SpongePowered/Mixin/blob/4053421aa10aaac6127d969028a29c94fe3054f6/src/main/java/org/spongepowered/asm/launch/platform/MainAttributes.java#L102
         */
        @Override
        public Path getFinalPath() {
            return zipPath;
        }

        @Override
        void cleanup(boolean selected) {
            if (selected)
                return;

            LOGGER.info(MARKER, "Closeing unselected FileSystem {}", this);
            try {
                zipPath.getFileSystem().close();
                path.getFileSystem().close();
            } catch (IOException e) {
                LOGGER.error(MARKER, "Failed to close unselected FileSystem {}", this, e);
            }
        }

        @Override
        @Nullable
        Path getResource(String path) {
            return zipPath.resolve(path);
        }

        @Override
        public String toString() {
            return parent + "!/" + nestedPath;
        }
    }

    private record Key(Entry parent, String path) {}

    private final class Selector extends JarSelector<Entry> {
        private final HashMap<Key, Entry> entries = new HashMap<>();
        private final HashMap<Entry, HashMap<String, String>> children = new HashMap<>();

        private Selector(Iterable<IModFile> mods) {
            for (var mod : mods)
                this.force(new ModEntry(mod));
        }

        @Override
        public void force(Entry entry) {
            super.force(entry);
            entries.put(new Key(entry, ""), entry);
        }

        @Override
        public void option(Entry entry, ContainedJarMetadata meta) {
            super.option(entry, meta);
            entries.put(new Key(entry, ""), entry);
        }

        @Override
        public void add(Entry entry) {
            super.add(entry);
            entries.put(new Key(entry, ""), entry);
        }

        @Override
        @Nullable
        protected InputStream getResource(Entry source, String path) {
            try {
                Path target = source.getResource(path);
                if (target == null || !Files.exists(target)) {
                    LOGGER.debug(MARKER, "Failed to load resource {} from {}, it does not contain dependency information.", path, source);
                    return null;
                }

                // Read the metadata file and store identifiers so we can group them later.
                if (JarSelector.CONTAINED_JARS_METADATA_PATH.equals(path)) {
                    try (var is = Files.newInputStream(target)) {
                        var meta = MetadataIOHandler.fromStream(is).orElse(null);
                        if (meta == null)
                            return Files.newInputStream(target);
                        var children = new HashMap<String, String>();
                        for (var child : meta.jars())
                            children.put(child.path().replace('\\', '/'), child.identifier().group() + ':' + child.identifier().artifact());
                        this.children.put(source, children);
                    }
                }

                return Files.newInputStream(target);
            } catch (Exception e) {
                LOGGER.error(MARKER, "Failed to load resource {} from {}, cause {}", path, source, e);
                return null;
            }
        }


        @Override
        protected Entry getNested(Entry source, String path) {
            var cleaned = path.replace('\\', '/');
            var key = new Key(source, cleaned);
            var entry = entries.get(key);
            if (entry == null) {
                entry = createNested(source, path, cleaned);
                if (entry == null)
                    entry = FAILED;
                entries.put(key, entry);
            }
            return entry == FAILED ? null : entry;
        }

        private Entry createNested(Entry source, String path, String cleaned) {
            var targetPath = source.getResource(path);

            if (targetPath == null || !Files.isRegularFile(targetPath))
                return null;

            Path roimfsPath = null;
            Path zipPath = null;
            try {
                // Lets copy the uncompressed file to memory, and then create a ReadOnlyInMemorryFileSystem for it, this allows us to reference it by URI.
                // Which is needed because a lot of things require navigation via URI.
                // And ZipFileSystem only caches the FileSystem instance when accessed via URI.
                byte[] data = Files.readAllBytes(targetPath);
                String filename = targetPath.getFileName().toString();
                URI uri = new URI(ROIMFS, "jar-jar-" + nextId() + "/" + filename, null);
                var roimfs = FileSystems.newFileSystem(uri, Collections.singletonMap("data", data));
                roimfsPath = roimfs.getPath(filename);
                var zip = FileSystems.newFileSystem(new URI("jar:" + roimfsPath.toUri()), Collections.emptyMap());
                zipPath = zip.getRootDirectories().iterator().next(); // Get first root, which on ZipFileSystem is the only root
            } catch (IOException | URISyntaxException e) {
                LOGGER.error(MARKER, "Failed to create FileSystem for nested jar {}", source, e);
                return null;
            }

            var mf = new Manifest();
            var mfPath = zipPath.resolve(JarFile.MANIFEST_NAME);
            if (Files.exists(mfPath)) {
                try (var is = Files.newInputStream(mfPath)) {
                    mf = new Manifest(is);
                } catch (IOException e) {
                    LOGGER.error(MARKER, "Failed to read manifest from {}", source, e);
                }
            }

            IModFile.Type type = null;
            String id = null;
            var tomlPath = zipPath.resolve(MODS_TOML);
            if (Files.exists(tomlPath)) {
                var cfg = FileConfig.builder(tomlPath).build();
                cfg.load();
                cfg.close();
                if (cfg.getOrElse("mods", null) instanceof Collection collection) {
                    @SuppressWarnings("unchecked")
                    var mods = (Collection<UnmodifiableConfig>)collection;
                    if (!mods.isEmpty()) {
                        var first = mods.iterator().next();
                        var modId = first.getOrElse("modId", null);
                        if (modId instanceof String str)
                            id = str;
                    }
                }
                var value = mf.getMainAttributes().getValue(ModFile.TYPE);
                if (value != null)
                    type = IModFile.Type.valueOf(value);
                else
                    type = IModFile.Type.MOD;
            } else {
                var value = mf.getMainAttributes().getValue(ModFile.TYPE);
                if (value != null)
                    type = IModFile.Type.valueOf(value);
                else {
                    // No mods.toml, no entry value, so decide based on the container's type
                    if (source.type == IModFile.Type.LIBRARY || source.type == IModFile.Type.LANGPROVIDER)
                        type = IModFile.Type.LIBRARY;
                    else
                        type = IModFile.Type.GAMELIBRARY;
                }
            }

            if (id == null) {
                var modulePath = zipPath.resolve(MODULE_INFO);
                if (Files.exists(modulePath)) {
                    try (var is = Files.newInputStream(modulePath)) {
                        var module = ModuleDescriptor.read(is);
                        id = module.name();
                    } catch (IOException e) {
                        LOGGER.error(MARKER, "Failed to read module-info.class from {}", source, e);
                    }
                }
            }

            if (id == null)
                id = mf.getMainAttributes().getValue("Automatic-Module-Name");

            if (id == null)
                id = Path.of(path).getFileName().toString();

            String coords = null;
            var child = this.children.get(source);
            if (child != null)
                coords = child.get(cleaned);

            return new Nested(id, roimfsPath, type, coords, source, cleaned, zipPath);
        }

        @Override
        protected String getIdentifier(Entry entry) {
            return entry.id;
        }

        @Override
        protected Throwable getFailureException(Collection<ResolutionFailureInformation<Entry>> failures) {
            var errors = new ArrayList<ExceptionData>();
            for (var failure : failures) {
                var message = failure.failureReason() == JarSelector.FailureReason.VERSION_RESOLUTION_FAILED ? "fml.dependencyloading.conflictingdependencies" : "fml.dependencyloading.mismatchedcontaineddependencies";
                var joiner = new StringJoiner(", ");
                for (var source : failure.sources()) {
                    String paths = null;
                    if (source.sources().size() == 0) {
                        paths = "[No Sources]";
                    } else if (source.sources().size() == 1) {
                        var entry = source.sources().iterator().next();
                        paths = entry.toString();
                    } else {
                        var j = new StringJoiner(", ", "[", "]");
                        for (var entry : source.sources())
                            j.add(entry.toString());
                        paths = j.toString();
                    }

                    var error = YELLOW + paths + RESET + " - " +
                                RED + source.requestedVersionRange() + RESET + " - " +
                                GREEN + source.includedVersion() + RESET;
                    joiner.add(error);
                }

                errors.add(new ExceptionData(message, failure.identifier().group() + ":" + failure.identifier().artifact(), joiner.toString()));
            }

            return new EarlyLoadingException(failures.size() + " Dependency restrictions were not met.", null, errors);
        }
    }
}
