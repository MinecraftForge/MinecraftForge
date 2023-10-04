/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.google.common.jimfs.Jimfs;

import cpw.mods.niofs.union.UnionFileSystemProvider;

@ApiStatus.Internal
abstract class ForgeDevLaunchHandler extends CommonDevLaunchHandler {
    private static final String MODS_TOML = "META-INF/mods.toml";
    private static final String PACK_META = "pack.mcmeta";
    private static final UnionFileSystemProvider UFSP = (UnionFileSystemProvider)FileSystemProvider.installedProviders().stream().filter(fsp->fsp.getScheme().equals("union")).findFirst().orElseThrow(()->new IllegalStateException("Couldn't find UnionFileSystemProvider"));

    private ForgeDevLaunchHandler(LaunchType type) {
        super(type, "forge_dev_");
    }

    @Override
    public LocatedPaths getMinecraftPaths() {
        // Our exploded directories in development include all mods and minecraft itself.
        var mods = getModClasses();

        // The extra jar is on the classpath, so try and pull it out of the legacy classpath
        var legacyCP = Objects.requireNonNull(System.getProperty("legacyClassPath"), "Missing legacyClassPath, cannot find client-extra").split(File.pathSeparator);
        var extra = findJarOnClasspath(legacyCP, "client-extra");


        // Minecraft is extra jar {resources} + our exploded directories in dev
        var minecraft = mods.remove("minecraft");
        if (minecraft == null)
            throw new IllegalStateException("Could not find 'minecraft' mod paths.");
        var mcFilter = getMcFilter(extra, minecraft);

        // The MC code/Patcher edits are in exploded directories
        var modstream = Stream.<List<Path>>builder();
        var forge = this.getForgeMod(minecraft);
        modstream.add(List.of(forge));

        // Explode our test mods into a framework that treats them all as separate jars.
        var tests = mods.remove("tests");
        if (tests != null)
            explodeTestMods(tests).forEach(modstream::add);

        mods.values().forEach(modstream::add);

        return new LocatedPaths(
            Stream.concat(minecraft.stream(), List.of(extra).stream()).toList(),
            mcFilter,
            modstream.build().toList(),
            getLibraries(legacyCP)
        );
    }

    private List<List<Path>> explodeTestMods(List<Path> paths) {
        var mod = new ArrayList<List<Path>>();
        var memory = Jimfs.newFileSystem();

        // First lets find all class files, that have the @Mod annotation and map packages to modids.
        var packages = findTestModPackages(paths);
        for (var entry : packages.entrySet()) {
            var pkg = entry.getKey();
            var modids = entry.getValue();

            var resourcePaths = new LinkedHashSet<Path>();
            // Jump down into the package for resources
            paths.stream()
                .<Path>mapMulti((p, c) -> modids.forEach(id -> c.accept(p.resolve(id))))
                .filter(Files::exists).forEach(resourcePaths::add);

            var root = memory.getPath(modids.iterator().next()); // use the first modid as our root
            buildModsToml(resourcePaths, modids, root);
            buildPackMeta(resourcePaths, root);

            if (Files.exists(root))
                resourcePaths.add(root);

            // We want just the class files from the root of the input paths. So make a new union with a filter.
            var classes = UFSP.newFileSystem(
                (path, base) -> {
                    if (path.endsWith("/")) {
                        if (path.startsWith("/"))
                            path = path.substring(1);
                        return pkg.startsWith(path) || path.startsWith(pkg);
                    }
                    return path.endsWith(".class") && path.startsWith(pkg);
                }, paths.toArray(Path[]::new));
            // And we want the resources, and none of the classes, just in case things get kinda fucky and somehow a package overlaps a sub package {shading?}
            var resources = UFSP.newFileSystem((path, base) -> !path.endsWith(".class"), resourcePaths.toArray(Path[]::new));

            mod.add(List.of(classes.getRoot(), resources.getRoot()));
        }

        return mod;
    }

