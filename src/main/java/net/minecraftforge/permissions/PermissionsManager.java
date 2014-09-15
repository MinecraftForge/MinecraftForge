package net.minecraftforge.permissions;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

/**
 * The main entry point for the Permissions API.
 * At most, mods should only use this class.
 *
 */
public final class PermissionsManager
{
    private static IPermissionsProvider provider = new DefaultPermProvider();
    
    private static boolean wasSet = false;
    
    private PermissionsManager(){}
    
    /**
     * Checks a permission
     * 
     * @param player
     *            The player to use as a context
     * @param permissionNode
     *            The permission node, that should be checked
     * @return Whether the permission is allowed
     */
    public static boolean checkPermission(EntityPlayer player, String permissionNode)
    {
        return provider.checkPermission(new PermissionContext().setPlayer(player), permissionNode);
    }

    /**
     * Checks a permission
     * 
     * @param context
     *            Context, where the permission is checked in.
     * @param permissionNode
     *            The permission node, that should be checked
     * @return Whether the permission is allowed
     */
    public static boolean checkPermission(IContext contextInfo, String permissionNode)
    {
        return provider.checkPermission(contextInfo, permissionNode);
    }

    /**
     * This is where permissions are registered with their default value.
     * 
     * @param permissionNode
     * @param level
     *            Default level of the permission. This can be used to tell the
     *            underlying {@link IPermissionsProvider} whether a player
     *            should be allowed to access this permission by default, or as
     *            operator only.
     */
    public static void registerPermission(String permissionNode, RegisteredPermValue level)
    {
        provider.registerPermission(permissionNode, level);
    }

    /**
     * Framework authors: <br>
     * Call this method to register your own implementation of
     * {@link IPermissionsProvider}. <br>
     * <br>
     * Registers a new {@link IPermissionsProvider} to replace the default
     * implementation. This method can only called in
     * {@link cpw.mods.fml.common.event.FMLPreInitializationEvent}, otherwise an
     * {@link IllegalStateException} will be thrown.
     * 
     * @param provider
     *            The new instance that should replace the default
     *            implementation of {@link IPermissionsProvider}
     * @throws IllegalStateException
     */
    public static void setPermProvider(IPermissionsProvider factory) throws IllegalStateException
    {
        if (Loader.instance().hasReachedState(LoaderState.INITIALIZATION))
        {
            throw new IllegalStateException(String.format("Attempted to register permissions framework %s after preinit! This is not allowed!", factory.getClass().getName()));
        }
        if (factory == null)
        {
            throw new IllegalArgumentException("Attempted to register a null provider - this is not allowed!");
        }
        else if (wasSet)
        {
            throw new IllegalStateException(String.format("Attempted to register permissions framework %s1 when permissions framework %s2 is already registered!", factory.getClass().getName(), provider.getClass().getName()));
        }
        else
        {
            provider = factory;
            wasSet = true;
            FMLLog.fine("Registered permissions framework " + provider.getClass().getName());
        }
    }
    
    /**
     * Based on Bukkit's PermissionDefault system.
     * Accepted names: True, False, Op
     *
     */
    public static enum RegisteredPermValue
    {
        TRUE, FALSE, OP;
        
        public static RegisteredPermValue fromBoolean(boolean toConvert)
        {
            if (toConvert) return TRUE;
            else return FALSE;
        }
    }
}
