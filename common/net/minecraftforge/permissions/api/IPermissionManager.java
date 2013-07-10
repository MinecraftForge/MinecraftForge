package net.minecraftforge.permissions.api;

import java.util.Set;

import net.minecraftforge.permissions.Permission;


public interface IPermissionManager
{
    /**
     * Gets a {@link Permission} from its fully qualified name
     *
     * @param name Name of the permission
     * @return Permission, or null if none
     */
    public Permission getPermission(String name);

    /**
     * Adds a {@link Permission} to this plugin manager.
     * <p>
     * If a permission is already defined with the given name of the new permission,
     * an exception will be thrown.
     *
     * @param perm Permission to add
     * @throws IllegalArgumentException Thrown when a permission with the same name already exists
     */
    public void addPermission(Permission perm);

    /**
     * Removes a {@link Permission} registration from this plugin manager.
     * <p>
     * If the specified permission does not exist in this plugin manager, nothing will happen.
     * <p>
     * Removing a permission registration will <b>not</b> remove the permission from any {@link IPermissible}s that have it.
     *
     * @param perm Permission to remove
     */
    public void removePermission(Permission perm);

    /**
     * Removes a {@link Permission} registration from this plugin manager.
     * <p>
     * If the specified permission does not exist in this plugin manager, nothing will happen.
     * <p>
     * Removing a permission registration will <b>not</b> remove the permission from any {@link IPermissible}s that have it.
     *
     * @param name Permission to remove
     */
    public void removePermission(String name);

    /**
     * Gets the default net.minecraftforge.permissions for the given op status
     *
     * @param op Which set of default net.minecraftforge.permissions to get
     * @return The default net.minecraftforge.permissions
     */
    public Set<Permission> getDefaultPermissions(boolean op);

    /**
     * Recalculates the defaults for the given {@link Permission}.
     * <p>
     * This will have no effect if the specified permission is not registered here.
     *
     * @param perm Permission to recalculate
     */
    public void recalculatePermissionDefaults(Permission perm);

    /**
     * Subscribes the given IPermissible for information about the requested Permission, by name.
     * <p>
     * If the specified Permission changes in any form, the IPermissible will be asked to recalculate.
     *
     * @param permission Permission to subscribe to
     * @param permissible IPermissible subscribing
     */
    public void subscribeToPermission(String permission, IPermissible permissible);

    /**
     * Unsubscribes the given IPermissible for information about the requested Permission, by name.
     *
     * @param permission Permission to unsubscribe from
     * @param permissible IPermissible subscribing
     */
    public void unsubscribeFromPermission(String permission, IPermissible permissible);

    /**
     * Gets a set containing all subscribed {@link IPermissible}s to the given permission, by name
     *
     * @param permission Permission to query for
     * @return Set containing all subscribed net.minecraftforge.permissions
     */
    public Set<IPermissible> getPermissionSubscriptions(String permission);

    /**
     * Subscribes to the given Default net.minecraftforge.permissions by operator status
     * <p>
     * If the specified defaults change in any form, the IPermissible will be asked to recalculate.
     *
     * @param op Default list to subscribe to
     * @param permissible IPermissible subscribing
     */
    public void subscribeToDefaultPerms(boolean op, IPermissible permissible);

    /**
     * Unsubscribes from the given Default net.minecraftforge.permissions by operator status
     *
     * @param op Default list to unsubscribe from
     * @param permissible IPermissible subscribing
     */
    public void unsubscribeFromDefaultPerms(boolean op, IPermissible permissible);

    /**
     * Gets a set containing all subscribed {@link IPermissible}s to the given default list, by op status
     *
     * @param op Default list to query for
     * @return Set containing all subscribed net.minecraftforge.permissions
     */
    public Set<IPermissible> getDefaultPermSubscriptions(boolean op);

    /**
     * Gets a set of all registered net.minecraftforge.permissions.
     * <p>
     * This set is a copy and will not be modified live.
     *
     * @return Set containing all current registered net.minecraftforge.permissions
     */
    public Set<Permission> getPermissions();


}
