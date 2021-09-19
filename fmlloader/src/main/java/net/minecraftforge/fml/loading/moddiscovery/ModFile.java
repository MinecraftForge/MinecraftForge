/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.collect.ImmutableMap;
import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import net.minecraftforge.forgespi.locating.ModFileFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

public class ModFile implements IModFile {
    public static final Manifest DEFAULTMANIFEST;
    private static final Logger LOGGER = LogManager.getLogger();

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
    private final Manifest manifest;
    private final IModLocator locator;
    private IModFileInfo modFileInfo;
    private ModFileScanData fileModFileScanData;
    private CompletableFuture<ModFileScanData> futureScanResult;
    private List<CoreModFile> coreMods;
    private Path accessTransformer;

    public static final Attributes.Name TYPE = new Attributes.Name("FMLModType");
    private SecureJar.Status securityStatus;

    public ModFile(final SecureJar jar, final IModLocator locator, final ModFileFactory.ModFileInfoParser parser) {
        this.locator = locator;
        this.jar = jar;
        this.parser = parser;

        manifest = this.jar.getManifest();
        modFileType = getFmlModType(manifest);
        jarVersion = Optional.ofNullable(manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION)).orElse("0.0NONE");
    }

    public static Type getFmlModType(Manifest manifest) {
        final Optional<String> value = Optional.ofNullable(manifest.getMainAttributes().getValue(TYPE));
        return Type.valueOf(value.orElse("MOD"));
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
        if (this.modFileInfo == null) {
            // Returns false if this is a MOD type, which means invalid. Otherwise, true because other mod types don't have a mods.toml
            return this.getType() != Type.MOD;
        }
        LOGGER.debug(LOADING,"Loading mod file {} with languages {}", this.getFilePath(), this.modFileInfo.requiredLanguageLoaders());
        this.coreMods = ModFileParser.getCoreMods(this);
        this.coreMods.forEach(mi-> LOGGER.debug(LOADING,"Found coremod {}", mi.getPath()));
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
        locator.scanFile(this, pathConsumer);
    }

    public void setFutureScanResult(CompletableFuture<ModFileScanData> future)
    {
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
        StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Completed deep scan of "+this.getFileName()));
    }

    public void setFileProperties(Map<String, Object> fileProperties)
    {
        this.fileProperties = fileProperties;
    }

    @Override
    public List<IModLanguageProvider> getLoaders()
    {
        return loaders;
    }

    @Override
    public Path findResource(String... path)
    {
        if (path.length < 1) {
            throw new IllegalArgumentException("Missing path");
        }
        return getSecureJar().getPath(String.join("/", path));
    }

    public void identifyLanguage() {
        this.loaders = this.modFileInfo.requiredLanguageLoaders().stream()
                .map(spec->FMLLoader.getLanguageLoadingProvider().findLanguage(this, spec.languageName(), spec.acceptedVersions()))
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
    public IModLocator getLocator() {
        return locator;
    }

    @Override
    public IModFileInfo getModFileInfo() {
        return modFileInfo;
    }

    public static ModFileFactory buildFactory() {
        return ModFile::new;
    }

    public static ModFile newFMLInstance(final IModLocator locator, final SecureJar jar) {
        return (ModFile) ModFileFactory.FACTORY.build(jar, locator, ModFileParser::modsTomlParser);
    }

    public static ModFile newFMLInstance(final IModLocator locator, final Path... paths) {
        return (ModFile) ModJarMetadata.buildFile(locator, paths);
    }
    @Override
    public void setSecurityStatus(final SecureJar.Status status) {
        this.securityStatus = status;
    }
}
