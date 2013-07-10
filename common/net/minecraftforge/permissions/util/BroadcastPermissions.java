package net.minecraftforge.permissions.util;

import net.minecraftforge.permissions.Permission;
import net.minecraftforge.permissions.PermissionDefault;

public final class BroadcastPermissions {
    private static final String ROOT = "forge.broadcast";
    private static final String PREFIX = ROOT + ".";

    private BroadcastPermissions() {}

    public static Permission registerPermissions(Permission parent) {
        Permission broadcasts = DefaultPermissions.registerPermission(ROOT, "Allows the user to receive all broadcast messages", parent);

        DefaultPermissions.registerPermission(PREFIX + "admin", "Allows the user to receive administrative broadcasts", PermissionDefault.OP, broadcasts);
        DefaultPermissions.registerPermission(PREFIX + "user", "Allows the user to receive user broadcasts", PermissionDefault.TRUE, broadcasts);

        broadcasts.recalculatePermissibles();

        return broadcasts;
    }


}
