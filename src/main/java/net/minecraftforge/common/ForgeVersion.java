/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.common;

import net.minecraftforge.fml.VersionChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static net.minecraftforge.fml.Logging.CORE;

public class ForgeVersion
{
    private static final Logger LOGGER = LogManager.getLogger();
    // This is Forge's Mod Id, used for the ForgeMod and resource locations
    public static final String MOD_ID = "forge";
    // This is the minecraft version we're building for - used in various places in Forge/FML code
    public static final String mcVersion = "1.13";
    // This is the MCP data version we're using
    public static final String mcpVersion = "9.42";

    private static final String forgeVersion;

    private static final String forgeSpec;

    static {
        String vers = ForgeVersion.class.getPackage().getImplementationVersion();
        if (vers == null) {
            vers = System.getProperty("forge.version");
        }
        if (vers == null) throw new RuntimeException("Missing forge version, cannot continue");
        String spec = ForgeVersion.class.getPackage().getSpecificationVersion();
        if (spec == null) {
            spec = System.getProperty("forge.spec");
        }
        if (spec == null) throw new RuntimeException("Missing forge spec, cannot continue");
        forgeVersion = vers;
        forgeSpec = spec;
        LOGGER.info(CORE, "Found Forge version {}", forgeVersion);
        LOGGER.info(CORE, "Found Forge spec {}", forgeSpec);
    }

    public static String getVersion()
    {
        return forgeVersion;
    }

    public static VersionChecker.Status getStatus()
    {
        return VersionChecker.Status.PENDING;
    }

    @Nullable
    public static String getTarget()
    {
        return "";
    }

    public static String getSpec() {
        return forgeSpec;
    }
}

