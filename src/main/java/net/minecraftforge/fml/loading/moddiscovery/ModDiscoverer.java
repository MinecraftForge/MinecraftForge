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

import cpw.mods.modlauncher.ServiceLoaderStreamUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.fmlLog;

public class ModDiscoverer {
    private final ServiceLoader<IModLocator> locators;
    private final List<IModLocator> locatorList;

    public ModDiscoverer() {
        locators = ServiceLoader.load(IModLocator.class);
        locatorList = ServiceLoaderStreamUtils.toList(this.locators);
        fmlLog.debug("Found Mod Locators : {}", ()->locatorList.stream().map(IModLocator::name).collect(Collectors.joining(",")));
    }

    ModDiscoverer(List<IModLocator> locatorList) {
        this.locatorList = locatorList;
        this.locators = null;
    }

    public BackgroundScanHandler discoverMods() {
        fmlLog.debug("Scanning for mods and other resources to load. We know {} ways to find mods", locatorList.size());
        final Map<ModFile.Type, List<ModFile>> modFiles = locatorList.stream()
                .peek(loc -> fmlLog.debug("Trying locator {}", loc))
                .map(IModLocator::scanMods)
                .flatMap(Collection::stream)
                .peek(mf -> fmlLog.debug("Found mod file {} of type {} with locator {}", mf.getFileName(), mf.getType(), mf.getLocator()))
                .collect(Collectors.groupingBy(ModFile::getType));

        ModLanguageProvider.loadAdditionalLanguages(modFiles.get(ModFile.Type.LANGPROVIDER));
        BackgroundScanHandler backgroundScanHandler = new BackgroundScanHandler();
        final List<ModFile> mods = modFiles.get(ModFile.Type.MOD);
        mods.forEach(ModFile::identifyMods);
        fmlLog.debug("Found {} mod files with {} mods", mods::size, ()->mods.stream().mapToInt(mf -> mf.getModInfos().size()).sum());
        mods.stream().map(ModFile::getCoreMods).flatMap(List::stream).forEach(ServiceProviders.getCoreModProvider()::addCoreMod);
        mods.forEach(backgroundScanHandler::submitForScanning);
        return backgroundScanHandler;
    }

}
