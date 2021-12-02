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

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.util.ServiceLoaderUtils;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

public class ModDiscoverer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServiceLoader<IModLocator> locators;
    private final List<IModLocator> locatorList;
    private final Set<String> coreGameMods;

    @SuppressWarnings("unchecked")
    public ModDiscoverer(Map<String, ?> arguments) {
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Environment.Keys.MODDIRECTORYFACTORY.get(), v->ModsFolderLocator::new);
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Environment.Keys.PROGRESSMESSAGE.get(), v-> StartupMessageManager.locatorConsumer().orElseGet(()-> s->{}));
        final var moduleLayerManager = Launcher.INSTANCE.environment().findModuleLayerManager().orElseThrow();
        locators = ServiceLoader.load(moduleLayerManager.getLayer(IModuleLayerManager.Layer.SERVICE).orElseThrow(), IModLocator.class);
        locatorList = ServiceLoaderUtils.streamServiceLoader(()->locators, sce->LOGGER.error("Failed to load locator list", sce)).collect(Collectors.toList());
        locatorList.forEach(l->l.initArguments(arguments));
        LOGGER.debug(LogMarkers.CORE,"Found Mod Locators : {}", ()->locatorList.stream().map(iModLocator -> "("+iModLocator.name() + ":" + iModLocator.getClass().getPackage().getImplementationVersion()+")").collect(Collectors.joining(",")));
        coreGameMods = Set.copyOf(((Map<String, List<String>>) arguments).getOrDefault("coreGameMods", List.of()));
        LOGGER.debug(LogMarkers.CORE, "Configured core game mods: {}", String.join(",", coreGameMods));
    }

    ModDiscoverer(List<IModLocator> locatorList) {
        this.locatorList = locatorList;
        this.locators = null;
        this.coreGameMods = Set.of();
    }

    public ModValidator discoverMods() {
        LOGGER.debug(LogMarkers.SCAN,"Scanning for mods and other resources to load. We know {} ways to find mods", locatorList.size());
        var loadedFiles = new ArrayList<>();
        for (IModLocator locator : locatorList) {
            LOGGER.debug(LogMarkers.SCAN,"Trying locator {}", locator);
            var modFiles = locator.scanMods();
            for (IModFile mf : modFiles) {
                LOGGER.info(LogMarkers.SCAN, "Found mod file {} of type {} with locator {}", mf.getFileName(), mf.getType(), mf.getLocator());
                StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Found mod file "+mf.getFileName()+" of type "+mf.getType()));
            }
            loadedFiles.addAll(modFiles);
        }
        final var modFilesMap = loadedFiles.stream()
                .map(ModFile.class::cast)
                .collect(Collectors.groupingBy(IModFile::getType));

        var validator = new ModValidator(modFilesMap, coreGameMods);
        validator.stage1Validation();
        return validator;
    }

}