    // Find all the @Mods so we can generate tomls, and so we can pick packages. Right now it doesn't allow mods in parent directories,
    // I could make it merge all the way up, but I think this would be fine.
    private Map<String, Set<String>> findTestModPackages(List<Path> paths) {
        var mods = new HashMap<String, Set<String>>();
        for (var path : paths) {
            try (var files = Files.walk(path)) {
                var classes = files
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".class"))
                    .toList();

                for (var cls : classes) {
                    try (var is = Files.newInputStream(cls)) {
                        var reader = new ClassReader(is);
                        reader.accept(new ClassVisitor(Opcodes.ASM9) {
                            private String clsName;
                            @Override
                            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                                this.clsName = name;
                                super.visit(version, access, name, signature, superName, interfaces);
                            }

                            @Override
                            public AnnotationVisitor visitAnnotation(String name, boolean runtime) {
                                if (!"Lnet/minecraftforge/fml/common/Mod;".equals(name))
                                    return super.visitAnnotation(name, runtime);

                                return new AnnotationVisitor(Opcodes.ASM9) {
                                    @Override
                                    public void visit(String key, Object value) {
                                        if ("value".equals(key)) {
                                            int idx = clsName.lastIndexOf('/');
                                            var pkg = clsName.substring(0, idx);
                                            mods.computeIfAbsent(pkg, k -> new HashSet<>()).add((String)value);

                                            idx = pkg.lastIndexOf('/');
                                            while (idx != -1) {
                                                pkg = pkg.substring(0, idx);
                                                idx = pkg.lastIndexOf('/');
                                                if (mods.containsKey(pkg))
                                                    throw new IllegalStateException("Invalid ForgeDev test mod layout, conflicting packages: " + pkg + " for " + clsName);
                                            }
                                        }
                                    }
                                };
                            }
                        }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    }
                }
            } catch (IOException e) {
                sneak(e);
            }
        }
        return mods;
    }

    // Builds or update the mods.toml file for all @Mods in this package
    private void buildModsToml(Set<Path> resources, Set<String> modids, Path root) {
        var toml = resources.stream()
            .map(p -> p.resolve(MODS_TOML))
            .filter(Files::exists)
            .findFirst()
            .orElse(null);

        Config cfg = null;

        if (toml != null)
            cfg = new TomlParser().parse(toml, FileNotFoundAction.READ_NOTHING);
        else
            cfg = Config.inMemory();

        boolean modified = false;
        var defaults = Map.of(
            "modLoader", "javafml",
            "loaderVersion", "[0,)",
            "license", "Doesnt fucking matter"
        );

        for (var key : defaults.keySet()) {
            if (!cfg.contains(key)) {
                cfg.set(key, defaults.get(key));
                modified = true;
            }
        }

        var modlist = new ArrayList<Config>();
        if (cfg.contains("mods")) {
            @SuppressWarnings("unchecked")
            var existing = ((List<Config>)cfg.get("mods"));
            modlist.addAll(existing);
        }

        for (var modid : modids) {
            if (!modlist.stream().anyMatch(c -> modid.equals(c.get("modId")))) {
                modified = true;
                var tmp = Config.inMemory();
                tmp.set("modId", modid);
                modlist.add(tmp);
            }
        }

        cfg.set("mods", modlist);

        if (modified) {
            var target = root.resolve(MODS_TOML);
            try {
                Files.createDirectories(target.getParent());
                new TomlWriter().write(cfg, target, WritingMode.REPLACE);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to create in memory toml: " + target, e);
            }
        }
    }

    // This is optional, it just hides a warning screen when starting up. I should probably remove this once I restructure how data gen for test mods work and make it generate there.
    private void buildPackMeta(Set<Path> paths, Path root) {
        var existing = paths.stream()
                .map(p -> p.resolve(PACK_META))
                .filter(Files::exists)
                .findFirst()
                .orElse(null);

        if (existing != null)
            return;
        var target = root.resolve(PACK_META);
        try {
            Files.createDirectories(target.getParent());
            Files.writeString(target,
            """
            {
                "pack": {
                    "description": "doesn't matter",
                    "pack_format": 18
                 }
            }
            """, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to make in memory pack.mcmeta: " + target, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable, R> R sneak(Throwable e) throws E {
        throw (E)e;
    }

    public static class Client extends ForgeDevLaunchHandler {
        public Client() {
            super(CLIENT);
        }
    }

    public static class Data extends ForgeDevLaunchHandler {
        public Data() {
            super(DATA);
        }
    }

    public static class Server extends ForgeDevLaunchHandler {
        public Server() {
            super(SERVER);
        }
    }

    public static class ServerGameTest extends ForgeDevLaunchHandler {
        public ServerGameTest() {
            super(SERVER_GAMETEST);
        }
    }
}
