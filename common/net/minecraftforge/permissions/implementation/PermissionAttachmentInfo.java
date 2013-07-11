package net.minecraftforge.permissions.implementation;

import net.minecraftforge.permissions.PermissionAttachment;
import net.minecraftforge.permissions.api.IPermissible;
import net.minecraftforge.permissions.api.IPermissionAttachmentInfo;

/**
 * Holds information on a permission and which {@link PermissionAttachment} provides it
 */
public class PermissionAttachmentInfo implements IPermissionAttachmentInfo {
    protected final IPermissible permissible;
    protected final String permission;
    protected final PermissionAttachment attachment;
    public final boolean value;

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


    public IPermissible getPermissible() {
        return permissible;
    }


    public String getPermission() {
        return permission;
    }


    public PermissionAttachment getAttachment() {
        return attachment;
    }


    public boolean getValue() {
        return value;
    }
    
    public boolean getValue(Object... varArgs)
    {
        return value;
    }
}