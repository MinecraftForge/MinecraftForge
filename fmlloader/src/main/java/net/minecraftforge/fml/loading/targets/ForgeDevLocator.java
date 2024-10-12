/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.AbstractModProvider;
import net.minecraftforge.forgespi.locating.IModLocator;

@ApiStatus.Internal
public final class ForgeDevLocator extends AbstractModProvider implements IModLocator {
    private static final String PACK_META = "pack.mcmeta";

    @Override
    public String name() {
        return "forge_dev_locator";
    }

    @Override
    public List<ModFileOrException> scanMods() {
        var handler = FMLLoader.getLaunchHandler();

        if (!(handler instanceof ForgeDevLaunchHandler))
            return List.of();

        var mods = getMods();
        var ret = new ArrayList<ModFileOrException>();
        for (var path : mods) {
            var mod = createMod(path);
            if (mod != null)
                ret.add(mod);
        }
        return ret;
    }

    private static List<Path> getMods() {
        // Forge is an exploded directory as well
        var minecraft = ForgeDevLaunchHandler.getPathFromResource("net/minecraft/client/Minecraft.class");
        var forge = ForgeDevLaunchHandler.getPathFromResource("net/minecraftforge/common/MinecraftForge.class");
        if (minecraft.equals(forge)) {
            // If both Forge and MC are in the same folder, then we are in intellij or gradle
            // So we have to create a filtered jar
            forge = CommonDevLaunchHandler.getForgeOnly(forge);
        }
        var ret = new ArrayList<Path>();
        ret.add(forge);

        var isTest = Boolean.getBoolean("forgedev.enableTestMods");
        if (!isTest)
            return ret;

        var test = ForgeDevLaunchHandler.getPathFromResource("net/minecraftforge/test/BaseTestMod.class", ClassLoader.getSystemClassLoader());
        // Explode our test mods into a framework that treats them all as separate jars.
        var tests = explodeTestMods(test);
        ret.addAll(tests);

        return ret;
    }

    private static List<Path> explodeTestMods(Path path) {
        var mod = new ArrayList<Path>();
        var memory = Jimfs.newFileSystem();

        // First lets find all class files, that have the @Mod annotation and map packages to modids.
        var packages = findTestModPackages(path);
        for (var entry : packages.entrySet()) {
            var pkg = entry.getKey() + '/';
            var modids = entry.getValue();

            // Find resource directories for every mod in this group
            var paths = new LinkedHashSet<Path>();
            for (var modid : modids) {
                var rsc = path.resolve(modid);
                if (Files.exists(rsc))
                    paths.add(rsc);
            }

            var root = memory.getPath(modids.iterator().next()); // use the first modid as our root
            buildModsToml(paths, modids, root);
            buildPackMeta(paths, root);
            moveModuleInfo(paths, root);

            if (Files.exists(root))
                paths.add(root);

            // We want just the class files from the root of the input paths. So make a new union with a filter.
            var classes = UnionHelper.newFileSystem(
                (name, base) -> {
                    if (name.endsWith("/")) {
                        if (name.startsWith("/"))
                            name = name.substring(1);
                        return pkg.startsWith(name) || name.startsWith(pkg);
                    }
                    return name.endsWith(".class") && name.startsWith(pkg);
                }, new Path[] { path }
            );

            paths.addFirst(classes.getRootDirectories().iterator().next());

            // Union of unions, Yay!
            var union = UnionHelper.newFileSystem(null, paths.stream().toArray(Path[]::new));
            mod.add(union.getRootDirectories().iterator().next());
        }

        return mod;
    }

    // Find all the @Mods so we can generate tomls, and so we can pick packages. Right now it doesn't allow mods in parent directories,
    // I could make it merge all the way up, but I think this would be fine.
    private static Map<String, Set<String>> findTestModPackages(Path path) {
        var mods = new HashMap<String, Set<String>>();
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
        return mods;
    }

    // Builds or update the mods.toml file for all @Mods in this package
    private static void buildModsToml(Set<Path> resources, Set<String> modids, Path root) {
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
            if (modlist.stream().noneMatch(c -> modid.equals(c.get("modId")))) {
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
    private static void buildPackMeta(Set<Path> paths, Path root) {
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

    private static void moveModuleInfo(Set<Path> paths, Path root) {
        var existing = paths.stream()
            .map(p -> p.resolve("module-info.dat"))
            .filter(Files::exists)
            .findFirst()
            .orElse(null);

        if (existing == null)
            return;

        var target = root.resolve("module-info.class");
        try {
            Files.createDirectories(target.getParent());
            Files.copy(existing, target);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to copy module-info.dat to memory: " + target, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable, R> R sneak(Throwable e) throws E {
        throw (E)e;
    }
}
