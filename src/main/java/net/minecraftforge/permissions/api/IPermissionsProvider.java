package net.minecraftforge.permissions.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;
import net.minecraftforge.permissions.api.context.IContext;

import com.google.gson.JsonObject;

/**
 * An interface for permission frameworks to implement.
 * 
 * Framework authors:
 * The class implementing this interface will be called by the API to check permissions.
 * Do all necessary calls here. You can bounce them to helper classes if you want.
 * 
 * If you choose not to support groups, simply make a dummy class implementing IGroup, and return it in all the group getters.
 * 
 */
public interface IPermissionsProvider
{
    /**
     * The name of this permissions provider (usually the modid)
     * @return name Name of permissions provider
     */
    String getName();
    
    /**
     * Check permissions
     * 
     * @param player
     * @param node
     * @param contextInfo see PermissionsManager.checkPerm for more info
     * @return whether the permission is allowed
     */
    boolean checkPerm(EntityPlayer player, String node, Map<String, IContext> contextInfo);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(EntityPlayer player);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(TileEntity te);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(ILocation loc);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(Entity entity);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(World world);

    /**
     * @return A IContext signifying the Server as a whole.
     */
    IContext getGlobalContext();

    /**
     * At the very least, this method should return an anonymous instance of IContext.
     * This method should NEVER return null.
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(Object whoKnows);

    /**
     * This is where permissions are registered with their default value.
     * @param perms
     */
    void registerPermission(String node, RegisteredPermValue allow);
}
