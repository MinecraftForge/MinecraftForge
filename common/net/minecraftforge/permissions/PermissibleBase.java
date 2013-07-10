package net.minecraftforge.permissions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.minecraftforge.permissions.api.IPermissible;
import net.minecraftforge.permissions.api.IServerOperator;


import cpw.mods.fml.common.Mod;


/**
 * Base IPermissible for use in any IPermissible object via proxy or extension
 */
public class PermissibleBase implements IPermissible {
    private IServerOperator opable = null;
    private IPermissible parent = this;
    private final List<PermissionAttachment> attachments = new LinkedList<PermissionAttachment>();
    private final Map<String, PermissionAttachmentInfo> permissions = new HashMap<String, PermissionAttachmentInfo>();

    public PermissibleBase()
    {
        this.opable = new ServerOperatorBase();
        
        recalculatePermissions();
    }
    
    public PermissibleBase(IServerOperator opable) {
        this.opable = opable;

        if (opable instanceof IPermissible) {
            this.parent = (IPermissible) opable;
        }

        recalculatePermissions();
    }

    public boolean isOp() {
        if (opable == null) {
            return false;
        } else {
            return opable.isOp();
        }
    }

    public void setOp(boolean value) {
        if (opable == null) {
            throw new UnsupportedOperationException("Cannot change op value as no IServerOperator is set");
        } else {
            opable.setOp(value);
        }
    }

    public boolean isPermissionSet(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        }

        return permissions.containsKey(name.toLowerCase());
    }

    public boolean isPermissionSet(Permission perm) {
        if (perm == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }

        return isPermissionSet(perm.getName());
    }

    public boolean hasPermission(String inName) {
        if (inName == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        }

        String name = inName.toLowerCase();

        if (isPermissionSet(name)) {
            return permissions.get(name).getValue();
        } else {
            Permission perm = ForgePermissionManager.getPermission(name);

            if (perm != null) {
                return perm.getDefault().getValue(isOp());
            } else {
                return Permission.DEFAULT_PERMISSION.getValue(isOp());
            }
        }
    }

    public boolean hasPermission(Permission perm) {
        if (perm == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }

        String name = perm.getName().toLowerCase();

        if (isPermissionSet(name)) {
            return permissions.get(name).getValue();
        }
        return perm.getDefault().getValue(isOp());
    }

    public PermissionAttachment addAttachment(Mod mod, String name, boolean value) {
        if (name == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        } else if (mod == null) {
            throw new IllegalArgumentException("Mod cannot be null");
        }
        PermissionAttachment result = addAttachment(mod);
        result.setPermission(name, value);

        recalculatePermissions();

        return result;
    }

    public PermissionAttachment addAttachment(Mod mod) {
        if (mod == null) {
            throw new IllegalArgumentException("Mod cannot be null");
        } 

        PermissionAttachment result = new PermissionAttachment(mod, parent);

        attachments.add(result);
        recalculatePermissions();

        return result;
    }

    public void removeAttachment(PermissionAttachment attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("Attachment cannot be null");
        }

        if (attachments.contains(attachment)) {
            attachments.remove(attachment);
            PermissionRemovedExecutor ex = attachment.getRemovalCallback();

            if (ex != null) {
                ex.attachmentRemoved(attachment);
            }

            recalculatePermissions();
        } else {
            throw new IllegalArgumentException("Given attachment is not part of IPermissible object " + parent);
        }
    }

    public void recalculatePermissions() {
        clearPermissions();
        Set<Permission> defaults = ForgePermissionManager.getDefaultPermissions(isOp());
        ForgePermissionManager.subscribeToDefaultPerms(isOp(), parent);

        for (Permission perm : defaults) {
            String name = perm.getName().toLowerCase();
            permissions.put(name, new PermissionAttachmentInfo(parent, name, null, true));
            ForgePermissionManager.subscribeToPermission(name, parent);
            calculateChildPermissions(perm.getChildren(), false, null);
        }

        for (PermissionAttachment attachment : attachments) {
            calculateChildPermissions(attachment.getPermissions(), false, attachment);
        }
    }

    public synchronized void clearPermissions() {
        Set<String> perms = permissions.keySet();

        for (String name : perms) {
            ForgePermissionManager.unsubscribeFromPermission(name, parent);
        }

        ForgePermissionManager.unsubscribeFromDefaultPerms(false, parent);
        ForgePermissionManager.unsubscribeFromDefaultPerms(true, parent);

        permissions.clear();
    }

    private void calculateChildPermissions(Map<String, Boolean> children, boolean invert, PermissionAttachment attachment) {
        Set<String> keys = children.keySet();

        for (String name : keys) {
            Permission perm = ForgePermissionManager.getPermission(name);
            boolean value = children.get(name) ^ invert;
            String lname = name.toLowerCase();

            permissions.put(lname, new PermissionAttachmentInfo(parent, lname, attachment, value));
            ForgePermissionManager.subscribeToPermission(name, parent);

            if (perm != null) {
                calculateChildPermissions(perm.getChildren(), !value, attachment);
            }
        }
    }

    public PermissionAttachment addAttachment(Mod Mod, String name, boolean value, int ticks) {
        if (name == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        } else if (Mod == null) {
            throw new IllegalArgumentException("Mod cannot be null");
        }

        PermissionAttachment result = addAttachment(Mod, ticks);

        if (result != null) {
            result.setPermission(name, value);
        }

        return result;
    }

    public PermissionAttachment addAttachment(Mod Mod, int ticks) {
        if (Mod == null) {
            throw new IllegalArgumentException("Mod cannot be null");
        } 

        PermissionAttachment result = addAttachment(Mod);
        return result;
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return new HashSet<PermissionAttachmentInfo>(permissions.values());
    }

    private class RemoveAttachmentRunnable implements Runnable {
        private PermissionAttachment attachment;

        public RemoveAttachmentRunnable(PermissionAttachment attachment) {
            this.attachment = attachment;
        }

        public void run() {
            attachment.remove();
        }
    }
}