package net.minecraftforge.permissions;

import net.minecraftforge.permissions.api.IPermissible;

/**
 * Represents a class which is to be notified when a {@link PermissionAttachment} is removed from a {@link IPermissible}
 */
public interface PermissionRemovedExecutor {
    /**
     * Called when a {@link PermissionAttachment} is removed from a {@link IPermissible}
     *
     * @param attachment Attachment which was removed
     */
    public void attachmentRemoved(PermissionAttachment attachment);
}
