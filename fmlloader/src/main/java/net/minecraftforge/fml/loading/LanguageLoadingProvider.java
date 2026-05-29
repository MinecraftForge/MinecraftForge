/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IModuleLayerManager.Layer;
import net.minecraftsingularity.singularityspi.language.IModLanguageProvider;
import net.minecraftsingularity.fml.loading.moddiscovery.ModFile;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.minecraftsingularity.fml.loading.LogMarkers.CORE;
import static net.minecraftsingularity.fml.loading.LogMarkers.LOADING;

public final class LanguageLoadingProvider {
    public static void forEach(Consumer<IModLanguageProvider> consumer) {
        providers.forEach(consumer);
    }

    public static <T> Stream<T> applyForEach(Function<IModLanguageProvider, T> function) {
        return providers.stream().map(function);
    }

    public static void reload() {
        loadLanguageProviders();
    }

    /*==========================================================================*
     *                        INTERNAL                                          *
     *==========================================================================*/
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ServiceLoader<IModLanguageProvider> serviceLoader;
    private static final ArrayList<IModLanguageProvider> providers = new ArrayList<>();
    private static final HashMap<String, Wrapper> providersByName = new HashMap<>();
    private static final DefaultArtifactVersion IN_DEV = new DefaultArtifactVersion("0.0-dev");

    private record Wrapper(ArtifactVersion version, IModLanguageProvider service) {}

    static {
        var sl = Launcher.INSTANCE.environment().findModuleLayerManager().flatMap(lm -> lm.getLayer(Layer.PLUGIN)).orElseThrow();
        serviceLoader = ServiceLoader.load(sl, IModLanguageProvider.class);
    }

    private static void loadLanguageProviders() {
        providers.clear();
        providersByName.clear();

        serviceLoader.reload();
        for (var itr = serviceLoader.iterator(); itr.hasNext(); ) {
            try {
                var srvc = itr.next();
                providers.add(srvc);
            } catch (ServiceConfigurationError sce) {
                LOGGER.error(CORE, "Failed to load IModLauguageProvider Service: " + sce.getMessage());
            }
        }

        LOGGER.debug(CORE, "Found {} language providers", providers.size());

        ImmediateWindowHandler.updateProgress("Loading language providers");
        for (var provider : providers) {
            var info = JarVersionLookupHandler.getInfo(provider.getClass());

            if (info.impl().version().isEmpty() && FMLEnvironment.production) {
                LOGGER.error(CORE, "Found unversioned language provider {}: {}", provider.name(), provider.getClass().getName());
                throw new RuntimeException("Failed to find implementation version for language provider " + provider.name() + " of type " + provider.getClass().getName());
            }

            var version = info.impl().version().map(DefaultArtifactVersion::new).orElse(IN_DEV);

            LOGGER.debug(CORE, "Found language provider {}, version {}", provider.name(), version);
            ImmediateWindowHandler.updateProgress("Loaded language provider " + provider.name() + " " + version);
            providersByName.put(provider.name(), new Wrapper(version, provider));
        }
    }

    public static IModLanguageProvider findLanguage(ModFile mod, String language, VersionRange versionRange) {
        var consumer = mod.getFileName();
        var wrapper = providersByName.get(language);

        if (wrapper == null) {
            LOGGER.error(LOADING, "Missing language {} version {} wanted by {}", language, versionRange, consumer);
            var data = new EarlyLoadingException.ExceptionData("fml.language.missingversion", language, versionRange, consumer, "null");
            throw new EarlyLoadingException("Missing language " + language, null, List.of(data));
        }

        var version = wrapper.version();
        if (version == IN_DEV) {
            LOGGER.debug(LOADING, "Language Provider {} version is unknown, but {} was requested by {}. Proceeding as this is development environement", language, versionRange, consumer);
            return wrapper.service();
        }

        if (!versionRange.containsVersion(version)) {
            LOGGER.error(LOADING, "Missing language {} version {} wanted by {}, found {}", language, versionRange, consumer, version);
            var data = new EarlyLoadingException.ExceptionData("fml.language.missingversion", language, versionRange, consumer, version);
            throw new EarlyLoadingException("Missing language " + language + " matching range " + versionRange + " found " + version, null, List.of(data));
        }

        return wrapper.service();
    }
}
