/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.*;
import net.minecraftsingularity.fml.loading.moddiscovery.ModFile;
import net.minecraftsingularity.singularityspi.Environment;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import static net.minecraftsingularity.fml.loading.LogMarkers.CORE;

public class FMLServiceProvider implements ITransformationService {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, Object> arguments = new HashMap<>();

    public FMLServiceProvider() {
        var markers = System.getProperty("singularity.logging.markers", "").split(",");
        for (var marker : markers)
            System.setProperty("singularity.logging.marker." + marker.toLowerCase(Locale.ROOT), "ACCEPT");
    }

    @Override
    public String name() {
        return "fml";
    }

    @Override
    public void initialize(IEnvironment environment) {
        LOGGER.debug(CORE, "Setting up basic FML game directories");
        FMLPaths.setup(environment);
        LOGGER.debug(CORE, "Loading configuration");
        FMLConfig.load();
        LOGGER.debug(CORE, "Preparing ModFile");
        environment.computePropertyIfAbsent(Environment.Keys.MODFILEFACTORY.get(), k->ModFile::new);
        LOGGER.debug(CORE, "Preparing launch handler");
        FMLLoader.setupLaunchHandler(environment, arguments);
        FMLEnvironment.setupInteropEnvironment(environment);
        Environment.build(environment);
    }

    @Override
    public List<Resource> beginScanning(final IEnvironment environment) {
        LOGGER.debug(CORE,"Initiating mod scan");
        return FMLLoader.beginModScan(arguments);
    }

    @Override
    public List<Resource> completeScan(final IModuleLayerManager layerManager) {
        return FMLLoader.completeScan(layerManager);
    }

    @Override
    public void onLoad(IEnvironment environment, Set<String> otherServices) throws IncompatibleEnvironmentException {
        FMLLoader.onInitialLoad(environment, otherServices);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public @NotNull List<ITransformer> transformers() {
        return List.of();
    }
}
