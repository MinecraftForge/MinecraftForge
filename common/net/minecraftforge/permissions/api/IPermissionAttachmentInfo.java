package net.minecraftforge.permissions.api;

import net.minecraftforge.permissions.PermissionAttachment;

public interface IPermissionAttachmentInfo 
{
    /**
     * Gets the permissible this is attached to
     *
     * @return IPermissible this permission is for
     */
    public IPermissible getPermissible();

    /**
     * Gets the permission being set
     *
     * @return Name of the permission
     */
    public String getPermission();

    /**
     * Gets the attachment providing this permission. This may be null for default
     * net.minecraftforge.permissions (usually parent net.minecraftforge.permissions).
     *
     * @return Attachment
     */
    public PermissionAttachment getAttachment();

    /**
     * Gets the value of this permission
     *
     * @return Value of the permission
     */
    public boolean getValue();

    /**
     * Gets the value of this permission based upon the passed in varArgs
     *
     * @param varArgs Additional arguments needed to check a permission.
     * @return Value of the permission
     */
    public boolean getValue(Object... varArgs);
}
