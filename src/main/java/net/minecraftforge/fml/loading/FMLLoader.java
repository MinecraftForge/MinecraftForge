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

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.loading.moddiscovery.ModDiscoverer;
import net.minecraftforge.forgespi.ICoreModProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.Logging.LOADING;
import static net.minecraftforge.fml.Logging.SCAN;
import static net.minecraftforge.fml.Logging.fmlLog;

public class FMLLoader
{

    private static ILaunchPluginService accessTransformer;
    private static ModDiscoverer modDiscoverer;
    private static ICoreModProvider coreMod;

    static void onInitialLoad(IEnvironment environment, Set<String> otherServices) throws IncompatibleEnvironmentException
    {
        final String version = ForgeVersion.getVersion();
        fmlLog.debug(CORE,"FML {} loading", version);
        final Package modLauncherPackage = ITransformationService.class.getPackage();
        fmlLog.debug(CORE,"FML found ModLauncher version : {}", modLauncherPackage.getImplementationVersion());
        if (!modLauncherPackage.isCompatibleWith("1.0")) {
            fmlLog.error(CORE,"Found incompatible ModLauncher specification : {}, version {} from {}", modLauncherPackage.getSpecificationVersion(), modLauncherPackage.getImplementationVersion(), modLauncherPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible modlauncher found "+modLauncherPackage.getSpecificationVersion());
        }

        accessTransformer = environment.findLaunchPlugin("accesstransformer").orElseThrow(()-> new IncompatibleEnvironmentException("Missing AccessTransformer, cannot run"));

        final Package atPackage = accessTransformer.getClass().getPackage();
        fmlLog.debug(CORE,"FML found AccessTransformer version : {}", atPackage.getImplementationVersion());
        if (!atPackage.isCompatibleWith("1.0")) {
            fmlLog.error(CORE,"Found incompatible AccessTransformer specification : {}, version {} from {}", atPackage.getSpecificationVersion(), atPackage.getImplementationVersion(), atPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible accesstransformer found "+atPackage.getSpecificationVersion());
        }

        final ArrayList<ICoreModProvider> coreModProviders = new ArrayList<>();
        ServiceLoader.load(ICoreModProvider.class).forEach(coreModProviders::add);

        if (coreModProviders.isEmpty()) {
            fmlLog.error(CORE, "Found no coremod provider. Cannot run");
            throw new IncompatibleEnvironmentException("No coremod library found");
        } else if (coreModProviders.size() > 1) {
            fmlLog.error(CORE, "Found multiple coremod providers : {}. Cannot run", coreModProviders.stream().map(p -> p.getClass().getName()).collect(Collectors.toList()));
            throw new IncompatibleEnvironmentException("Multiple coremod libraries found");
        }

        coreMod = coreModProviders.get(0);
        final Package coremodPackage = coreMod.getClass().getPackage();
        fmlLog.debug(CORE,"FML found CoreMod version : {}", coremodPackage.getImplementationVersion());
    }

    public static void load()
    {
        fmlLog.debug(SCAN,"Scanning for Mod Locators");
        modDiscoverer = new ModDiscoverer();
        modDiscoverer.discoverMods();
    }
}
