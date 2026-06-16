/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.fml.loading.LanguageLoadingProvider;
import net.minecraftforge.fml.loading.LogMarkers;
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

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ModFile implements IModFile {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String jarVersion;
    private Map<String, Object> fileProperties;
    private List<IModLanguageProvider> loaders;
    private Throwable scanError;
    private final SecureJar jar;
    private final Type modFileType;
    private final IModProvider provider;
    private final IModFileInfo modFileInfo;
    private ModFileScanData fileModFileScanData;
    private CompletableFuture<ModFileScanData> futureScanResult;
    private List<Path> accessTransformers = List.of();

    static final Attributes.Name TYPE = new Attributes.Name("FMLModType");
    private SecureJar.Status securityStatus;

    public ModFile(final SecureJar jar, final IModProvider provider, final ModFileFactory.ModFileInfoParser parser) {
        this(jar, provider, parser, parseType(jar));
    }

    public ModFile(final SecureJar jar, final IModProvider provider, final ModFileFactory.ModFileInfoParser parser, String type) {
        this.provider = provider;
        this.jar = jar;

        var manifest = this.jar.moduleDataProvider().getManifest();
        modFileType = Type.valueOf(type);
        jarVersion = Optional.ofNullable(manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION)).orElse("0.0NONE");
        this.modFileInfo = ModFileParser.readModList(this, parser);
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

    public List<Path> getAccessTransformers() {
        return this.accessTransformers;
    }

    public boolean identifyMods() {
        if (this.modFileInfo == null) return this.getType() != Type.MOD;
        LOGGER.debug(LogMarkers.LOADING,"Loading mod file {} with languages {}", this.getFileName(), this.modFileInfo.requiredLanguageLoaders());
        var cfg = this.modFileInfo.getConfig()
            .<List<String>>getConfigElement("accessTransformers")
            .orElse(null);

        if (cfg == null) {
            var path = findResource("META-INF", "accesstransformer.cfg");
            if (Files.exists(path)) {
                this.accessTransformers = List.of(path);
            }
        } else if (!cfg.isEmpty()) {
            var paths = new ArrayList<Path>(cfg.size());
            for (var path : cfg)
                paths.add(getSecureJar().getPath(path.replace('\\', '/')));
            this.accessTransformers = List.copyOf(paths);
        }

        return true;
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
        var lst = new ArrayList<IModLanguageProvider>(this.modFileInfo.requiredLanguageLoaders().size());
        for (var spec : this.modFileInfo.requiredLanguageLoaders()) {
            var service = LanguageLoadingProvider.findLanguage(this, spec.languageName(), spec.acceptedVersions());
            lst.add(service);
        }
        this.loaders = List.copyOf(lst);
    }

    @Override
    public String toString() {
        var path = getFilePath();
        return "Mod File: " + path == null ? "null" : path.toUri().toString();
    }

    @Override
    public String getFileName() {
        // This is only used for debugging, so lets try and make it a useful debug
        var path = getFilePath();
        var filename = path.getFileName();

        // ZipFileSystem toString will return the outer file path
        var ret = filename == null ? path.getFileSystem().toString() : filename.toString();

        // If it's a directory on the default file system, return the full path
        if (Files.isDirectory(path) && path.getFileSystem() == FileSystems.getDefault())
            ret = path.toAbsolutePath().toString();

        // If we are in a FileSystem 'root' like UnionFS, return the full uri
        if (ret.isEmpty() || ret.equals("/"))
            ret = path.toUri().toString();

        return ret;
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

    public SecureJar.Status getSecurityStatus() {
        return this.securityStatus;
    }

    public ArtifactVersion getJarVersion() {
        return new DefaultArtifactVersion(this.jarVersion);
    }

    private static String parseType(final SecureJar jar) {
        final Manifest m = jar.moduleDataProvider().getManifest();
        final Optional<String> value = Optional.ofNullable(m.getMainAttributes().getValue(TYPE));
        return value.orElse("MOD");
    }
}
