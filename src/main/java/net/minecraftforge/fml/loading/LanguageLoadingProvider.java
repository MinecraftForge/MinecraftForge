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

package net.minecraftforge.fml.loading;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.fml.language.IModLanguageProvider;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.minecraftforge.fml.Logging.CORE;

public class LanguageLoadingProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<IModLanguageProvider> languageProviders = new ArrayList<>();
    private final ServiceLoader<IModLanguageProvider> serviceLoader;
    private final Map<String, ModLanguageWrapper> languageProviderMap = new HashMap<>();

    public void forEach(final Consumer<IModLanguageProvider> consumer)
    {
        languageProviders.forEach(consumer);
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
        serviceLoader = ServiceLoader.load(IModLanguageProvider.class);
        serviceLoader.forEach(languageProviders::add);

        languageProviders.forEach(lp -> {
            final Package pkg = lp.getClass().getPackage();
            String implementationVersion = pkg.getImplementationVersion();
            if (implementationVersion == null) {
                implementationVersion = ForgeVersion.getVersion();
            }
            LOGGER.debug(CORE, "Found system classpath language provider {}, version {}", lp.name(), implementationVersion);
            languageProviderMap.put(lp.name(), new ModLanguageWrapper(lp, new DefaultArtifactVersion(implementationVersion)));
        });
    }

    public void addAdditionalLanguages(List<ModFile> modFiles)
    {
        if (modFiles==null) return;
        Stream<Path> langPaths = modFiles.stream().map(ModFile::getFilePath);
        serviceLoader.reload();
    }

    public IModLanguageProvider findLanguage(String modLoader, VersionRange modLoaderVersion) {
        final ModLanguageWrapper mlw = languageProviderMap.get(modLoader);
        if (mlw == null) {
            throw new MissingLanguageException("Missing language "+modLoader);
        }
        if (!modLoaderVersion.containsVersion(mlw.getVersion())) {
            throw new MissingLanguageException("Missing language "+ modLoader + " matching range "+modLoaderVersion + " found "+mlw.getVersion());
        }

        return mlw.getModLanguageProvider();
    }
}
