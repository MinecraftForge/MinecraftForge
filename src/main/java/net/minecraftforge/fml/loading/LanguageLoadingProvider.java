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

import net.minecraftforge.fml.loading.moddiscovery.ModFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.Logging.fmlLog;

public class LanguageLoadingProvider
{
    private final List<IModLanguageProvider> languageProviders = new ArrayList<>();
    private final ServiceLoader<IModLanguageProvider> serviceLoader;
    private final Map<String, IModLanguageProvider> languageProviderMap = new HashMap<>();

    LanguageLoadingProvider() {
        serviceLoader = ServiceLoader.load(IModLanguageProvider.class);
        serviceLoader.forEach(languageProviders::add);

        languageProviders.forEach(lp -> {
            final Package pkg = lp.getClass().getPackage();
            fmlLog.debug(CORE, "Found system classpath language provider {}, version {}", lp.name(), pkg.getImplementationVersion());
        });

        languageProviders.forEach(lp->languageProviderMap.put(lp.name(), lp));
    }

    public void addAdditionalLanguages(List<ModFile> modFiles)
    {
        if (modFiles==null) return;
        Stream<Path> langPaths = modFiles.stream().map(ModFile::getFilePath);
        serviceLoader.reload();
    }

    public IModLanguageProvider getLanguage(String name)
    {
        return languageProviderMap.get(name);
    }
}
