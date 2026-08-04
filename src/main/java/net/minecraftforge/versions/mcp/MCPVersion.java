/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.versions.mcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.fml.Logging.CORE;

public class MCPVersion {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String mcVersion;
    private static final String mcpVersion;
    static {
        String vers = MCPVersion.class.getPackage().getSpecificationVersion();
        if (vers == null) {
            vers = System.getenv("MC_VERSION");
        }
        if (vers == null) throw new RuntimeException("Missing MC version, cannot continue");
        mcVersion = vers;

        vers = MCPVersion.class.getPackage().getImplementationVersion();
        if (vers == null) {
            vers = System.getenv("MCP_VERSION");
        }
        if (vers == null) throw new RuntimeException("Missing MCP version, cannot continue");
        mcpVersion = vers;
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
