package net.minecraftforge.mcp;

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
            vers = System.getProperty("mc.version");
        }
        if (vers == null) throw new RuntimeException("Missing MC version, cannot continue");
        mcVersion = vers;

        vers = MCPVersion.class.getPackage().getImplementationVersion();
        if (vers == null) {
            vers = System.getProperty("mcp.version");
        }
        if (vers == null) throw new RuntimeException("Missing MCP version, cannot continue");
        mcpVersion = vers;
        LOGGER.info(CORE, "Found MC version information {}", mcVersion);
        LOGGER.info(CORE, "Found MCP version information {}", mcpVersion);
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
