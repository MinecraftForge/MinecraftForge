package net.minecraftforge.permissions;

/**
 * The Minecraft Forge Permissions API
 * <p>
 * Permission mods should register a {@link IPermissionHandler
 * PermissionHandler} instance during
 * {@link cpw.mods.fml.common.event.FMLPreInitializationEvent PreInit}
 * <p>
 * Mods should register commands and permissions during
 * {@link cpw.mods.fml.common.event.FMLServerStartingEvent ServerStarting
 */
public class PermissionsManager {

    private static IPermissionHandler permHandler = null;

    public static synchronized IPermissionHandler getPermHandler()
    {
        if (permHandler == null)
        {
            // If no mods have registered, create the default instance
            permHandler = new DefaultPermHandler();
        }

        return permHandler;
    }

    /**
     * Set the permission handler framework
     * <p>
     * <b>This should be called during
     * {@link cpw.mods.fml.common.event.FMLPreInitializationEvent PreInit}</b>
     * 
     * @param handler
     *            the Permission Handler
     */
    public static void setPermHandler(IPermissionHandler handler)
    {
        permHandler = handler;
    }
}
