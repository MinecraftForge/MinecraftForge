package net.minecraftforge.permissions.api;
import java.util.Set;

import net.minecraftforge.permissions.Permission;
import net.minecraftforge.permissions.PermissionAttachment;
import net.minecraftforge.permissions.PermissionAttachmentInfo;


import cpw.mods.fml.common.Mod;

/**
 * Represents an object that may be assigned net.minecraftforge.permissions
 */
public interface IPermissible extends IServerOperator {
    /**
     * Checks if this object contains an override for the specified permission, by fully qualified name
     *
     * @param name Name of the permission
     * @return true if the permission is set, otherwise false
     */
    public boolean isPermissionSet(String name);

    /**
     * Checks if this object contains an override for the specified {@link Permission}
     *
     * @param perm Permission to check
     * @return true if the permission is set, otherwise false
     */
    public boolean isPermissionSet(Permission perm);

    /**
     * Gets the value of the specified permission, if set.
     * <p>
     * If a permission override is not set on this object, the default value of the permission will be returned.
     *
     * @param name Name of the permission
     * @return Value of the permission
     */
    public boolean hasPermission(String name);

    /**
     * Gets the value of the specified permission, if set.
     * <p>
     * If a permission override is not set on this object, the default value of the permission will be returned
     *
     * @param perm Permission to get
     * @return Value of the permission
     */
    public boolean hasPermission(Permission perm);

    /**
     * Adds a new {@link PermissionAttachment} with a single permission by name and value
     *
     * @param mod Mod responsible for this attachment, may not be null or disabled
     * @param name Name of the permission to attach
     * @param value Value of the permission
     * @return The PermissionAttachment that was just created
     */
    public PermissionAttachment addAttachment(Mod mod, String name, boolean value);

    /**
     * Adds a new empty {@link PermissionAttachment} to this object
     *
     * @param mod Mod responsible for this attachment, may not be null or disabled
     * @return The PermissionAttachment that was just created
     */
    public PermissionAttachment addAttachment(Mod mod);

    /**
     * Temporarily adds a new {@link PermissionAttachment} with a single permission by name and value
     *
     * @param mod Mod responsible for this attachment, may not be null or disabled
     * @param name Name of the permission to attach
     * @param value Value of the permission
     * @param ticks Amount of ticks to automatically remove this attachment after
     * @return The PermissionAttachment that was just created
     */
    public PermissionAttachment addAttachment(Mod mod, String name, boolean value, int ticks);

    /**
     * Temporarily adds a new empty {@link PermissionAttachment} to this object
     *
     * @param plugin Plugin responsible for this attachment, may not be null or disabled
     * @param ticks Amount of ticks to automatically remove this attachment after
     * @return The PermissionAttachment that was just created
     */
    public PermissionAttachment addAttachment(Mod mod, int ticks);

    /**
     * Removes the given {@link PermissionAttachment} from this object
     *
     * @param attachment Attachment to remove
     * @throws IllegalArgumentException Thrown when the specified attachment isn't part of this object
     */
    public void removeAttachment(PermissionAttachment attachment);

    /**
     * Recalculates the net.minecraftforge.permissions for this object, if the attachments have changed values.
     * <p>
     * This should very rarely need to be called from a mod.
     */
    public void recalculatePermissions();

    /**
     * Gets a set containing all of the net.minecraftforge.permissions currently in effect by this object
     *
     * @return Set of currently effective net.minecraftforge.permissions
     */
    public Set<PermissionAttachmentInfo> getEffectivePermissions();

}
