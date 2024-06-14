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
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.jar.JarFile;

@ApiStatus.Internal
public abstract class AbstractModProvider implements IModProvider {
    private static final   Logger LOGGER    = LogUtils.getLogger();
    protected static final String MODS_TOML = "META-INF/mods.toml";

    protected IModLocator.ModFileOrException createMod(Path path) {
        return this.createMod(path, getDefaultJarModType());
    }

    protected IModLocator.ModFileOrException createMod(Path path, String defaultType) {
        var mjm = new ModJarMetadata();
        var sj = SecureJar.from(
            jar -> jar.moduleDataProvider().findFile(MODS_TOML).isPresent() ? mjm : JarMetadata.from(jar, path),
            path
        );

        IModFile mod;
        var type = sj.moduleDataProvider().getManifest().getMainAttributes().getValue(ModFile.TYPE);
        if (type == null)
            type = defaultType;

        if (sj.moduleDataProvider().findFile(MODS_TOML).isPresent()) {
            LOGGER.debug(LogMarkers.SCAN, "Found {} mod of type {}: {}", MODS_TOML, type, path);
            mod = new ModFile(sj, this, ModFileParser::modsTomlParser);
            if (ModFileInfo.DETECT_NON_FORGE_MODS_TOML && mod.getModFileInfo().getFileProperties().containsKey(ModFileInfo.NOT_A_FORGE_MOD_PROP)) {
                LOGGER.error(LogMarkers.SCAN, "Unable to load file \"{}\" because its mods.toml is requesting an invalid javafml loaderVersion (use \"*\" if you want to allow all versions) and is missing a forge modId dependency declaration (see the sample mods.toml in the MDK).", path);
                return new IModLocator.ModFileOrException(null, new ModFileLoadingException("File \"%s\" is not a Forge mod and cannot be loaded. Look for a Forge version of this mod or consider alternative mods.".formatted(mod.getFileName())));
            }
        } else if (type != null) {
            LOGGER.debug(LogMarkers.SCAN, "Found {} mod of type {}: {}", JarFile.MANIFEST_NAME, type, path);
            mod = new ModFile(sj, this, this::manifestParser, type);
        } else
            return new IModLocator.ModFileOrException(null, new ModFileLoadingException("Invalid mod file found " + path));

        mjm.setModFile(mod);
        return new IModLocator.ModFileOrException(mod, null);
    }

    protected IModFileInfo manifestParser(final IModFile mod) {
        var mf = mod.getSecureJar().moduleDataProvider().getManifest().getMainAttributes();
        var license = mf.getValue("LICENSE");
        var dummy = new IConfigurable() {
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
        T value;
    }

    private record DefaultModFileInfo(IModFile mod, String license, IConfigurable configurable) implements IModFileInfo, IConfigurable {
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
