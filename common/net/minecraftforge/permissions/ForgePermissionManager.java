package net.minecraftforge.permissions;

import java.util.Set;

import net.minecraftforge.permissions.api.IPermissible;
import net.minecraftforge.permissions.api.IPermissionManager;
import net.minecraftforge.permissions.implementation.DefaultPermissionManager;
import net.minecraftforge.permissions.implementation.PermissibleBase;


public class ForgePermissionManager //implements IPermissionManager
{
    public static IPermissionManager manager = new DefaultPermissionManager();
    
//    public static Class<? extends IPermissible> genericPermissible = PermissibleBase.class;

    public static boolean setPermissionManager(IPermissionManager replaceManager)
    {
        manager = replaceManager;
        return true;
    }
    
    public static IPermissionManager getPermissionManager()
    {
        return manager;
    }
    
    public static Permission getPermission(String name)
    {
        return manager.getPermission(name);
    }

    public static void addPermission(Permission perm)
    {
        manager.addPermission(perm);
    }

    public static void removePermission(Permission perm)
    {
        manager.removePermission(perm);
    }

    public static void removePermission(String name)
    {
        manager.removePermission(name);
    }

    public static Set<Permission> getDefaultPermissions(boolean op)
    {
        return manager.getDefaultPermissions(op);
    }

    public static void recalculatePermissionDefaults(Permission perm)
    {
        manager.recalculatePermissionDefaults(perm);
    }

    public static void subscribeToPermission(String permission,
                                      IPermissible permissible)
    {
        manager.subscribeToPermission(permission, permissible);
    }

    public static void unsubscribeFromPermission(String permission,
                                          IPermissible permissible)
    {
        manager.unsubscribeFromPermission(permission, permissible);
    }


    public static Set<IPermissible> getPermissionSubscriptions(String permission)
    {
        return manager.getPermissionSubscriptions(permission);
    }


    public static void subscribeToDefaultPerms(boolean op, IPermissible permissible)
    {
       manager.subscribeToDefaultPerms(op, permissible);
    }


    public static void unsubscribeFromDefaultPerms(boolean op, IPermissible permissible)
    {
        manager.unsubscribeFromDefaultPerms(op, permissible);
    }


    public static Set<IPermissible> getDefaultPermSubscriptions(boolean op)
    {
        return manager.getDefaultPermSubscriptions(op);
    }


    public static Set<Permission> getPermissions()
    {
        return manager.getPermissions();
    }


//    @Override
//    public static Permission getPermission(String name)
//    {
//        return manager.getPermission(name);
//    }
//
//    @Override
//    public void addPermission(Permission perm)
//    {
//        manager.addPermission(perm);
//    }
//
//    @Override
//    public void removePermission(Permission perm)
//    {
//        manager.removePermission(perm);
//    }
//
//    @Override
//    public void removePermission(String name)
//    {
//        manager.removePermission(name);
//    }
//
//    @Override
//    public Set<Permission> getDefaultPermissions(boolean op)
//    {
//        return manager.getDefaultPermissions(op);
//    }
//
//    @Override
//    public void recalculatePermissionDefaults(Permission perm)
//    {
//        manager.recalculatePermissionDefaults(perm);
//    }
//
//    @Override
//    public void subscribeToPermission(String permission,
//                                      IPermissible permissible)
//    {
//        manager.subscribeToPermission(permission, permissible);
//    }
//
//    @Override
//    public void unsubscribeFromPermission(String permission,
//                                          IPermissible permissible)
//    {
//        manager.unsubscribeFromPermission(permission, permissible);
//    }
//
//    @Override
//    public Set<IPermissible> getPermissionSubscriptions(String permission)
//    {
//        return manager.getPermissionSubscriptions(permission);
//    }
//
//    @Override
//    public void subscribeToDefaultPerms(boolean op, IPermissible permissible)
//    {
//       manager.subscribeToDefaultPerms(op, permissible);
//    }
//
//    @Override
//    public void unsubscribeFromDefaultPerms(boolean op, IPermissible permissible)
//    {
//        manager.unsubscribeFromDefaultPerms(op, permissible);
//    }
//
//    @Override
//    public Set<IPermissible> getDefaultPermSubscriptions(boolean op)
//    {
//        return manager.getDefaultPermSubscriptions(op);
//    }
//
//    @Override
//    public Set<Permission> getPermissions()
//    {
//        return manager.getPermissions();
//    }

    
    
}
