/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class LauncherVersion {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String launcherVersion;

    static {
        String vers = JarVersionLookupHandler.getImplementationVersion(LauncherVersion.class).orElse(System.getenv("LAUNCHER_VERSION"));
        if (vers == null) throw new RuntimeException("Missing FMLLauncher version, cannot continue");
        launcherVersion = vers;
        LOGGER.debug(CORE, "Found FMLLauncher version {}", launcherVersion);
    }

    public static String getVersion()
    {
        return launcherVersion;
    }
}
