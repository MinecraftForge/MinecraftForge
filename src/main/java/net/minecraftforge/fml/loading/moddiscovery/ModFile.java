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

import net.minecraftforge.fml.loading.IModLanguageProvider;

import javax.swing.text.html.Option;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
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


    public void claimLanguage(String modId, IModLanguageProvider.IModLanguageLoader loader)
    {
        this.modInfoMap.get(modId).setLoader(loader);
    }

    public enum Type {
        MOD, LIBRARY, LANGPROVIDER
    }
    private final Path filePath;
    private final Type modFileType;
    private final Manifest manifest;
    private List<ModInfo> modInfos;
    private Map<String, ModInfo> modInfoMap;
    private final IModLocator locator;
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
    }

    public Type getType() {
        return modFileType;
    }

    public Path getFilePath() {
        return filePath;
    }

    public List<ModInfo> getModInfos() {
        return modInfos;
    }

    public Optional<Path> getAccessTransformer() {
        return Optional.ofNullable(Files.exists(accessTransformer) ? accessTransformer : null);
    }
    public void identifyMods() {
        this.modInfos = ModFileParser.readModList(this);
        this.modInfos.forEach(mi-> fmlLog.debug(LOADING,"Found mod {} for language {}", mi.getModId(), mi.getModLoader()));
        this.modInfoMap = this.modInfos.stream().collect(Collectors.toMap(ModInfo::getModId, Function.identity()));
        this.coreMods = ModFileParser.getCoreMods(this);
        this.coreMods.forEach(mi-> fmlLog.debug(LOADING,"Found coremod {}", mi.getPath()));
        this.accessTransformer = locator.findPath(this, "META-INF", "accesstransformer.cfg");
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
}
