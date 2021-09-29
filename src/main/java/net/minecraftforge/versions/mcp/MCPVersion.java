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

package net.minecraftforge.versions.mcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.JarVersionLookupHandler;

import static net.minecraftforge.fml.Logging.CORE;

public class MCPVersion {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String mcVersion;
    private static final String mcpVersion;
    static {
        LOGGER.debug(CORE, "MCP Version package {} from {}", MCPVersion.class.getPackage(), MCPVersion.class.getClassLoader());
        mcVersion = JarVersionLookupHandler.getSpecificationVersion(MCPVersion.class).orElse(FMLLoader.versionInfo().mcVersion());
        if (mcVersion == null) throw new RuntimeException("Missing MC version, cannot continue");

        mcpVersion = JarVersionLookupHandler.getImplementationVersion(MCPVersion.class).orElse(FMLLoader.versionInfo().mcpVersion());
        if (mcpVersion == null) throw new RuntimeException("Missing MCP version, cannot continue");

        LOGGER.debug(CORE, "Found MC version information {}", mcVersion);
        LOGGER.debug(CORE, "Found MCP version information {}", mcpVersion);
    }
    public static String getMCVersion() {
        return mcVersion;
    }

    public static String getMCPVersion() {
        return mcpVersion;
    }

    public static String getMCPandMCVersion()
    {
        return mcVersion+"-"+mcpVersion;
    }
}
