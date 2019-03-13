/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
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
import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public class ModFile
{
    private static final Manifest DEFAULTMANIFEST;
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        DEFAULTMANIFEST = new Manifest();
        DEFAULTMANIFEST.getMainAttributes().putValue("FMLModType", "MOD");
    }

    private final String jarVersion;
    private Map<String, Object> fileProperties;
    private IModLanguageProvider loader;
    private Throwable scanError;

    public void setFileProperties(Map<String, Object> fileProperties)
    {
        this.fileProperties = fileProperties;
    }

    public IModLanguageProvider getLoader()
    {
        return loader;
    }

    public Path findResource(String className)
    {
        return locator.findPath(this, className);
    }

    public void identifyLanguage() {
        this.loader = FMLLoader.getLanguageLoadingProvider().findLanguage(this, this.modFileInfo.getModLoader(), this.modFileInfo.getModLoaderVersion());
    }

    public enum Type {
        MOD, LIBRARY, LANGPROVIDER
    }
    private final Path filePath;
    private final Type modFileType;
    private final Manifest manifest;
    private final IModLocator locator;
    private IModFileInfo modFileInfo;
    private ModFileScanData fileModFileScanData;
    private CompletableFuture<ModFileScanData> futureScanResult;
    private List<CoreModFile> coreMods;
    private Path accessTransformer;

    private static final Attributes.Name TYPE = new Attributes.Name("FMLModType");

    public ModFile(final Path file, final IModLocator locator) {
        this.locator = locator;
        this.filePath = file;
        manifest = locator.findManifest(file).orElse(DEFAULTMANIFEST);
        if (manifest != DEFAULTMANIFEST) LOGGER.debug(SCAN,"Mod file {} has a manifest", file);
        else LOGGER.debug(SCAN,"Mod file {} is missing a manifest", file);
        final Optional<String> value = Optional.ofNullable(manifest.getMainAttributes().getValue(TYPE));
        modFileType = Type.valueOf(value.orElse("MOD"));
        jarVersion = Optional.ofNullable(manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION)).orElse("NONE");
    }

    public Supplier<Map<String,Object>> getSubstitutionMap() {
        return () -> ImmutableMap.<String,Object>builder().put("jarVersion", jarVersion).putAll(fileProperties).build();
    }
    public Type getType() {
        return modFileType;
    }

    public Path getFilePath() {
        return filePath;
    }

    public List<IModInfo> getModInfos() {
        return modFileInfo.getMods();
    }

    public Optional<Path> getAccessTransformer() {
        return Optional.ofNullable(Files.exists(accessTransformer) ? accessTransformer : null);
    }
    public boolean identifyMods() {
        this.modFileInfo = ModFileParser.readModList(this);
        if (this.modFileInfo == null) return false;
        LOGGER.debug(LOADING,"Loading mod file {} with language {}", this.getFilePath(), this.modFileInfo.getModLoader());
        this.coreMods = ModFileParser.getCoreMods(this);
        this.coreMods.forEach(mi-> LOGGER.debug(LOADING,"Found coremod {}", mi.getPath()));
        this.accessTransformer = locator.findPath(this, "META-INF", "accesstransformer.cfg");
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

    public ModFileScanData getScanResult() {
        if (this.futureScanResult != null) {
            try {
                this.futureScanResult.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
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

    @Override
    public String toString() {
        return "Mod File: " + Objects.toString(this.filePath);
    }

    public String getFileName() {
        return getFilePath().getFileName().toString();
    }

    public IModLocator getLocator() {
        return locator;
    }

    public IModFileInfo getModFileInfo() {
        return modFileInfo;
    }
}
