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
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;
import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public class ModDiscoverer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServiceLoader<IModLocator>        locators;
    private final ServiceLoader<IDependencyLocator> dependencyLocators;
    private final List<IModLocator>                 locatorList;
    private final List<IDependencyLocator> dependencyLocatorList;

    public ModDiscoverer(Map<String, ?> arguments) {
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Environment.Keys.MODDIRECTORYFACTORY.get(), v->ModsFolderLocator::new);
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Environment.Keys.PROGRESSMESSAGE.get(), v->StartupMessageManager.locatorConsumer().orElseGet(()->s->{}));
        final var moduleLayerManager = Launcher.INSTANCE.environment().findModuleLayerManager().orElseThrow();
        locators = ServiceLoader.load(moduleLayerManager.getLayer(IModuleLayerManager.Layer.SERVICE).orElseThrow(), IModLocator.class);
        locatorList = ServiceLoaderUtils.streamServiceLoader(()->locators, sce->LOGGER.error("Failed to load locator list", sce)).collect(Collectors.toList());
        locatorList.forEach(l->l.initArguments(arguments));
        LOGGER.debug(CORE,"Found Mod Locators : {}", ()->locatorList.stream().map(iModLocator -> "("+iModLocator.name() + ":" + iModLocator.getClass().getPackage().getImplementationVersion()+")").collect(Collectors.joining(",")));
        dependencyLocators = ServiceLoader.load(moduleLayerManager.getLayer(IModuleLayerManager.Layer.SERVICE).orElseThrow(), IDependencyLocator.class);
        dependencyLocatorList = ServiceLoaderUtils.streamServiceLoader(()->dependencyLocators, sce->LOGGER.error("Failed to load dependency locator list", sce)).collect(Collectors.toList());
        dependencyLocatorList.forEach(l->l.initArguments(arguments));
        LOGGER.debug(CORE,"Found Dependency Locators : {}", ()->dependencyLocatorList.stream().map(iDependencyLocator -> "("+iDependencyLocator.name() + ":" + iDependencyLocator.getClass().getPackage().getImplementationVersion()+")").collect(Collectors.joining(",")));
    }

    ModDiscoverer(final List<IModLocator> locatorList)
    {
        this.locatorList = locatorList;
        this.locators = null;
        this.dependencyLocatorList = new ArrayList<>();
        this.dependencyLocators = null;
    }

    ModDiscoverer(List<IModLocator> locatorList, List<IDependencyLocator> dependencyLocatorList) {
        this.locatorList = locatorList;
        this.locators = null;
        this.dependencyLocatorList = dependencyLocatorList;
        this.dependencyLocators = null;
    }

    public ModValidator discoverMods() {
        LOGGER.debug(SCAN,"Scanning for mods and other resources to load. We know {} ways to find mods", locatorList.size());
        var initialLoadedFiles = new ArrayList<IModFile>();
        for (IModLocator locator : locatorList) {
            LOGGER.debug(SCAN,"Trying locator {}", locator);
            var modFiles = locator.scanMods();
            for (IModFile mf : modFiles) {
                LOGGER.info(SCAN, "Found mod file {} of type {} with locator {}", mf.getFileName(), mf.getType(), mf.getProvider());
                StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Found mod file "+mf.getFileName()+" of type "+mf.getType()));
            }
            initialLoadedFiles.addAll(modFiles);
        }
        final var initialModFilesMap = initialLoadedFiles.stream()
          .map(ModFile.class::cast)
          .collect(Collectors.groupingBy(IModFile::getType));

        //Perform initial validation
        var initialValidator = new ModValidator(initialModFilesMap);
        var initialValidModFiles = initialValidator.getInitialValidModFiles();

        var additionalLoadedFiles = new ArrayList<IModFile>();
        for (IDependencyLocator locator : dependencyLocatorList) {
            LOGGER.debug(SCAN,"Trying dependency locator {}", locator);
            var modFiles = locator.scanMods(initialValidModFiles);
            for (IModFile mf : modFiles) {
                LOGGER.info(SCAN, "Found dependency file {} of type {} with locator {}", mf.getFileName(), mf.getType(), mf.getProvider());
                StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Found dependency file "+mf.getFileName()+" of type "+mf.getType()));
            }
            additionalLoadedFiles.addAll(modFiles);
        }

        var loadedFiles = new ArrayList<>(initialLoadedFiles);
        loadedFiles.addAll(additionalLoadedFiles);

        final var modFilesMap = loadedFiles.stream()
          .map(ModFile.class::cast)
          .collect(Collectors.groupingBy(IModFile::getType));

        var validator = new ModValidator(modFilesMap);
        validator.stage1Validation();

        return validator;
    }

}
