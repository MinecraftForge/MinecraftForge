package net.minecraftforge.permissions;

import java.util.Map;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

public class DefaultPermHandler implements IPermissionHandler {

    private Map<String, DefaultValue> permMap = Maps.newHashMap();

    @Override
    public boolean checkPerm(String perm, EntityPlayer player)
    {
        DefaultValue result = permMap.get(perm);

        if (result == null) return false;

        switch (result)
        {
        case FALSE:
            return false;
        case TRUE:
            return true;
        case OP:
            return isOp(player.getGameProfile());
        }

        return false;
    }

    @Override
    public void regiserPerm(String perm, DefaultValue defaultValue)
    {
        permMap.put(perm, defaultValue);
    }

    @Override
    public boolean checkCommand(ICommand command, ICommandSender sender)
    {

        if (!(sender instanceof EntityPlayer))
        {
            return true; // Rcon, Console, or Command Blocks are always allowed
        }

        EntityPlayer player = (EntityPlayer) sender;

        if (command instanceof CommandBase)
        {
            CommandBase cmdBase = (CommandBase) command;
            DefaultValue result = permMap.get(cmdBase.getPermNode());

            if (result == null) return false;

            switch (result)
            {
            case TRUE:
                return true;
            case FALSE:
                return false;
            case OP:
                return isOp(player.getGameProfile());
            }

        }

        return false;
    }

    @Override
    public void registerCommand(ICommand command)
    {
        if (command instanceof CommandBase)
        {
            CommandBase cmdBase = (CommandBase) command;

            switch (cmdBase.getRequiredPermissionLevel())
            {
            case 0:
                permMap.put(cmdBase.getPermNode(), DefaultValue.TRUE);
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                permMap.put(cmdBase.getPermNode(), DefaultValue.OP);
                break;
            default:
                permMap.put(cmdBase.getPermNode(), DefaultValue.FALSE);
            }
        }
        else
        // People should extend CommandBase, but whatever
        {
            permMap.put("cmd." + command.getCommandName(), DefaultValue.OP);
        }
    }

    public static boolean isOp(GameProfile profile)
    {
        return MinecraftServer.getServer().getConfigurationManager().func_152596_g(profile);
    }

}
