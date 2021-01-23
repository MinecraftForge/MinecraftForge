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

package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.ServiceLoaderStreamUtils;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.fml.loading.moddiscovery.ExplodedDirectoryLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
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

import static cpw.mods.modlauncher.api.LamdbaExceptionUtils.rethrowFunction;
import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class LanguageLoadingProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final LanguageClassLoader languageClassLoader;
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
    private static class LanguageClassLoader extends URLClassLoader
    {
        public LanguageClassLoader() {
            super(new URL[0]);
        }

        @Override
        public void addURL(final URL url) {
            LOGGER.debug(CORE, "Adding {} to languageloader classloader", url);
            super.addURL(url);
        }

    }
    LanguageLoadingProvider() {
        languageClassLoader = new LanguageClassLoader();
        serviceLoader = ServiceLoader.load(IModLanguageProvider.class, languageClassLoader);
        loadLanguageProviders();
    }
    private void loadLanguageProviders() {
        LOGGER.debug(CORE, "Found {} language providers", ServiceLoaderStreamUtils.toList(serviceLoader).size());
        serviceLoader.forEach(languageProviders::add);

        languageProviders.forEach(lp -> {
            final Path lpPath;
            try {
                lpPath = Paths.get(lp.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Huh?", e);
            }
            Optional<String> implementationVersion = JarVersionLookupHandler.getImplementationVersion(lp.getClass());
            String impl = implementationVersion.orElse(Files.isDirectory(lpPath) ? FMLLoader.forgeVersion.split("\\.")[0] : null);
            if (impl == null) {
                LOGGER.fatal(CORE, "Found unversioned language provider {}", lp.name());
                throw new RuntimeException("Failed to find implementation version for language provider "+ lp.name());
            }
            LOGGER.debug(CORE, "Found language provider {}, version {}", lp.name(), impl);
            StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Loaded language provider "+lp.name()+ " " + impl));
            languageProviderMap.put(lp.name(), new ModLanguageWrapper(lp, new DefaultArtifactVersion(impl)));
        });
    }

    void addForgeLanguage(final Path forgePath) {
        if (!languageProviderMap.containsKey("javafml")) {
            LOGGER.debug(CORE,"Adding Forge as a language from {}", forgePath.toString());
            addLanguagePaths(Stream.of(forgePath));
            serviceLoader.reload();
            loadLanguageProviders();
        } else {
            LOGGER.debug(CORE, "Skipping adding Forge jar - javafml is already present");
        }
    }

    private void addLanguagePaths(final Stream<Path> langPaths) {
        languageProviders.clear();
        languageProviderMap.clear();
        langPaths.peek(languagePaths::add).map(Path::toFile).map(File::toURI).map(rethrowFunction(URI::toURL)).forEach(languageClassLoader::addURL);
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
        final String languageFileName = mf.getLocator() instanceof ExplodedDirectoryLocator ? "in-development" : mf.getFileName();
        final ModLanguageWrapper mlw = languageProviderMap.get(modLoader);
        if (mlw == null) {
            LOGGER.error("Missing language {} version {} wanted by {}", modLoader, modLoaderVersion, languageFileName);
            throw new EarlyLoadingException("Missing language "+modLoader, null, Collections.singletonList(new EarlyLoadingException.ExceptionData("fml.language.missingversion", modLoader, modLoaderVersion, languageFileName, "null")));
        }
        if (!VersionSupportMatrix.testVersionSupportMatrix(modLoaderVersion, modLoader, "languageloader", (llid, range) -> range.containsVersion(mlw.getVersion()))) {
            LOGGER.error("Missing language {} version {} wanted by {}, found {}", modLoader, modLoaderVersion, languageFileName, mlw.getVersion());
            throw new EarlyLoadingException("Missing language "+ modLoader + " matching range "+modLoaderVersion + " found "+mlw.getVersion(), null, Collections.singletonList(new EarlyLoadingException.ExceptionData("fml.language.missingversion", modLoader, modLoaderVersion, languageFileName, mlw.getVersion())));
        }

        return mlw.getModLanguageProvider();
    }
}
