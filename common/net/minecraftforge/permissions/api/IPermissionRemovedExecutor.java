package net.minecraftforge.permissions.api;

import net.minecraftforge.permissions.PermissionAttachment;

/**
 * Represents a class which is to be notified when a {@link PermissionAttachment} is removed from a {@link IPermissible}
 */
public interface IPermissionRemovedExecutor {
    /**
     * Called when a {@link PermissionAttachment} is removed from a {@link IPermissible}
     *
     * @param attachment Attachment which was removed
     */
    public void attachmentRemoved(PermissionAttachment attachment);
}
