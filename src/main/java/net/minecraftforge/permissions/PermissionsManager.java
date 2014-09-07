package net.minecraftforge.permissions;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.permissions.api.IPermissionsProvider;
import net.minecraftforge.permissions.api.context.IContext;
import net.minecraftforge.permissions.opbasedimpl.OpPermSystem;

/**
 * The main entry point for the Permissions API.
 * At most, mods should only use this class.
 *
 */
public final class PermissionsManager
{
    private static IPermissionsProvider FACTORY = new OpPermSystem();
    
    private PermissionsManager(){}
    
    public static void initialize()
    {
        setPermProvider(null);
    }

    private static       boolean            wasSet  = false;
    
    /**
     * Check a permission with no contexts
     * @param player The EntityPlayer being checked
     * @param node The permission node to be checked
     * @return true if permission allowed, false if not
     */
    public static boolean checkPerm(EntityPlayer player, String node)
    {
        ImmutableMap map = ImmutableMap.of();
        return FACTORY.checkPerm(player, node, map);
    }
    
    /**
     * Check a permission with contexts
     * @param player
     * @param node
     * @param contextInfo An ImmutableMap with your contexts. 
     * 
     * To build an ImmutableMap of contexts, you will need to call:
     * ImmutableMap.of("key", val, "key2", val2); etc where keyX is your key and val is the context.
     * 
     * The following are the standard keys to which the values should be assigned.
     * sourcePlayer, targetPlayer, sourceLocation, targetLocation,
     * sourceEntity, targetEntity, sourceTileEntity, targetTileEntity, sourceWorld, targetWorld
     * 
     * It is not necessary for the blackboard to contain an entry for every one of the above keys. Instead it should contain only one or two entries with the that best describes the value.
     * @return true if permission allowed, false if not
     */
    public static boolean checkPerm(EntityPlayer player, String node, Map<String, IContext> contextInfo)
    {
        return FACTORY.checkPerm(player, node, contextInfo);
    }

    /**
     * Get the factory
     * FOR ADVANCED OPERATIONS
     * @return the current permission implementor
     */
    public static IPermissionsProvider getPermProvider()
    {
        return FACTORY;
    }
    
    /**
     * This is where permissions are registered with their default value.
     * If you want to register permissions for commands, please use the methods in CommandHandlerForge instead.
     * @param perms
     */
    public static void registerPermission(String node, RegisteredPermValue allow)
    {
        FACTORY.registerPermission(node, allow);
    }

    /**
     * Framework authors:
     * Call this method in preinit, and only preinit.
     * You will not receive the registration call for vanilla permissions if you register your framework after preinit.
     * @param factory your permission framework class implementing IPermissionsProvider
     * @throws IllegalStateException
     */
    public static void setPermProvider(IPermissionsProvider factory) throws IllegalStateException
    {
        if (factory == null)
        {
            wasSet = false;
        }
        else if (wasSet)
        {
            throw new IllegalStateException(String.format("Attempted to register permissions framework %s1 when permissions framework %s2 is already registered!", factory.getName(), FACTORY.getName()));
        }
        else
        {
            FACTORY = factory;
            wasSet = true;
            FMLLog.fine("Registered permissions framework " + FACTORY.getName());
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
        
        public static RegisteredPermValue fromString(String name)
        {
            for (RegisteredPermValue value : values())
            {
                if (value.name().equalsIgnoreCase(name)) return value;
            }
            return null;
        }
    }
    
    public static class UnregisteredPermissionException extends RuntimeException
    {
        public final String node;
        public UnregisteredPermissionException(String node)
        {
            super("Unregistered Permission encountered! "+node);
            this.node = node;
        }
    }
}
