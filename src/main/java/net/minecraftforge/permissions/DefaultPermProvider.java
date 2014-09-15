package net.minecraftforge.permissions;

import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class DefaultPermProvider implements IPermissionsProvider {

    static HashSet<String> operatorPermissions = new HashSet<String>();

    static HashSet<String> deniesPermissions = new HashSet<String>();

    @Override
    public void registerPermission(String permissionNode, RegisteredPermValue level)
    {
        switch (level)
        {
        case FALSE:
            deniesPermissions.add(permissionNode);
            break;
        case OP:
            operatorPermissions.add(permissionNode);
            break;
        default:
            break;
        }
    }

    @Override
    public boolean checkPermission(IContext contextInfo, String node)
    {
        if (deniesPermissions.contains(node))
        {
            return false;
        }
        else if (operatorPermissions.contains(node))
        {
            EntityPlayer player = contextInfo.getPlayer();
            return player == null ? false : MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile());
        }
        else
        {
            return true;
        }
    }
}
