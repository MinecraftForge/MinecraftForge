package net.minecraftforge.permissions;

import net.minecraftforge.permissions.api.IPermissible;

/**
 * Holds information on a permission and which {@link PermissionAttachment} provides it
 */
public class PermissionAttachmentInfo {
    private final IPermissible permissible;
    private final String permission;
    private final PermissionAttachment attachment;
    private final boolean value;

    public PermissionAttachmentInfo(IPermissible permissible, String permission, PermissionAttachment attachment, boolean value) {
        if (permissible == null) {
            throw new IllegalArgumentException("IPermissible may not be null");
        } else if (permission == null) {
            throw new IllegalArgumentException("Permissions may not be null");
        }

        this.permissible = permissible;
        this.permission = permission;
        this.attachment = attachment;
        this.value = value;
    }

    /**
     * Gets the permissible this is attached to
     *
     * @return IPermissible this permission is for
     */
    public IPermissible getPermissible() {
        return permissible;
    }

    /**
     * Gets the permission being set
     *
     * @return Name of the permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Gets the attachment providing this permission. This may be null for default
     * net.minecraftforge.permissions (usually parent net.minecraftforge.permissions).
     *
     * @return Attachment
     */
    public PermissionAttachment getAttachment() {
        return attachment;
    }

    /**
     * Gets the value of this permission
     *
     * @return Value of the permission
     */
    public boolean getValue() {
        return value;
    }
}