package net.minecraftforge.permissions;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;


/**
 * An interface for permission frameworks to implement.
 * 
 * Framework authors:
 * The class implementing this interface will be called by the API to check permissions.
 * Do all necessary calls here. You can bounce them to helper classes if you want.
 * 
 */
public interface IPermissionsProvider
{

    /**
     * Checks a permission
     * 
     * @param context
     *            Context, where the permission is checked in.
     * @param permissionNode
     *            The permission node, that should be checked
     * @return Whether the permission is allowed
     */
    boolean checkPermission(IContext context, String permissionNode);

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
    void registerPermission(String permissionNode, RegisteredPermValue level);
}
