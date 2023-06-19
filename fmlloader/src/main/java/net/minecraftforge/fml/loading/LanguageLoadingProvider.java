/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.util.ServiceLoaderUtils;
import net.minecraftforge.fml.loading.progress.StartupNotificationManager;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.fml.loading.moddiscovery.ExplodedDirectoryLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.slf4j.Logger;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;
import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

public class LanguageLoadingProvider
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<IModLanguageProvider> languageProviders = new ArrayList<>();
    private final ServiceLoader<IModLanguageProvider> serviceLoader;
    private final Map<String, ModLanguageWrapper> languageProviderMap = new HashMap<>();
    private List<Path> languagePaths = new ArrayList<>();

    public void forEach(final Consumer<IModLanguageProvider> consumer)
    {
        languageProviders.forEach(consumer);
    }

    public <T> Stream<T> applyForEach(final Function<IModLanguageProvider, T> function) {
        return languageProviders.stream().map(function);
    }

    private static class ModLanguageWrapper {
        private final IModLanguageProvider modLanguageProvider;

        private final ArtifactVersion version;
        public ModLanguageWrapper(IModLanguageProvider modLanguageProvider, ArtifactVersion version)
        {
            this.modLanguageProvider = modLanguageProvider;
            this.version = version;
        }
        public ArtifactVersion getVersion()
        {
            return version;
        }

        public IModLanguageProvider getModLanguageProvider()
        {
            return modLanguageProvider;
        }


    }

    LanguageLoadingProvider() {
        var sl = Launcher.INSTANCE.environment().findModuleLayerManager().flatMap(lm->lm.getLayer(IModuleLayerManager.Layer.PLUGIN)).orElseThrow();
        serviceLoader = ServiceLoader.load(sl, IModLanguageProvider.class);
        loadLanguageProviders();
    }
    private void loadLanguageProviders() {
        LOGGER.debug(CORE, "Found {} language providers", ServiceLoaderUtils.streamServiceLoader(()->serviceLoader, sce->LOGGER.error("Problem with language loaders")).count());
        serviceLoader.forEach(languageProviders::add);
        ImmediateWindowHandler.updateProgress("Loading language providers");
        languageProviders.forEach(lp -> {
            final Path lpPath;
            try {
                lpPath = Paths.get(lp.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Huh?", e);
            }
            Optional<String> implementationVersion = JarVersionLookupHandler.getImplementationVersion(lp.getClass());
            String impl = implementationVersion.orElse(Files.isDirectory(lpPath) ? FMLLoader.versionInfo().forgeVersion().split("\\.")[0] : null);
            if (impl == null) {
                LOGGER.error(CORE, "Found unversioned language provider {}", lp.name());
                throw new RuntimeException("Failed to find implementation version for language provider "+ lp.name());
            }
            LOGGER.debug(CORE, "Found language provider {}, version {}", lp.name(), impl);
            ImmediateWindowHandler.updateProgress("Loaded language provider "+lp.name()+ " " + impl);
            languageProviderMap.put(lp.name(), new ModLanguageWrapper(lp, new DefaultArtifactVersion(impl)));
        });
    }

    void addForgeLanguage(final Path forgePath) {
        if (!languageProviderMap.containsKey("javafml")) {
            LOGGER.debug(CORE,"Adding forge as a language from {}", forgePath.toString());
            addLanguagePaths(Stream.of(forgePath));
            serviceLoader.reload();
            loadLanguageProviders();
        } else {
            LOGGER.debug(CORE, "Skipping adding forge jar - javafml is already present");
        }
    }

    private void addLanguagePaths(final Stream<Path> langPaths) {
        languageProviders.clear();
        languageProviderMap.clear();
//        langPaths.peek(languagePaths::add).map(Path::toFile).map(File::toURI).map(rethrowFunction(URI::toURL)).forEach(languageClassLoader::addURL);
    }

    public void addAdditionalLanguages(List<ModFile> modFiles)
    {
        if (modFiles==null) return;
        Stream<Path> langPaths = modFiles.stream().map(ModFile::getFilePath);
        addLanguagePaths(langPaths);
        serviceLoader.reload();
        loadLanguageProviders();
    }

    Stream<Path> getLibraries() {
        return languagePaths.stream();
    }

    public IModLanguageProvider findLanguage(ModFile mf, String modLoader, VersionRange modLoaderVersion) {
        final String languageFileName = mf.getProvider() instanceof ExplodedDirectoryLocator ? "in-development" : mf.getFileName();
        final ModLanguageWrapper mlw = languageProviderMap.get(modLoader);
        if (mlw == null) {
            LOGGER.error(LOADING,"Missing language {} version {} wanted by {}", modLoader, modLoaderVersion, languageFileName);
            throw new EarlyLoadingException("Missing language "+modLoader, null, Collections.singletonList(new EarlyLoadingException.ExceptionData("fml.language.missingversion", modLoader, modLoaderVersion, languageFileName, "null")));
        }
        if (!VersionSupportMatrix.testVersionSupportMatrix(modLoaderVersion, modLoader, "languageloader", (llid, range) -> range.containsVersion(mlw.getVersion()))) {
            LOGGER.error(LOADING,"Missing language {} version {} wanted by {}, found {}", modLoader, modLoaderVersion, languageFileName, mlw.getVersion());
            throw new EarlyLoadingException("Missing language "+ modLoader + " matching range "+modLoaderVersion + " found "+mlw.getVersion(), null, Collections.singletonList(new EarlyLoadingException.ExceptionData("fml.language.missingversion", modLoader, modLoaderVersion, languageFileName, mlw.getVersion())));
        }

        return mlw.getModLanguageProvider();
    }
}
