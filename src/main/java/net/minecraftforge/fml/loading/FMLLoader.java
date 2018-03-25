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

import java.util.Set;

import static net.minecraftforge.fml.Logging.fmlLog;

public class FMLLoader
{

    private static ILaunchPluginService accessTransformer;
    private static ModDiscoverer modDiscoverer;

    static void initialize(IEnvironment environment, Set<String> otherServices) throws IncompatibleEnvironmentException
    {
        final String version = ForgeVersion.getVersion();
        fmlLog.debug("FML {} loading", version);
        final Package modLauncherPackage = ITransformationService.class.getPackage();
        fmlLog.debug("FML found ModLauncher version : {}", modLauncherPackage.getImplementationVersion());
        if (!modLauncherPackage.isCompatibleWith("1.0")) {
            fmlLog.error("Found incompatible ModLauncher specification : {}, version {} from {}", modLauncherPackage.getSpecificationVersion(), modLauncherPackage.getImplementationVersion(), modLauncherPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible modlauncher found "+modLauncherPackage.getSpecificationVersion());
        }

        accessTransformer = environment.findLaunchPlugin("accesstransformer").orElseThrow(()-> new IncompatibleEnvironmentException("Missing AccessTransformer, cannot run"));

        final Package atPackage = accessTransformer.getClass().getPackage();
        fmlLog.debug("FML found AccessTransformer version : {}", atPackage.getImplementationVersion());
        if (!atPackage.isCompatibleWith("1.0")) {
            fmlLog.error("Found incompatible AccessTransformer specification : {}, version {} from {}", atPackage.getSpecificationVersion(), atPackage.getImplementationVersion(), atPackage.getImplementationVendor());
        }
//        final ILaunchPluginService coreMod = environment.findLaunchPlugin("coremod").orElseThrow(()-> new IncompatibleEnvironmentException("Missing CoreMod, cannot run"));

        fmlLog.debug("Scanning for Mod Locators");
        modDiscoverer = new ModDiscoverer();
    }
}
