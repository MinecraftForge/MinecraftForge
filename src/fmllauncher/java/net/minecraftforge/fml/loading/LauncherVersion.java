package net.minecraftforge.fml.loading;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class LauncherVersion {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String launcherVersion;

    static {
        String vers = JarVersionLookupHandler.getImplementationVersion(LauncherVersion.class).orElse(System.getProperty("fmllauncher.version"));
        if (vers == null) throw new RuntimeException("Missing FMLLauncher version, cannot continue");
        launcherVersion = vers;
        LOGGER.info(CORE, "Found FMLLauncher version {}", launcherVersion);
    }

    public static String getVersion()
    {
        return launcherVersion;
    }
}
