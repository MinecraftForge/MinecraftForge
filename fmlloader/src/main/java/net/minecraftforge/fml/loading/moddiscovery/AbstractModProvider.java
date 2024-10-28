/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.mojang.logging.LogUtils;

import cpw.mods.jarhandling.JarMetadata;
import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import net.minecraftforge.forgespi.locating.IModProvider;
import net.minecraftforge.forgespi.locating.ModFileLoadingException;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.module.InvalidModuleDescriptorException;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.Version;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@ApiStatus.Internal
public abstract class AbstractModProvider implements IModProvider {
    private static final   Logger LOGGER      = LogUtils.getLogger();
    protected static final String MODS_TOML   = "META-INF/mods.toml";
    protected static final String MODULE_INFO = "module-info.class";

    protected IModLocator.ModFileOrException createMod(Path path) {
        return createMod(path, false);
    }

    @Nullable
    protected IModLocator.ModFileOrException createMod(Path path, boolean ignoreUnknown) {
        return createMod(path, ignoreUnknown, getDefaultJarModType());
    }

    @Nullable
    protected IModLocator.ModFileOrException createMod(Path path, boolean ignoreUnknown, String defaultType) {
        var mjm = new ModJarMetadata();
        SecureJar sj = null;
        try {
            sj = SecureJar.from(jar -> loadMetaFromJar(jar, mjm), path);
        } catch (Throwable t) {
            return new IModLocator.ModFileOrException(null, new ModFileLoadingException("Failed to create secure jar for \"" + path + "\" - " + t.getMessage()));
        }

        IModFile mod;
        var type = sj.moduleDataProvider().getManifest().getMainAttributes().getValue(ModFile.TYPE);
        if (type == null)
            type = defaultType;

        if (sj.moduleDataProvider().findFile(MODS_TOML).isPresent()) {
            LOGGER.debug(LogMarkers.SCAN, "Found {} mod of type {}: {}", MODS_TOML, type, path);
            mod = new ModFile(sj, this, ModFileParser::modsTomlParser);
            // ModJarMetadata is only used when loading mods.toml mods as ModJarMetadata
            mjm.setModFile(mod);
            if (mod.getModFileInfo().getFileProperties().containsKey(ModFileInfo.NOT_A_FORGE_MOD_PROP)) {
                LOGGER.error(LogMarkers.SCAN, "Unable to load file \"{}\" because its mods.toml is requesting an invalid javafml loaderVersion (use \"*\" if you want to allow all versions) and is missing a forge modId dependency declaration (see the sample mods.toml in the MDK).", path);
                return new IModLocator.ModFileOrException(null, new ModFileLoadingException("File \"%s\" is not a Forge mod and cannot be loaded. Look for a Forge version of this mod or consider alternative mods.".formatted(mod.getFileName())));
            }
        } else if (type != null) {
            LOGGER.debug(LogMarkers.SCAN, "Found {} mod of type {}: {}", JarFile.MANIFEST_NAME, type, path);
            mod = new ModFile(sj, this, this::manifestParser, type);
        } else if (ignoreUnknown) {
            return null;
        } else
            return new IModLocator.ModFileOrException(null, new ModFileLoadingException("Invalid mod file found " + path));

        return new IModLocator.ModFileOrException(mod, null);
    }

