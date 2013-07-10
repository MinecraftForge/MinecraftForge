package net.minecraftforge.permissions;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraftforge.permissions.api.IPermissible;


import cpw.mods.fml.common.Mod;

/**
 * Holds information about a permission attachment on a {@link IPermissible} object
 */
public class PermissionAttachment {
    private PermissionRemovedExecutor removed;
    private final Map<String, Boolean> permissions = new LinkedHashMap<String, Boolean>();
    private final IPermissible permissible;
    private final Mod mod;

    public PermissionAttachment(Mod mod, IPermissible Permissible) {
        if (mod == null) {
            throw new IllegalArgumentException("mod cannot be null");
        }// else if (!mod.isEnabled()) {
        //    throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
        //}

        this.permissible = Permissible;
        this.mod = mod;
    }

    /**
     * Gets the mod responsible for this attachment
     *
     * @return Mod responsible for this permission attachment
     */
    public Mod getPlugin() {
        return mod;
    }

    /**
     * Sets an object to be called for when this attachment is removed from a {@link IPermissible}. May be null.
     *
     * @param ex Object to be called when this is removed
     */
    public void setRemovalCallback(PermissionRemovedExecutor ex) {
        removed = ex;
    }

    /**
     * Gets the class that was previously set to be called when this attachment was removed from a {@link IPermissible}. May be null.
     *
     * @return Object to be called when this is removed
     */
    public PermissionRemovedExecutor getRemovalCallback() {
        return removed;
    }

    /**
     * Gets the IPermissible that this is attached to
     *
     * @return IPermissible containing this attachment
     */
    public IPermissible getPermissible() {
        return permissible;
    }

    /**
     * Gets a copy of all set net.minecraftforge.permissions and values contained within this attachment.
     * <p>
     * This map may be modified but will not affect the attachment, as it is a copy.
     *
     * @return Copy of all net.minecraftforge.permissions and values expressed by this attachment
     */
    public Map<String, Boolean> getPermissions() {
        return new LinkedHashMap<String, Boolean>(permissions);
    }

    /**
     * Sets a permission to the given value, by its fully qualified name
     *
     * @param name Name of the permission
     * @param value New value of the permission
     */
    public void setPermission(String name, boolean value) {
        permissions.put(name.toLowerCase(), value);
        permissible.recalculatePermissions();
    }

    /**
     * Sets a permission to the given value
     *
     * @param perm Permission to set
     * @param value New value of the permission
     */
    public void setPermission(Permission perm, boolean value) {
        setPermission(perm.getName(), value);
    }

    /**
     * Removes the specified permission from this attachment.
     * <p>
     * If the permission does not exist in this attachment, nothing will happen.
     *
     * @param name Name of the permission to remove
     */
    public void unsetPermission(String name) {
        permissions.remove(name.toLowerCase());
        permissible.recalculatePermissions();
    }

    /**
     * Removes the specified permission from this attachment.
     * <p>
     * If the permission does not exist in this attachment, nothing will happen.
     *
     * @param perm Permission to remove
     */
    public void unsetPermission(Permission perm) {
        unsetPermission(perm.getName());
    }

    /**
     * Removes this attachment from its registered {@link IPermissible}
     *
     * @return true if the permissible was removed successfully, false if it did not exist
     */
    public boolean remove() {
        try {
            permissible.removeAttachment(this);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}