/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.progress.StartupNotificationManager;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModProvider;
import net.minecraftforge.forgespi.locating.ModFileFactory;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ModFile implements IModFile {
    // Mods either must have a mods.toml or a manifest. We can no longer just put any jar on the classpath.
    @Deprecated(forRemoval = true, since = "1.18")
    public static final Manifest DEFAULTMANIFEST;
    private static final Logger LOGGER = LogUtils.getLogger();

    static {
        DEFAULTMANIFEST = new Manifest();
        DEFAULTMANIFEST.getMainAttributes().putValue("FMLModType", "MOD");
    }

    private final String jarVersion;
    private final ModFileFactory.ModFileInfoParser parser;
    private Map<String, Object> fileProperties;
    private List<IModLanguageProvider> loaders;
    private Throwable scanError;
    private final SecureJar jar;
    private final Type modFileType;
    private final Manifest     manifest;
    private final IModProvider provider;
    private       IModFileInfo modFileInfo;
    private ModFileScanData fileModFileScanData;
    private CompletableFuture<ModFileScanData> futureScanResult;
    private List<CoreModFile> coreMods;
    private Path accessTransformer;

    static final Attributes.Name TYPE = new Attributes.Name("FMLModType");
    private SecureJar.Status securityStatus;

    public ModFile(final SecureJar jar, final IModProvider provider, final ModFileFactory.ModFileInfoParser parser) {
        this(jar, provider, parser, parseType(jar));
    }

    public ModFile(final SecureJar jar, final IModProvider provider, final ModFileFactory.ModFileInfoParser parser, String type) {
        this.provider = provider;
        this.jar = jar;
        this.parser = parser;

        manifest = this.jar.moduleDataProvider().getManifest();
        modFileType = Type.valueOf(type);
        jarVersion = Optional.ofNullable(manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION)).orElse("0.0NONE");
        this.modFileInfo = ModFileParser.readModList(this, this.parser);
    }

    @Override
    public Supplier<Map<String,Object>> getSubstitutionMap() {
        return () -> ImmutableMap.<String,Object>builder().put("jarVersion", jarVersion).putAll(fileProperties).build();
    }
    @Override
    public Type getType() {
        return modFileType;
    }

    @Override
    public Path getFilePath() {
        return jar.getPrimaryPath();
    }

    @Override
    public SecureJar getSecureJar() {
        return this.jar;
    }

    @Override
    public List<IModInfo> getModInfos() {
        return modFileInfo.getMods();
    }

    public Optional<Path> getAccessTransformer() {
        return Optional.ofNullable(Files.exists(accessTransformer) ? accessTransformer : null);
    }

    public boolean identifyMods() {
        this.modFileInfo = ModFileParser.readModList(this, this.parser);
        if (this.modFileInfo == null) return this.getType() != Type.MOD;
        LOGGER.debug(LogMarkers.LOADING,"Loading mod file {} with languages {}", this.getFilePath(), this.modFileInfo.requiredLanguageLoaders());
        this.coreMods = ModFileParser.getCoreMods(this);
        this.coreMods.forEach(mi-> LOGGER.debug(LogMarkers.LOADING,"Found coremod {}", mi.getPath()));
        this.accessTransformer = findResource("META-INF", "accesstransformer.cfg");
        return true;
    }

    public List<CoreModFile> getCoreMods() {
        return coreMods;
    }

    /**
     * Run in an executor thread to harvest the class and annotation list
     */
    public ModFileScanData compileContent() {
        return new Scanner(this).scan();
    }

    public void scanFile(Consumer<Path> pathConsumer) {
        provider.scanFile(this, pathConsumer);
    }

    public void setFutureScanResult(CompletableFuture<ModFileScanData> future) {
        this.futureScanResult = future;
    }

    @Override
    public ModFileScanData getScanResult() {
        if (this.futureScanResult != null) {
            try {
                this.futureScanResult.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Caught unexpected exception processing scan results", e);
            }
        }
        if (this.scanError != null) {
            throw new RuntimeException(this.scanError);
        }
        return this.fileModFileScanData;
    }

    public void setScanResult(final ModFileScanData modFileScanData, final Throwable throwable) {
        this.futureScanResult = null;
        this.fileModFileScanData = modFileScanData;
        if (throwable != null) {
            this.scanError = throwable;
        }
    }

    public void setFileProperties(Map<String, Object> fileProperties) {
        this.fileProperties = fileProperties;
    }

    @Override
    public List<IModLanguageProvider> getLoaders() {
        return loaders;
    }

    @Override
    public Path findResource(String... path) {
        if (path.length < 1) {
            throw new IllegalArgumentException("Missing path");
        }
        return getSecureJar().getPath(String.join("/", path));
    }

    public void identifyLanguage() {
        this.loaders = this.modFileInfo.requiredLanguageLoaders().stream()
                .map(spec-> FMLLoader.getLanguageLoadingProvider().findLanguage(this, spec.languageName(), spec.acceptedVersions()))
                .toList();
    }

    @Override
    public String toString() {
        return "Mod File: " + Objects.toString(this.jar.getPrimaryPath());
    }

    @Override
    public String getFileName() {
        return getFilePath().getFileName().toString();
    }

    @Override
    public IModProvider getProvider() {
        return provider;
    }

    @Override
    public IModFileInfo getModFileInfo() {
        return modFileInfo;
    }

    @Override
    public void setSecurityStatus(final SecureJar.Status status) {
        this.securityStatus = status;
    }

    public ArtifactVersion getJarVersion()
    {
        return new DefaultArtifactVersion(this.jarVersion);
    }

    private static String parseType(final SecureJar jar) {
        final Manifest m = jar.moduleDataProvider().getManifest();
        final Optional<String> value = Optional.ofNullable(m.getMainAttributes().getValue(TYPE));
        return value.orElse("MOD");
    }
}