    private static JarMetadata loadMetaFromJar(SecureJar jar, ModJarMetadata mjm) {
        var info = jar.moduleDataProvider().open(MODULE_INFO).orElse(null);
        if (info != null) {
            try {
                var desc = ModuleDescriptor.read(info, jar::getPackages);
                var all = new HashSet<>(jar.getPackages());
                all.removeAll(desc.packages());
                if (!all.isEmpty()) {
                    var missing = all.stream().sorted().collect(Collectors.joining(", "));
                    LOGGER.error("Invalid module-info, missing packages " + missing);
                    throw new ModFileLoadingException("Invalid module-info, missing packages " + missing);
                } else {
                    return new JarMetadata() {
                        @Override
                        public String name() {
                            return desc.name();
                        }

                        @Override
                        public String version() {
                            return desc.version().map(Version::toString).or(desc::rawVersion).orElse(null);
                        }

                        @Override
                        public ModuleDescriptor descriptor() {
                            return desc;
                        }
                    };
                }
            } catch (InvalidModuleDescriptorException | IOException e) {
                LOGGER.error("Failed to parse " + jar.getPrimaryPath() + " module-info", e);
                throw new ModFileLoadingException("Invalid module-info: " + e.getMessage());
            } finally {
                try {
                    info.close();
                } catch (IOException e) {}
            }
        }

        if (jar.moduleDataProvider().findFile(MODS_TOML).isEmpty())
            return JarMetadata.from(jar, jar.getPrimaryPath());

        return mjm;
    }

    protected IModFileInfo manifestParser(final IModFile mod) {
        var mf = mod.getSecureJar().moduleDataProvider().getManifest().getMainAttributes();
        var license = mf.getValue("LICENSE");
        var dummy = new IConfigurable() {
            @Override
            public <T> Optional<T> getConfigElement(String key) {
                return Optional.empty();
            }

            @Override
            public <T> Optional<T> getConfigElement(String... key) {
                return Optional.empty();
            }
            @Override
            public List<? extends IConfigurable> getConfigList(String... key) {
                return Collections.emptyList();
            }
        };

        return new DefaultModFileInfo(mod, license == null ? "" : license, dummy);
    }

    @Override
    public boolean isValid(final IModFile modFile) {
        return true;
    }

    @Override
    public void initArguments(final Map<String, ?> arguments) {
    }

    protected String getDefaultJarModType() {
        return null;
    }

    @Override
    public void scanFile(IModFile file, Consumer<Path> pathConsumer) {
        LOGGER.debug(LogMarkers.SCAN, "Scan started: {}", file);
        var jar = file.getSecureJar();
        var root = jar.getRootPath();
        Consumer<Path> consumer = pathConsumer;

        //TODO: [SecureJar] Actually redesign this to have a proper validation.
        var holder = new Holder<SecureJar.Status>();
        holder.value = SecureJar.Status.NONE;
        if (jar.hasSecurityData()) {
            consumer = path -> {
                pathConsumer.accept(path);
                var status = jar.verifyPath(path);
                if (status.ordinal() < holder.value.ordinal())
                    holder.value = status;
            };
        }

        try (var files = Files.walk(root)) {
            files
                .filter(p -> p.toString().endsWith(".class"))
                .forEach(consumer);

            file.setSecurityStatus(holder.value);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.debug(LogMarkers.SCAN, "Scan finished: {}", file);
    }

    private static final class Holder<T> {
        private T value;
    }

    private record DefaultModFileInfo(IModFile mod, String license, IConfigurable configurable) implements IModFileInfo, IConfigurable {
        @Override public <T> Optional<T> getConfigElement(final String string) { return Optional.empty(); }
        @Override public <T> Optional<T> getConfigElement(final String... strings) { return Optional.empty(); }
        @Override public List<? extends IConfigurable> getConfigList(final String... strings) { return null; }
        @Override public List<IModInfo> getMods() { return Collections.emptyList(); }
        @Override public List<LanguageSpec> requiredLanguageLoaders() { return Collections.emptyList(); }
        @Override public boolean showAsResourcePack() { return false; }
        @Override public boolean showAsDataPack() { return false; }
        @Override public Map<String, Object> getFileProperties() { return Collections.emptyMap(); }
        @Override public String getLicense() { return license; }
        @Override public IModFile getFile() { return mod; }
        @Override public IConfigurable getConfig() { return configurable; }
        // These Should never be called as it's only called from ModJarMetadata.version and we bypass that
        @Override public String moduleName() { return mod.getSecureJar().name(); }
        @Override public String versionString() { return null; }
        @Override public List<String> usesServices() { return null; }
        @Override public String toString() { return "IModFileInfo(" + mod.getFilePath() + ")"; }
    }
}
