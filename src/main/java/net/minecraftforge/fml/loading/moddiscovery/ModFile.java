/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.fml.StringUtils;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.IModLanguageProvider;
import net.minecraftforge.fml.loading.ModLoadingClassLoader;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.LOADING;
import static net.minecraftforge.fml.Logging.SCAN;
import static net.minecraftforge.fml.Logging.fmlLog;

public class ModFile
{
    private static final Manifest DEFAULTMANIFEST;
    static {
        DEFAULTMANIFEST = new Manifest();
        DEFAULTMANIFEST.getMainAttributes().putValue("FMLModType", "MOD");
    }

    private final String jarVersion;
    private Map<String, Object> fileProperties;
    private IModLanguageProvider loader;

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

    public List<ModContainer> buildMods(ModLoadingClassLoader modClassLoader)
    {
        return getScanResult().getTargets().entrySet().stream().
                map(e->e.getValue().loadMod(this.modInfoMap.get(e.getKey()), modClassLoader)).collect(Collectors.toList());
    }

    public enum Type {
        MOD, LIBRARY, LANGPROVIDER
    }
    private final Path filePath;
    private final Type modFileType;
    private final Manifest manifest;
    private final IModLocator locator;
    private ModFileInfo modFileInfo;
    private Map<String, ModInfo> modInfoMap;
    private ScanResult fileScanResult;
    private CompletableFuture<ScanResult> futureScanResult;
    private List<CoreModFile> coreMods;
    private Path accessTransformer;

    private static final Attributes.Name TYPE = new Attributes.Name("FMLModType");

    public ModFile(final Path file, final IModLocator locator) {
        this.locator = locator;
        this.filePath = file;
        manifest = locator.findManifest(file).orElse(DEFAULTMANIFEST);
        if (manifest != DEFAULTMANIFEST) fmlLog.debug(SCAN,"Mod file {} has a manifest", file);
        else fmlLog.debug(SCAN,"Mod file {} is missing a manifest", file);
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

    public List<ModInfo> getModInfos() {
        return modFileInfo.getMods();
    }

    public Optional<Path> getAccessTransformer() {
        return Optional.ofNullable(Files.exists(accessTransformer) ? accessTransformer : null);
    }
    public boolean identifyMods() {
        this.modFileInfo = ModFileParser.readModList(this);
        if (this.modFileInfo == null) return false;
        fmlLog.debug(LOADING,"Loading mod file {} with language {}", this.getFilePath(), this.modFileInfo.modLoader);
        this.modInfoMap = this.getModInfos().stream().collect(Collectors.toMap(ModInfo::getModId, Function.identity()));
        this.coreMods = ModFileParser.getCoreMods(this);
        this.coreMods.forEach(mi-> fmlLog.debug(LOADING,"Found coremod {}", mi.getPath()));
        this.accessTransformer = locator.findPath(this, "META-INF", "accesstransformer.cfg");
        this.loader = FMLLoader.getLanguageLoadingProvider().findLanguage(this.modFileInfo.modLoader, this.modFileInfo.modLoaderVersion);
        return true;
    }


    public List<CoreModFile> getCoreMods() {
        return coreMods;
    }

    /**
     * Run in an executor thread to harvest the class and annotation list
     */
    public ScanResult compileContent() {
        return new Scanner(this).scan();
    }

    public void scanFile(Consumer<Path> pathConsumer) {
        locator.scanFile(this, pathConsumer);
    }
    public void setFutureScanResult(CompletableFuture<ScanResult> future)
    {
        this.futureScanResult = future;
    }

    public ScanResult getScanResult() {
        if (this.futureScanResult != null) {
            try {
                this.futureScanResult.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return this.fileScanResult;
    }

    public void setScanResult(final ScanResult scanResult, final Throwable throwable) {
        this.futureScanResult = null;
        this.fileScanResult = scanResult;
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

    public ModFileInfo getModFileInfo() {
        return modFileInfo;
    }
    public static class ModFileInfo {
        private final UnmodifiableConfig config;
        private final ModFile modFile;
        private final URL updateJSONURL;
        private final URL issueURL;
        private final String modLoader;
        private final VersionRange modLoaderVersion;
        private final List<ModInfo> mods;
        private final Map<String,Object> properties;

        public ModFileInfo(final ModFile modFile, final UnmodifiableConfig config)
        {
            this.modFile = modFile;
            this.config = config;
            this.modLoader = config.<String>getOptional("modLoader").
                    orElseThrow(()->new InvalidModFileException("Missing ModLoader in file", this));
            this.modLoaderVersion = config.<String>getOptional("loaderVersion").
                    map(VersionRange::createFromVersionSpec).
                    orElseThrow(()->new InvalidModFileException("Missing ModLoader version in file", this));
            this.properties = config.<UnmodifiableConfig>getOptional("properties").
                    map(UnmodifiableConfig::valueMap).orElse(Collections.emptyMap());
            this.modFile.setFileProperties(this.properties);
            final ArrayList<UnmodifiableConfig> modConfigs = config.getOrElse("mods", ArrayList::new);
            if (modConfigs.isEmpty()) {
                throw new InvalidModFileException("Missing mods list", this);
            }
            this.mods = modConfigs.stream().map(mi-> new ModInfo(this, mi)).collect(Collectors.toList());
            this.updateJSONURL = config.<String>getOptional("updateJSONURL").map(StringUtils::toURL).orElse(null);
            this.issueURL = config.<String>getOptional("issueTrackerURL").map(StringUtils::toURL).orElse(null);
        }

        public List<ModInfo> getMods()
        {
            return mods;
        }

        public ModFile getFile()
        {
            return this.modFile;
        }

        public UnmodifiableConfig getConfig()
        {
            return this.config;
        }
    }
}
